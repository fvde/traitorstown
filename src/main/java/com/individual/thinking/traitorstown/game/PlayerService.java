package com.individual.thinking.traitorstown.game;

import com.individual.thinking.traitorstown.game.exceptions.PlayerNotFoundException;
import com.individual.thinking.traitorstown.game.repository.PlayerRepository;
import com.individual.thinking.traitorstown.model.Card;
import com.individual.thinking.traitorstown.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final CardService cardService;

    public Player createPlayer(Boolean isAi){
        return playerRepository.save(Player.builder().decks(cardService.getStandardDecks()).ready(false).ai(isAi).build());
    }

    public List<Card> getPlayerCards(Long playerId) throws PlayerNotFoundException {
        return getPlayerCards(getPlayerById(playerId));
    }

    public Player getPlayerById(Long id) throws PlayerNotFoundException {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found"));
    }

    private List<Card> getPlayerCards(Player player){
        return player.getHandCards()
                .stream()
                .sorted(Comparator.comparing(Card::getId))
                .collect(Collectors.toList());
    }

    public void setPlayerReady(Long playerId, Boolean ready) throws PlayerNotFoundException {
        Player player = getPlayerById(playerId);
        player.setReady(ready);
        playerRepository.save(player);
    }
}
