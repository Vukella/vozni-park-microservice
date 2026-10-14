package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.TachographCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TachographCardRepository extends JpaRepository<TachographCard, Long> {
}
