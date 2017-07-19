package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.Configuration;
import com.individual.thinking.traitorstown.authorization.AuthenticationInterceptor;
import com.individual.thinking.traitorstown.game.exceptions.GameNotFoundException;
import com.individual.thinking.traitorstown.model.Card;
import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.Turn;
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
import java.util.Collections;

import static com.individual.thinking.traitorstown.TestUtils.readFileFromResource;
import static org.mockito.Matchers.*;
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
            fieldWithPath("status").description("Game status"),
            fieldWithPath("players").description("Players in the game"))
            .andWithPrefix("players[].",
                fieldWithPath("id").description("Id of the player"),
                fieldWithPath("ready").description("Indicates whether player is ready to play")
    );

    private ResponseFieldsSnippet multipleGameSnippet = responseFields(
            fieldWithPath("[].id").description("Game id"),
            fieldWithPath("[].status").description("Game status"),
            fieldWithPath("[].players").description("Players in the game"))
            .andWithPrefix("[].players[].",
                    fieldWithPath("id").description("Id of the player"),
                    fieldWithPath("ready").description("Indicates whether player is ready to play")
            );

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(this.restDocumentation))
                .build();

        when(authenticationInterceptor.preHandle(any(), any(), any())).thenAnswer(invocation -> {
            MockHttpServletRequest request = (MockHttpServletRequest) invocation.getArguments()[0];
            request.setAttribute(Configuration.AUTHENTICATION_KEY, Player.builder()
                .id(validUserId)
                .gameId(validGameId)
                .cards(Arrays.asList(
                        Card.builder().id(1L).name("Trade").build(),
                        Card.builder().id(2L).name("Fight").build()))
                .build());
            return true;
        });
    }

    @Test
    public void createNewGame() throws Exception {
        when(gameService.createNewGame()).thenReturn(new Game() {{addPlayer(Player.builder().build());}});
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
    public void getGames() throws Exception {

        Game game = new Game();
        game.addPlayer(Player.builder().build());
        game.addPlayer(Player.builder().ready(true).build());

        when(gameService.getGamesByStatus(any())).thenReturn(Collections.singletonList(game));

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
    public void getGame() throws Exception, GameNotFoundException {
        when(gameService.getGameById(validGameId)).thenReturn(new Game() {{addPlayer(Player.builder().build());}});

        this.mockMvc.perform(get("/games/{gameId}", validGameId)
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
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
        when(gameService.addPlayerToGame(anyLong(), any())).thenReturn(new Game() {{addPlayer(Player.builder().build());}});

        this.mockMvc.perform(post("/games/{gameId}/players", validGameId)
                .header("token", validToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(readFileFromResource("player.json"))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
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
        when(gameService.removePlayerFromGame(anyLong(), any())).thenReturn(new Game() {{addPlayer(Player.builder().build());}});

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
        when(gameService.setPlayerReady(anyLong(), any(), any())).thenReturn(new Game() {{addPlayer(Player.builder().build());}});

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
        when(gameService.getPlayerCards(anyLong())).thenReturn(Arrays.asList(
                Card.builder().id(1L).name("Trade").build(),
                Card.builder().id(2L).name("Fight").build()));

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
                                fieldWithPath("[].name").description("Name of the card")
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
}