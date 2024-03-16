package com.backtothefuture.domain.store.repository;

import com.backtothefuture.domain.store.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

	Optional<Store> findById(Long id);

	boolean existsByName(String name);
}
