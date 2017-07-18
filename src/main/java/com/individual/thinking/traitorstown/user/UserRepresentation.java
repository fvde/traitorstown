package com.individual.thinking.traitorstown.user;

import lombok.Data;

@Data
class UserRepresentation {
    private final Long id;
    private final Long playerId;
    private final String email;
    private final String token;

    protected static UserRepresentation fromUser(User user){
        return new UserRepresentation(user.getId(),
                user.getPlayer() != null ? user.getPlayer().getId() : null,
                user.getEmail(),
                user.getToken());
    }
}
