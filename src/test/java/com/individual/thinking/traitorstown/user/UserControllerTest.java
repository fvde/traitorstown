package com.individual.thinking.traitorstown.user;

import com.individual.thinking.traitorstown.MockMvcBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.individual.thinking.traitorstown.TestUtils.readFileFromResource;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class UserControllerTest extends MockMvcBase{

    @MockBean
    private UserService userService;

    @Test
    public void register() throws Exception {
        when(userService.register(any(), any())).thenReturn(
                User.builder()
                .id(123456789L)
                .email("peter@hotmail.com")
                .token("6NVhMN3A3i4NOcqzezrc5crwrhteyM1cVo2TZrlMOsE=")
                .build());

        this.mockMvc.perform(post("/users/register")
                .content(readFileFromResource("registration.json"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void login() throws Exception {
        when(userService.login(any(), any())).thenReturn(
                User.builder()
                .id(123456789L)
                .email("peter@hotmail.com")
                .token("6NVhMN3A3i4NOcqzezrc5crwrhteyM1cVo2TZrlMOsE=")
                .build());

        this.mockMvc.perform(post("/users/login")
                .content(readFileFromResource("login.json"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}