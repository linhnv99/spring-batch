package com.linhnv.springbatchexample.repository;

import com.linhnv.springbatchexample.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<Player, String> {
}
