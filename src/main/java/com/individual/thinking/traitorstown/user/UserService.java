package com.individual.thinking.traitorstown.user;

import com.individual.thinking.traitorstown.user.encryption.Secure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UserService {

    private final UserRepository userRepository;

    User register(String email, String password) throws Exception {

        if (userRepository.findByEmail(email).isPresent()){
            throw new EmailAlreadyInUseException("Email already in use!");
        }

        User user = User.builder()
                .email(email)
                .password(Secure.getSaltedHash(password))
                .token(Secure.getToken())
                .build();

        userRepository.save(user);

        return user;
    }
}
