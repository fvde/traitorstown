package com.individual.thinking.traitorstown.model;

import com.individual.thinking.traitorstown.game.CardService;
import com.individual.thinking.traitorstown.game.rules.Rules;
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

    @OneToMany(fetch = FetchType.EAGER)
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
        turns.add(Turn.builder().counter(0).build());
        Rules.getRolesForPlayers(players).forEach((role, players) ->
                players.forEach(p -> p.startGameWithRole(role)));
    }

    public void end(){
        status = GameStatus.FINISHED;
        winner = getMayor().get().getRole();
    }

    public void playCard(Player player, Player target, Card card) throws NotCurrentTurnException, PlayerDoesNotHaveCardException, PlayedAlreadyPlayedCardThisTurnException, PlayerMayNotPlayThisCardException, InactiveGameException, TargetPlayerNotInGameException {
        Turn turn = getCurrentTurn().get();

        if (!status.equals(GameStatus.PLAYING)){
            throw new InactiveGameException("This game has not started or is already over!");
        }

        if (!players.contains(target)){
            throw new TargetPlayerNotInGameException("The player you are targeting is not in your game!");
        }

        turn.playCard(card, player, target);
    }

    public void startNextTurn(){
        players.forEach(Player::endTurn);
        Turn next = getCurrentTurn().get().end();

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
            electedPlayer.addEffect(CardService.Effects.get(EffectType.MAYOR));
        }
        // clean up candidacies
        players.stream().forEach(Player::clearCandidacy);
    }

    //TODO add timeout
    public boolean isTurnOver(){
        return getCurrentTurn().get().getFinishedPlayers().size() == getPlayers().size() - getAIPlayers().size();
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

    public Optional<Turn> getCurrentTurn(){
        return turns.isEmpty() ? Optional.empty() : Optional.of(turns.get(0));
    }

    public int getTurn(){
        Optional<Turn> turn = getCurrentTurn();
        return turn.isPresent() ? turn.get().getCounter() : 0;
    }

    public boolean isCurrentTurn(Integer turn){
        return getCurrentTurn().isPresent() && getCurrentTurn().get().getCounter().equals(turn);
    }

    @Tolerate
    public Game() {}
}
