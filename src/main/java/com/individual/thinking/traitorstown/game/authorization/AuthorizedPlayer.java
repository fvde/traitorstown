package com.individual.thinking.traitorstown.game.authorization;

import com.individual.thinking.traitorstown.Configuration;
import com.individual.thinking.traitorstown.model.Player;
import lombok.Getter;

import javax.servlet.ServletRequest;

@Getter
public class AuthorizedPlayer {
    private final Player player;

    public AuthorizedPlayer(ServletRequest request) {
        player = (Player) request.getAttribute(Configuration.AUTHENTICATION_KEY);
    }
}
