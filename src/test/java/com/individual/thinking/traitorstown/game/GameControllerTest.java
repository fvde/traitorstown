package com.individual.thinking.traitorstown.game;

import com.google.common.collect.ImmutableMap;
import com.individual.thinking.traitorstown.MockMvcBase;
import com.individual.thinking.traitorstown.model.*;
import com.individual.thinking.traitorstown.model.effects.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.individual.thinking.traitorstown.TestUtils.readFileFromResource;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GameControllerTest extends MockMvcBase{

    @MockBean
    private GameService gameService;

    @MockBean
    private PlayerService playerService;


    private Long validUserId = 62532532L;
    private Long validGameId = 97217217L;
    private String validToken = "1srioisp5mb07drbbejqni519eib2pti";

    private final Effect removeGoldEffect = ResourceEffect.builder()
            .effectTargetType(EffectTargetType.TARGET)
            .operator(EffectOperator.REMOVE)
            .resourceType(ResourceType.GOLD)
            .amount(10)
            .duration(1)
            .build();

    private final List<Card> cards = Arrays.asList(
            Card.builder().id(1L).cardType(CardType.HONEST_TRADE).name("Trade").description("get gold from people!").effects(Arrays.asList(removeGoldEffect)).build(),
            Card.builder().id(2L).cardType(CardType.RUN_FOR_MAYOR).name("Run for mayor").description("Apply to become mayor!").effects(Arrays.asList(ResourceEffect.builder().effectTargetType(EffectTargetType.TARGET).operator(EffectOperator.REMOVE).resourceType(ResourceType.REPUTATION).amount(20).duration(1).build())).build());

    private final Player opponent = Player.builder()
            .id(validUserId)
            .gameId(validGameId)
            .ready(true)
            .handCards(cards)
            .resources(ImmutableMap.of(
                    ResourceType.GOLD, 12,
                    ResourceType.REPUTATION, -15,
                    ResourceType.STOLEN_GOLD, 31
            ))
            .activeEffects(Collections.emptyList())
            .build();

    private final Player player = Player.builder()
            .id(validUserId)
            .gameId(validGameId)
            .ready(true)
            .handCards(cards)
            .resources(ImmutableMap.of(
                    ResourceType.GOLD, 17,
                    ResourceType.REPUTATION, 5,
                    ResourceType.STOLEN_GOLD, 0
            ))
            .activeEffects(Collections.singletonList(EffectActive.builder().effect(removeGoldEffect).origin(opponent).target(opponent).remainingTurns(5).build()))
            .build();

    private final Game game = Game.builder()
            .players(singletonList(player))
            .status(GameStatus.PLAYING)
            .turns(Arrays.asList(
                    Turn.builder().counter(1).humanPlayers(3).startedAt(new Date()).build(),
                    Turn.builder().counter(2).humanPlayers(4).startedAt(new Date()).build()))
            .build();

    @Test
    public void createNewGame() throws Exception {
        when(gameService.createNewGame()).thenReturn(game);
        this.mockMvc.perform(post("/games")
                .with(authorizedPlayer(player))
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getGamesByStatus() throws Exception {
        when(gameService.getGamesByStatus(any())).thenReturn(singletonList(game));

        this.mockMvc.perform(get("/games")
                .with(authorizedPlayer(player))
                .header("token", validToken)
                .param("status", "OPEN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getPlayersGame() throws Exception {
        when(gameService.getGameByPlayerId(validUserId)).thenReturn(game);

        this.mockMvc.perform(get("/players/{playerId}/games", validUserId)
                .with(authorizedPlayer(player))
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getGame() throws Exception {
        when(gameService.getGameById(validGameId)).thenReturn(game);

        this.mockMvc.perform(get("/games/{gameId}", validGameId)
                .with(authorizedPlayer(player))
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void addPlayer() throws Exception {
        when(gameService.addPlayerToGame(anyLong(), any())).thenReturn(game);

        this.mockMvc.perform(post("/games/{gameId}/players", validGameId)
                .with(authorizedPlayer(player))
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(readFileFromResource("player.json")))
                .andExpect(status().isOk());
    }

    @Test
    public void removePlayer() throws Exception {
        when(gameService.removePlayerFromGame(anyLong(), any())).thenReturn(game);

        this.mockMvc.perform(delete("/games/{gameId}/players/{playerId}", validGameId, validUserId)
                .with(authorizedPlayer(player))
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void setPlayerReady() throws Exception {
        when(gameService.setPlayerReady(anyLong(), any(), any())).thenReturn(game);

        this.mockMvc.perform(put("/games/{gameId}/players/{playerId}", validGameId, validUserId)
                .with(authorizedPlayer(player))
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(readFileFromResource("player_ready.json")))
                .andExpect(status().isOk());
    }

    @Test
    public void getPlayerCards() throws Exception {
        when(playerService.getPlayerCards(anyLong())).thenReturn(cards);

        this.mockMvc.perform(get("/games/{gameId}/players/{playerId}/cards", validGameId, validUserId)
                .with(authorizedPlayer(player))
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void getTurn() throws Exception {
        when(gameService.getTurnByGameIdAndCounter(anyLong(), anyInt())).thenReturn(Turn.builder().counter(1).humanPlayers(7).startedAt(new Date()).build());

        this.mockMvc.perform(get("/games/{gameId}/turns/{turnCounter}", validGameId, 1).header("token", validToken)
                .with(authorizedPlayer(player))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void playCard() throws Exception {
        doNothing().when(gameService).playCard(anyLong(), anyInt(), anyLong(), anyLong(), anyLong());

        this.mockMvc.perform(post("/games/{gameId}/turns/{turnCounter}/cards", validGameId, 1)
                .with(authorizedPlayer(player))
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(readFileFromResource("card.json")))
                .andExpect(status().isOk());
    }
}