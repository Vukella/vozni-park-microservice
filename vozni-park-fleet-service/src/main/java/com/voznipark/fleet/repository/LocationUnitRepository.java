package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.LocationUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationUnitRepository extends JpaRepository<LocationUnit, Long> {

    List<LocationUnit> findByParentIsNull();

    List<LocationUnit> findByParent_IdLocationUnit(Long parentId);
}
