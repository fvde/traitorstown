package com.individual.thinking.traitorstown.game.authorization;

import com.individual.thinking.traitorstown.Configuration;
import com.individual.thinking.traitorstown.game.exceptions.PlayerUnauthorizedException;
import com.individual.thinking.traitorstown.model.Player;
import lombok.Getter;

import javax.servlet.ServletRequest;

@Getter
public class AuthorizedPlayer {
    private final Player player;

    public AuthorizedPlayer(ServletRequest request) {
        player = (Player) request.getAttribute(Configuration.AUTHENTICATION_KEY);
    }

    public AuthorizedPlayer authorize(Long gameId, Long playerId) throws PlayerUnauthorizedException {
        if (gameId != null && !player.getGameId().equals(gameId)){
            throw new PlayerUnauthorizedException("Each player may only edit or retrieve information from games that they are in");
        }

        if (playerId != null && (player == null || !player.getId().equals(playerId))){
            throw new PlayerUnauthorizedException("Each player may only edit or retrieve their own attributes");
        }

        return this;
    }
}
