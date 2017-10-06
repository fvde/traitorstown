package com.individual.thinking.traitorstown.model;

import com.individual.thinking.traitorstown.game.CardService;
import com.individual.thinking.traitorstown.game.rules.Rules;
import com.individual.thinking.traitorstown.model.effects.Effect;
import com.individual.thinking.traitorstown.model.effects.SpecialEffectType;
import com.individual.thinking.traitorstown.model.exceptions.InactiveGameException;
import com.individual.thinking.traitorstown.model.exceptions.PlayerDoesNotHaveCardException;
import com.individual.thinking.traitorstown.model.exceptions.RuleSetViolationException;
import com.individual.thinking.traitorstown.model.exceptions.TargetPlayerNotInGameException;
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

    @Transient
    private List<PostTurnEffect> postTurnEffects = new ArrayList<>();

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

    public void end(Role role){
        status = GameStatus.FINISHED;
        winner = role;
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

        if (target.isDead()){
            throw new RuleSetViolationException("Can not target dead players!");
        }

        getCurrentTurn().playCard(card, player, target);

        if (!player.isAi()){
            getCurrentTurn().end();
        }
    }

    public void startNextTurn(){

        finishPreviousTurn();

        if (isInactive() || getLivingHumanPlayers().size() == 0){
            cancel();
        } else if (onlyCitizensAlive()){
            end(Role.CITIZEN);
        } else if (onlyTraitorsAlive()){
            end(Role.TRAITOR);
        } else {
            turns.add(getCurrentTurn().startNext());
            players.forEach(Player::startTurn);
        }
//
//        if (Day.isElectionDay(next.getCounter()) && players.stream().filter(Player::isCandidate).count() > 0){
//            getLivingPlayers().stream().forEach(player -> player.addCard(CardService.Cards.get(CardType.VOTE)));
//        }
//
//        if (Day.isDayAfterElections(next.getCounter())){
//            electMayor();
//        }
    }

    private void finishPreviousTurn() {
        /**
         * Order matters here
         * 1) Temporary cards are discarded
         * 2) Effects are applied for all players
         * 3) Effects that are no longer active are removed
         * 4) Apply post turn effects that are created by this turn
         */
        players.forEach(Player::discardSingleTurnCards);
        players.forEach(p -> p.applyEffects(this));
        players.forEach(Player::removeInactiveEffects);
        postTurnEffects.forEach(e -> e.getTarget().addEffect(e.getEffect(), e.getOrigin()));
    }

    private void electMayor(){
        Player electedPlayer = Collections.max(players, Comparator.comparingLong(Player::getVotes));

        if (electedPlayer.getVotes() > 0){
            // TODO add mayor cards
            electedPlayer.addEffect(CardService.Effects.get(SpecialEffectType.MAYOR), electedPlayer);
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

    public List<Player> getLivingHumanPlayers(){
        return getLivingPlayers().stream().filter(Player::isHuman).collect(Collectors.toList());
    }

    public List<Player> getLivingPlayers(){
        return players.stream().filter(Player::isAlive).collect(Collectors.toList());
    }

    public boolean onlyTraitorsAlive(){
        return getLivingPlayers().stream().filter(Player::isTraitor).collect(Collectors.toList()).size() == getLivingPlayers().size();
    }

    public boolean onlyCitizensAlive(){
        return getLivingPlayers().stream().filter(Player::isCitizen).collect(Collectors.toList()).size() == getLivingPlayers().size();
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

    public void addPostTurnEffect(Effect effect, Player origin, Player target) {
        postTurnEffects.add(PostTurnEffect.builder().effect(effect).origin(origin).target(target).build());
    }

    @Tolerate
    public Game() {}
}
