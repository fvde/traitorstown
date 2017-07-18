package com.individual.thinking.traitorstown.user;

import com.individual.thinking.traitorstown.model.Player;
import com.individual.thinking.traitorstown.model.User;
import com.individual.thinking.traitorstown.user.encryption.Secure;
import com.individual.thinking.traitorstown.user.exceptions.EmailAlreadyInUseException;
import com.individual.thinking.traitorstown.user.exceptions.IncorrectPasswordException;
import com.individual.thinking.traitorstown.user.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    User register(String email, String password) throws Exception {

        if (userRepository.findByEmail(email).isPresent()){
            throw new EmailAlreadyInUseException("Email already in use!");
        }

        User user = User.builder()
                .email(email)
                .password(Secure.getSaltedHash(password))
                .token(Secure.getToken())
                .player(new Player())
                .build();

        userRepository.save(user);

        return user;
    }

    User login(String email, String password) throws UserNotFoundException, IncorrectPasswordException {
        Optional<User> user = userRepository.findByEmail(email);
        if (!user.isPresent()){
            throw new UserNotFoundException("User not found");
        }

        try {
            if (Secure.check(password, user.get().getPassword()))
                return user.get();
        } catch (Exception e) {
            throw new IncorrectPasswordException("Password incorrect", e);
        }

        return null;
    }

    public User getUserByToken(String token) throws UserNotFoundException {
        Optional<User> user = userRepository.findByToken(token);
        if (!user.isPresent()){
            throw new UserNotFoundException("User not found");
        }
        return user.get();
    }
}
