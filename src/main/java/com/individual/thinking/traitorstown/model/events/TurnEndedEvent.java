package com.individual.thinking.traitorstown.model.events;

import com.individual.thinking.traitorstown.model.Turn;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Builder
@Value
public class TurnEndedEvent {
    @NonNull
    final Turn turn;
}