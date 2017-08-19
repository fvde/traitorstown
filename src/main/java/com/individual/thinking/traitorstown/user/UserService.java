package com.individual.thinking.traitorstown.user;

import com.individual.thinking.traitorstown.game.PlayerService;
import com.individual.thinking.traitorstown.model.Player;
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
    private final PlayerService playerService;

    public User register(String email, String password) throws Exception {
        return register(email, password, false);
    }

    public User register(String email, String password, boolean ai) throws Exception {

        if (userRepository.findByEmail(email).isPresent()){
            throw new EmailAlreadyInUseException("Email already in use!");
        }

        Player player = playerService.createPlayer(ai);
        User user = User.builder()
                .email(email)
                .password(Secure.getSaltedHash(password))
                .token(Secure.getToken())
                .player(player)
                .build();

        userRepository.save(user);

        return user;
    }

    public User login(String email, String password) throws Exception {
        Optional<User> user = userRepository.findByEmail(email);

        if (!user.isPresent()){
            throw new UserNotFoundException("User not found");
        }

        if (!Secure.check(password, user.get().getPassword())){
            throw new IncorrectPasswordException("Password incorrect");
        }

        return user.get();
    }

    public User getUserByToken(String token) throws UserNotFoundException {
        Optional<User> user = userRepository.findByToken(token);
        if (!user.isPresent()){
            throw new UserNotFoundException("User not found");
        }
        return user.get();
    }
}
