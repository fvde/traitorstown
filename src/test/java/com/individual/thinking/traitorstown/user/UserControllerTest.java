package com.individual.thinking.traitorstown.user;

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

import static com.individual.thinking.traitorstown.TestUtils.readFileFromResource;
import static org.mockito.Matchers.any;
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
public class UserControllerTest {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Before
    public void setup(){
        this.mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }

    @Test
    public void register() throws Exception {
        when(userService.register(any(), any())).thenReturn(
                User.builder()
                .id(123456789L)
                .email("peter@hotmail.com")
                .password("i50M6NBwORO5FVH+bNGbyc0qtPsrNpma3wCPvAK+Bpg=$6NVhMN3A3i4NOcqzezrc5crwrhteyM1cVo2TZrlMOsE=")
                .build());

        this.mockMvc.perform(post("/user/register")
                .content(readFileFromResource("registration.json"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("register",
                        requestHeaders(headerWithName("Content-Type").description("Request content type, currently only supporting application/json")),
                        responseFields(
                                fieldWithPath("id").description("User id"),
                                fieldWithPath("email").description("User email"),
                                fieldWithPath("token").description("Access token, to be used to access secured API calls.")
                        )));
    }
}