package com.backtothefuture.domain.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backtothefuture.domain.store.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {
	boolean existsByName(String name);
}
