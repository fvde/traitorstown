package com.individual.thinking.traitorstown.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Tolerate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Builder
@Getter
public class Card {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String name;

    @Tolerate
    Card() {}
}
