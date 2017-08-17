package com.individual.thinking.traitorstown.game.representation;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class MessageVo {
    private List<Long> recipients;
    private String content;
}
