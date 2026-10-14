package com.voznipark.fleet.repository;

import com.voznipark.fleet.entity.SystemRefresh;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SystemRefreshRepository extends JpaRepository<SystemRefresh, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from SystemRefresh s where s.keyName = :keyName")
    Optional<SystemRefresh> findByKeyNameWithLock(String keyName);
}
