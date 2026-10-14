package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}
