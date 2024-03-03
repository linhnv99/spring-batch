package com.linhnv.springbatchexample.dto;

import com.linhnv.springbatchexample.entity.Player;
import org.springframework.batch.item.ItemProcessor;

public class PlayerProcessor implements ItemProcessor<Player, Player> {

    @Override
    public Player process(Player player) throws Exception {
        return player;
    }
}