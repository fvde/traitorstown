package com.individual.thinking.traitorstown.message;

import com.individual.thinking.traitorstown.model.Message;
import com.individual.thinking.traitorstown.model.Visibility;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final ApplicationEventPublisher eventPublisher;

    public void sendMessage(Long game, String content){
        sendMessage(game, content, Visibility.ALL);
    }

    public void sendMessage(Long game, String content, Visibility visibility){
        eventPublisher.publishEvent(new Message(game, visibility, content));
    }
}
