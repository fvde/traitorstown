package com.individual.thinking.traitorstown.model;

import com.individual.thinking.traitorstown.game.rules.Rules;
import com.individual.thinking.traitorstown.model.effects.CitizenEffect;
import com.individual.thinking.traitorstown.model.effects.TraitorEffect;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;

@RunWith(Parameterized.class)
@Slf4j
public class PlayerResourceTest {

    @Parameter
    public Player player;

    @Parameter(1)
    public Integer gold;

    @Parameter(2)
    public Integer stolen;

    @Parameter(3)
    public Integer expectedMaximumStolenGold;

    @Parameters
    public static Collection<Object[]> data() {
        Player citizen = Player.builder().decks(Collections.emptyList()).ready(true).ai(false).build();
        citizen.addEffect(CitizenEffect.builder().build(), citizen);

        Player traitor = Player.builder().decks(Collections.emptyList()).ready(true).ai(false).build();
        traitor.addEffect(TraitorEffect.builder().build(), traitor);

        return Arrays.asList(new Object[][] {
                // role, gold, stolen, expected stolen
                { citizen, 10, 10, 10 },
                { citizen, 10, 1, 1 },
                { citizen, 10, 1, 1 },
                { citizen, 10, 1, 1 },
                { citizen, 10, 1, 1 },
                { citizen, 10, 2, 2 },
                { citizen, 10, 2, 2 },
                { citizen, 10, 2, 2 },
                { citizen, 10, 2, 2 },
                { citizen, 10, 20, 20 },
                { citizen, 10, 0, 0 },
                { traitor, 10, 10, 0 },
                { traitor, 10, 7, 0 },
                { traitor, 10, 18, 8 },
                { traitor, 10, 0, 0 }
        });
    }

    @Before
    public void setup(){
        for (ResourceType type : ResourceType.values()){
            player.getResources().put(type, Rules.STARTING_RESOURCES.containsKey(type) ? Rules.STARTING_RESOURCES.get(type) : 0);
        }

        player.addResource(ResourceType.STOLEN_GOLD, stolen);
    }

    @Test
    public void shouldUseStolenGoldBasedOnRole() {
        log.info("Role " + player.getRole());
        log.info("Gold " + gold);
        log.info("Stolen " + stolen);
        player.removeResource(ResourceType.GOLD, gold);
        assertThat(player.getResource(ResourceType.STOLEN_GOLD), lessThanOrEqualTo(expectedMaximumStolenGold));
        log.info("Remaining stolen " + player.getResource(ResourceType.STOLEN_GOLD));
    }
}