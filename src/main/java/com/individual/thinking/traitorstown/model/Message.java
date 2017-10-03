package com.individual.thinking.traitorstown.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.Optional;

@Getter
@Builder
@ToString
public class Message {

    @Builder.Default
    private MessageStructure structure = MessageStructure.CONTENT;

    @NonNull
    private String content;

    public String buildContent(Optional<Player> origin, Optional<Player> target){
        switch (structure) {
            case CONTENT: return content;
            case PREFIX_ORIGIN: return (origin.isPresent() ? "Player " + origin.get().getId() : "Unknown player") + content;
            case PREFIX_TARGET: return (target.isPresent() ? "Player " + target.get().getId() : "Unknown player") + content;
            case PREFIX_ORIGIN_POSTFIX_TARGET: return "Player " + origin.get().getId() + content + "Player " + target.get().getId();

            default: return content;
        }
    }
}
