package com.individual.thinking.traitorstown.model;

import com.individual.thinking.traitorstown.game.CardService;
import com.individual.thinking.traitorstown.game.rules.Rules;
import com.individual.thinking.traitorstown.model.effects.SpecialEffectType;
import com.individual.thinking.traitorstown.model.exceptions.*;
import lombok.*;
import lombok.experimental.Tolerate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Builder
@Getter
@ToString
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "game_id")
    @OrderBy("id DESC")
    @NonNull
    private List<Player> players = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinColumn(name = "game_id")
    @OrderBy("counter DESC")
    @NonNull
    private List<Turn> turns = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Setter
    @NonNull
    private GameStatus status = GameStatus.OPEN;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role winner = Role.NONE;

    @Builder.Default
    private Integer desiredNumberOfHumanPlayers = 1;

    public void addPlayer(Player player){
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void start() throws RuleSetViolationException {
        setStatus(GameStatus.PLAYING);
        turns.add(Turn.builder()
                .counter(0)
                .finishedPlayers(Collections.emptyList())
                .humanPlayers(players.size() - getAIPlayers().size())
                .startedAt(new Date())
                .build());
        Rules.getRolesForPlayers(players).forEach((role, players) ->
                players.forEach(p -> p.startGameWithRole(role)));
    }

    public void end(){
        status = GameStatus.FINISHED;
        Optional<Player> mayor = getMayor();
        if (mayor.isPresent()){
            winner = mayor.get().getRole();
        }
    }

    public void cancel(){
        status = GameStatus.CANCELLED;
    }

    public void playCard(Player player, Player target, Card card) throws PlayerDoesNotHaveCardException, InactiveGameException, TargetPlayerNotInGameException, RuleSetViolationException {

        if (!status.equals(GameStatus.PLAYING)){
            throw new InactiveGameException("This game has not started or is already over!");
        }

        if (!players.contains(target)){
            throw new TargetPlayerNotInGameException("The player you are targeting is not in your game!");
        }

        getCurrentTurn().playCard(card, player, target);

        if (!player.isAi()){
            getCurrentTurn().end();
        }
    }

    public void startNextTurn(){
        if (isInactive()){
            cancel();
        }

        /**
         * Order matters here
         * 1) Temporary cards are discarded
         * 2) Effects are applied for all players
         * 3) Effects that are no longer active are removed
         */
        players.forEach(Player::discardSingleTurnCards);
        players.forEach((Player p) -> p.applyCardEffects(this));
        players.forEach(Player::removeInactiveEffects);

        Turn next = getCurrentTurn().startNext();

        if (Day.isElectionDay(next.getCounter()) && players.stream().filter(Player::isCandidate).count() > 0){
            players.stream().forEach(player -> player.addCard(CardService.Cards.get(CardType.VOTE)));
        }

        if (Day.isDayAfterElections(next.getCounter())){
            electMayor();
        }

        if (getMayor().isPresent() && Day.isElectionDay(next.getCounter())) {
            end();
        } else {
            turns.add(next);
            players.forEach(Player::drawCard);
        }
    }

    private void electMayor(){
        Player electedPlayer = Collections.max(players, Comparator.comparingLong(Player::getVotes));

        if (electedPlayer.getVotes() > 0){
            // TODO add mayor cards
            electedPlayer.addEffect(CardService.Effects.get(SpecialEffectType.MAYOR));
        }
        // clean up candidacies
        players.stream().forEach(Player::clearCandidacy);
    }

    private List<Player> getReadyPlayers(){
        return players.stream()
                .filter(Player::isReady)
                .filter(p -> !p.isAi())
                .collect(Collectors.toList());
    }

    public List<Player> getAIPlayers(){
        return players.stream().filter(Player::isAi).collect(Collectors.toList());
    }

    public Player getPlayer(Long id){
        return players.stream().filter(p -> p.getId().equals(id)).findFirst().get();
    }

    public Optional<Player> getMayor(){
        return players.stream().filter(Player::isMayor).findFirst();
    }

    public boolean isReadyToBeStarted(){
        return getReadyPlayers().size() >= desiredNumberOfHumanPlayers &&
                status.equals(GameStatus.OPEN);
    }

    public Turn getCurrentTurn(){
        return turns.get(0);
    }

    public int getCurrentTurnCounter(){
        return getCurrentTurn().getCounter();
    }

    public boolean isCurrentTurn(Integer turn){
        return getCurrentTurnCounter() == turn.intValue();
    }

    public boolean isInactive(){
        return getCurrentTurn().allHumanPlayersInactive();
    }

    @Tolerate
    public Game() {}
}
