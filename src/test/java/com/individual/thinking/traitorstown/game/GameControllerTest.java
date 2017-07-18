package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.Configuration;
import com.individual.thinking.traitorstown.authorization.AuthenticationInterceptor;
import com.individual.thinking.traitorstown.game.exceptions.GameNotFoundException;
import com.individual.thinking.traitorstown.model.Game;
import com.individual.thinking.traitorstown.model.Player;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static com.individual.thinking.traitorstown.TestUtils.readFileFromResource;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(this.restDocumentation))
                .build();

        when(authenticationInterceptor.preHandle(any(), any(), any())).thenAnswer(invocation -> {
            MockHttpServletRequest request = (MockHttpServletRequest) invocation.getArguments()[0];
            request.setAttribute(Configuration.AUTHENTICATION_KEY, new Player() {{ setId(1L);}});
            return true;
        });
    }

    @Test
    public void createNewGame() throws Exception {
        when(gameService.createNewGame()).thenReturn(new Game());

        this.mockMvc.perform(post("/games")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "1srioisp5mb07drbbejqni519eib2pti")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("post-games",
                        requestHeaders(headerWithName("Content-Type").description("Request content type, currently only supporting application/json")),
                        requestHeaders(headerWithName("token").description("API access token. Obtain through Registration or Login")),
                        responseFields(
                                fieldWithPath("id").description("User id"),
                                fieldWithPath("players").description("Players in the game"),
                                fieldWithPath("status").description("Game status")
                        )));
    }

    @Test
    public void getGames() throws Exception {

        Game game = new Game();
        game.getPlayers().add(new Player());
        game.getPlayers().add(new Player() {{setReady(true);}});

        when(gameService.getGamesByStatus(any())).thenReturn(Collections.singletonList(game));

        this.mockMvc.perform(get("/games")
                .param("status", "OPEN")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "1srioisp5mb07drbbejqni519eib2pti")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
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
                        ).andWithPrefix("[].",
                                fieldWithPath("id").description("Game id"),
                                fieldWithPath("players").description("Players in the game"),
                                fieldWithPath("playersReady").description("Players that have set themselves to ready"),
                                fieldWithPath("status").description("Game status")
                        )));
    }

    @Test
    public void getGame() throws Exception, GameNotFoundException {
        when(gameService.getGameById(6L)).thenReturn(new Game());

        this.mockMvc.perform(get("/games/{gameId}", 6L)
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "1srioisp5mb07drbbejqni519eib2pti")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("get-games-gameId",
                        requestHeaders(headerWithName("Content-Type").description("Request content type, currently only supporting application/json")),
                        requestHeaders(headerWithName("token").description("API access token. Obtain through Registration or Login")),
                        pathParameters(parameterWithName("gameId").description("The id of the requested game")),
                        responseFields(
                                fieldWithPath("id").description("Game id"),
                                fieldWithPath("players").description("Players in the game"),
                                fieldWithPath("playersReady").description("Players that have set themselves to ready"),
                                fieldWithPath("status").description("Game status")
                        )));
    }

    @Test
    public void addPlayer() throws Exception, GameNotFoundException {
        when(gameService.addPlayerToGame(anyLong(), any())).thenReturn(new Game());

        this.mockMvc.perform(post("/games/{gameId}/players", 6L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(readFileFromResource("player.json"))
                .header("token", "1srioisp5mb07drbbejqni519eib2pti")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("post-games-gameId-players",
                        requestHeaders(headerWithName("Content-Type").description("Request content type, currently only supporting application/json")),
                        requestHeaders(headerWithName("token").description("API access token. Obtain through Registration or Login")),
                        pathParameters(parameterWithName("gameId").description("The id of the requested game")),
                        responseFields(
                                fieldWithPath("id").description("Game id"),
                                fieldWithPath("players").description("Players in the game"),
                                fieldWithPath("playersReady").description("Players that have set themselves to ready"),
                                fieldWithPath("status").description("Game status")
                        )));
    }

    @Test
    public void removePlayer() throws Exception, GameNotFoundException {
        when(gameService.removePlayerFromGame(anyLong(), any())).thenReturn(new Game());

        this.mockMvc.perform(delete("/games/{gameId}/players/{playerId}", 6L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "1srioisp5mb07drbbejqni519eib2pti")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("delete-games-gameId-players-playerId",
                        requestHeaders(headerWithName("Content-Type").description("Request content type, currently only supporting application/json")),
                        requestHeaders(headerWithName("token").description("API access token. Obtain through Registration or Login")),
                        pathParameters(
                                parameterWithName("gameId").description("The id of the requested game"),
                                parameterWithName("playerId").description("The id of the player to be removed from the game")),
                        responseFields(
                                fieldWithPath("id").description("Game id"),
                                fieldWithPath("players").description("Players in the game"),
                                fieldWithPath("playersReady").description("Players that have set themselves to ready"),
                                fieldWithPath("status").description("Game status")
                        )));
    }
}