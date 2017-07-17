package com.individual.thinking.traitorstown.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void shouldRegisterUser() throws Exception {

        String email = "hans@wurst.de";
        String password = "test";

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        userService.register(email, password);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());

        assertThat(userArgumentCaptor.getValue(), notNullValue());
        assertThat(userArgumentCaptor.getValue().getEmail(), is(email));
        assertThat(userArgumentCaptor.getValue().getPassword(), not(password));
        assertThat(userArgumentCaptor.getValue().getPassword().length(), greaterThan(32));
    }
}