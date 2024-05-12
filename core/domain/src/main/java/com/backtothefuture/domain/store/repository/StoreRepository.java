package com.backtothefuture.domain.store.repository;

import com.backtothefuture.domain.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {

    boolean existsByName(String name);
}
