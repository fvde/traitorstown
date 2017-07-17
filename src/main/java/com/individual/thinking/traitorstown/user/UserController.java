package com.individual.thinking.traitorstown.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/v1/user/register")
    public User register(@RequestBody RegistrationRequestVo registrationRequestVo) throws Exception {

        log.debug("Registering new user with email ", registrationRequestVo.getEmail());
        User user = userService.register(registrationRequestVo.getEmail(), registrationRequestVo.getPassword());
        log.debug("Successfully registered user with email ", user.getEmail());

        return user;
    }
}
