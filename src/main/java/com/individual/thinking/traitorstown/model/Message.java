package com.individual.thinking.traitorstown.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Tolerate;

import javax.persistence.*;

@Entity
@Builder
@ToString
public class Message {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Getter
    private MessageDeliveryType deliveryType = MessageDeliveryType.ALL;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MessageStructure structure = MessageStructure.CONTENT;

    @NonNull
    private String content;

    @Tolerate
    Message() {}

    public String buildContent(Player origin, Player target){
        switch (structure) {
            case CONTENT: return content;
            case PREFIX_TARGET: return target.getId() + content;
            default: return content;
        }
    }

    public boolean isForTarget(){
        return deliveryType == MessageDeliveryType.ORIGIN;
    }

    public boolean isForOrigin(){
        return deliveryType == MessageDeliveryType.TARGET;
    }

    public boolean isForAll(){
        return deliveryType == MessageDeliveryType.ALL;
    }
}
