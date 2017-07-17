package com.individual.thinking.traitorstown.user;

import com.individual.thinking.traitorstown.user.encryption.Password;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User register(String email, String password) throws Exception {

        if (userRepository.findByEmail(email).isPresent()){
            throw new EmailAlreadyRegisteredException();
        }

        User user = new User(email, Password.getSaltedHash(password));
        userRepository.save(user);

        return user;
    }
}
