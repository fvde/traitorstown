package com.individual.thinking.traitorstown.user;

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

        log.info("Registering new user with email {}", registrationRequestVo.getEmail());
        User user = userService.register(registrationRequestVo.getEmail(), registrationRequestVo.getPassword());
        log.info("Successfully registered user with email {}", user.getEmail());

        return user;
    }
}
