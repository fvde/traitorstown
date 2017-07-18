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
    public UserRepresentation register(@RequestBody RegistrationRequestVo registrationRequestVo) throws Exception {
        return UserRepresentation.fromUser(userService.register(registrationRequestVo.getEmail(), registrationRequestVo.getPassword()));
    }

    @PostMapping("/user/login")
    public UserRepresentation login(@RequestBody LoginRequestVo loginRequestVo) throws UserNotFoundException, IncorrectPasswordException {
        return UserRepresentation.fromUser(userService.login(loginRequestVo.getEmail(), loginRequestVo.getPassword()));
    }
}
