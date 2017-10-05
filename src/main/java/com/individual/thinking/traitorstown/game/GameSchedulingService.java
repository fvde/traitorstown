package com.individual.thinking.traitorstown.game;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GameSchedulingService {

    private final GameService gameService;

    // TODO replace with queuing turn end event with delay on starting a turn
//    @Scheduled(fixedRate = 5000L)
//    public void endTurns(){
//        log.info("Ending turns...");
//        gameService.getGamesByStatus(GameStatus.PLAYING).forEach(game -> game.getCurrentTurn().end());
//    }
}
