package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.Configuration;
import com.individual.thinking.traitorstown.authorization.AuthenticationInterceptor;
import com.individual.thinking.traitorstown.game.exceptions.GameNotFoundException;
import com.individual.thinking.traitorstown.game.exceptions.PlayerNotInGameException;
import com.individual.thinking.traitorstown.model.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static com.individual.thinking.traitorstown.TestUtils.readFileFromResource;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class GameControllerTest {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @MockBean
    private AuthenticationInterceptor authenticationInterceptor;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private Long validUserId = 62532532L;
    private Long validGameId = 97217217L;
    private String validToken = "1srioisp5mb07drbbejqni519eib2pti";


    private ResponseFieldsSnippet gameSnippet = responseFields(
            fieldWithPath("id").description("Game id"),
            fieldWithPath("status").description("Game status [OPEN = 0, PLAYING = 1, FINISHED = 2]"),
            fieldWithPath("turn").description("Current turn"),
            fieldWithPath("players").description("Players in the game"))
            .andWithPrefix("players[].",
                fieldWithPath("id").description("Id of the player"),
                fieldWithPath("ready").description("Indicates whether player is ready to play"),
                fieldWithPath("resources").description("Players resources"))
                .andWithPrefix("players[].resources[].",
                        fieldWithPath("type").description("The type of resource. [GOLD = 0, REPUTATION = 1, CARD = 2]"),
                        fieldWithPath("amount").description("The amount of the specified resource")
    );

    private ResponseFieldsSnippet multipleGameSnippet = responseFields(
            fieldWithPath("[].id").description("Game id"),
            fieldWithPath("[].status").description("Game status [OPEN = 0, PLAYING = 1, FINISHED = 2]"),
            fieldWithPath("[].turn").description("Current turn"),
            fieldWithPath("[].players").description("Players in the game"))
            .andWithPrefix("[].players[].",
                    fieldWithPath("id").description("Id of the player"),
                    fieldWithPath("ready").description("Indicates whether player is ready to play")
            );

    private final List<Card> cards = Arrays.asList(
            Card.builder().id(1L).name("Trade").description("get gold from people!").effects(Arrays.asList(Effect.builder().type(EffectType.REMOVE).targetType(Resource.GOLD).amount(10).duration(1).build())).build(),
            Card.builder().id(2L).name("Fight").description("Hit people!").effects(Arrays.asList(Effect.builder().type(EffectType.REMOVE).targetType(Resource.REPUTATION).amount(20).duration(1).build())).build());

    private final Player player = Player.builder()
            .id(validUserId)
            .gameId(validGameId)
            .ready(true)
            .handCards(cards)
            .gold(20)
            .reputation(15)
            .build();

    private final Game game = Game.builder()
            .players(singletonList(player))
            .status(GameStatus.PLAYING)
            .turns(Arrays.asList(
                    Turn.builder().counter(1).build(),
                    Turn.builder().counter(2).build()))
            .build();

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(this.restDocumentation))
                .build();

        when(authenticationInterceptor.preHandle(any(), any(), any())).thenAnswer(invocation -> {
            MockHttpServletRequest request = (MockHttpServletRequest) invocation.getArguments()[0];
            request.setAttribute(Configuration.AUTHENTICATION_KEY, player);
            return true;
        });
    }

    @Test
    public void createNewGame() throws Exception {
        when(gameService.createNewGame()).thenReturn(game);
        this.mockMvc.perform(post("/games")
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("post-games",
                        requestHeaders(headerWithName("Content-Type").description("Request content type, currently only supporting application/json")),
                        requestHeaders(headerWithName("token").description("API access token. Obtain through Registration or Login")),
                        gameSnippet));
    }

    @Test
    public void getGamesByStatus() throws Exception {
        when(gameService.getGamesByStatus(any())).thenReturn(singletonList(game));

        this.mockMvc.perform(get("/games")
                .header("token", validToken)
                .param("status", "OPEN")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-games",
                        requestHeaders(headerWithName("Content-Type").description("Request content type, currently only supporting application/json")),
                        requestHeaders(headerWithName("token").description("API access token. Obtain through Registration or Login")),
                        requestParameters(
                                parameterWithName("status").description("Status of the games. [OPEN, PLAYING, FINISHED]")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of games")
                        ), multipleGameSnippet));
    }

    @Test
    public void getPlayersGame() throws Exception, PlayerNotInGameException {
        when(gameService.getGameByPlayerId(validUserId)).thenReturn(game);

        this.mockMvc.perform(get("/players/{playerId}/games", validUserId)
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-players-playerId-games",
                        requestHeaders(headerWithName("Content-Type").description("Request content type, currently only supporting application/json")),
                        requestHeaders(headerWithName("token").description("API access token. Obtain through Registration or Login")),
                        pathParameters(parameterWithName("playerId").description("The id of the player for which the game shall be queried")),
                        gameSnippet));
    }

    @Test
    public void getGame() throws Exception, GameNotFoundException {
        when(gameService.getGameById(validGameId)).thenReturn(game);

        this.mockMvc.perform(get("/games/{gameId}", validGameId)
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-games-gameId",
                        requestHeaders(headerWithName("Content-Type").description("Request content type, currently only supporting application/json")),
                        requestHeaders(headerWithName("token").description("API access token. Obtain through Registration or Login")),
                        pathParameters(parameterWithName("gameId").description("The id of the requested game")),
                        gameSnippet));
    }

    @Test
    public void addPlayer() throws Exception, GameNotFoundException {
        when(gameService.addPlayerToGame(anyLong(), any())).thenReturn(game);

        this.mockMvc.perform(post("/games/{gameId}/players", validGameId)
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(readFileFromResource("player.json")))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("post-games-gameId-players",
                        requestHeaders(headerWithName("Content-Type").description("Request content type, currently only supporting application/json")),
                        requestHeaders(headerWithName("token").description("API access token. Obtain through Registration or Login")),
                        pathParameters(parameterWithName("gameId").description("The id of the requested game")),
                        gameSnippet));
    }

    @Test
    public void removePlayer() throws Exception, GameNotFoundException {
        when(gameService.removePlayerFromGame(anyLong(), any())).thenReturn(game);

        this.mockMvc.perform(delete("/games/{gameId}/players/{playerId}", validGameId, validUserId)
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("put-games-gameId-players-playerId",
                        requestHeaders(headerWithName("Content-Type").description("Request content type, currently only supporting application/json")),
                        requestHeaders(headerWithName("token").description("API access token. Obtain through Registration or Login")),
                        pathParameters(
                                parameterWithName("gameId").description("The id of the requested game"),
                                parameterWithName("playerId").description("The id of the player to be removed from the game")),
                        gameSnippet));
    }

    @Test
    public void setPlayerReady() throws Exception, GameNotFoundException {
        when(gameService.setPlayerReady(anyLong(), any(), any())).thenReturn(game);

        this.mockMvc.perform(put("/games/{gameId}/players/{playerId}", validGameId, validUserId)
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(readFileFromResource("player_ready.json")))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("delete-games-gameId-players-playerId",
                        requestHeaders(headerWithName("Content-Type").description("Request content type, currently only supporting application/json")),
                        requestHeaders(headerWithName("token").description("API access token. Obtain through Registration or Login")),
                        pathParameters(
                                parameterWithName("gameId").description("The id of the requested game"),
                                parameterWithName("playerId").description("The id of the player to be removed from the game")),
                        gameSnippet));
    }

    @Test
    public void getPlayerCards() throws Exception {
        when(gameService.getPlayerCards(anyLong())).thenReturn(cards);

        this.mockMvc.perform(get("/games/{gameId}/players/{playerId}/cards", validGameId, validUserId)
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-games-gameId-players-playerId-cards",
                        requestHeaders(headerWithName("Content-Type").description("Request content type, currently only supporting application/json")),
                        requestHeaders(headerWithName("token").description("API access token. Obtain through Registration or Login")),
                        pathParameters(
                                parameterWithName("gameId").description("The id of the requested game"),
                                parameterWithName("playerId").description("The id of the player who's cards should be retrieved")),
                        responseFields(
                                fieldWithPath("[].id").description("Id of the card"),
                                fieldWithPath("[].name").description("Name of the card"),
                                fieldWithPath("[].description").description("Description of the card"))
                                .andWithPrefix("[].costs[].",
                                        fieldWithPath("type").description("The kind of resource. [GOLD, REPUTATION, CARD]"),
                                        fieldWithPath("amount").description("The amount of the specified resource")
                                )));
    }

    @Test
    public void getTurn() throws Exception {
        when(gameService.getTurnByGameIdAndCounter(anyLong(), anyInt())).thenReturn(Turn.builder().counter(1).build());

        this.mockMvc.perform(get("/games/{gameId}/turns/{turnCounter}", validGameId, 1)
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-games-gameId-turns-turnCounter",
                        requestHeaders(headerWithName("Content-Type").description("Request content type, currently only supporting application/json")),
                        requestHeaders(headerWithName("token").description("API access token. Obtain through Registration or Login")),
                        pathParameters(
                                parameterWithName("gameId").description("The id of the requested game"),
                                parameterWithName("turnCounter").description("The requested turn")),
                        responseFields(
                                fieldWithPath("counter").description("Turn counter")
                        )));
    }

    @Test
    public void playCard() throws Exception, GameNotFoundException {
        doNothing().when(gameService).playCard(anyLong(), anyInt(), anyLong(), anyLong(), anyLong());

        this.mockMvc.perform(post("/games/{gameId}/turns/{turnCounter}/cards", validGameId, 1)
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(readFileFromResource("card.json")))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("post-games-gameId-turns-turnCounter-cards",
                        requestHeaders(headerWithName("Content-Type").description("Request content type, currently only supporting application/json")),
                        requestHeaders(headerWithName("token").description("API access token. Obtain through Registration or Login")),
                        pathParameters(
                                parameterWithName("gameId").description("The id of the requested game"),
                                parameterWithName("turnCounter").description("The requested turn"))));
    }
}