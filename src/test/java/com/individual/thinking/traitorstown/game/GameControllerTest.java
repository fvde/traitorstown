package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.model.Game;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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

    @Before
    public void setup(){
        this.mockMvc = MockMvcBuilders.standaloneSetup(new GameController(gameService))
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }

    @Test
    public void createNewGame() throws Exception {
        when(gameService.createNewGame()).thenReturn(new Game());

        this.mockMvc.perform(post("/game")
                .contentType(MediaType.APPLICATION_JSON)
                .header("token", "1srioisp5mb07drbbejqni519eib2pti")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("post-game",
                        requestHeaders(headerWithName("Content-Type").description("Request content type, currently only supporting application/json")),
                        requestHeaders(headerWithName("token").description("API access token. Obtain through Registration or Login")),
                        responseFields(
                                fieldWithPath("id").description("User id"),
                                fieldWithPath("players").description("Players in the game"),
                                fieldWithPath("status").description("Game status")
                        )));
    }

}