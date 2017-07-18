package com.individual.thinking.traitorstown.user;

import com.individual.thinking.traitorstown.user.exceptions.IncorrectPasswordException;
import com.individual.thinking.traitorstown.user.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/user/register")
    public User register(@RequestBody RegistrationRequestVo registrationRequestVo) throws Exception {
        User user = userService.register(registrationRequestVo.getEmail(), registrationRequestVo.getPassword());
        log.info("Registered user with email {}", user.getEmail());
        return user;
    }

    @PostMapping("/user/login")
    public User login(@RequestBody LoginRequestVo loginRequestVo) throws UserNotFoundException, IncorrectPasswordException {
        User user = userService.login(loginRequestVo.getEmail(), loginRequestVo.getPassword());
        return user;
    }
}
