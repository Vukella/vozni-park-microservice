package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.NisCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NisCardRepository extends JpaRepository<NisCard, Long> {
}
