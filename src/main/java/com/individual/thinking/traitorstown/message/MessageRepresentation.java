package com.individual.thinking.traitorstown.message;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class MessageRepresentation {
    private final Long gameId;
    private final List<Long> recipients;
    private final Long from;
    private final String content;
}
