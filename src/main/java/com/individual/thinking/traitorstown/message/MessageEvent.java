package com.individual.thinking.traitorstown.message;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder
@Value
public class MessageEvent {
    final Long gameId;
    final String content;
    final List<Long> recipients;
    final Long from;
}
