package com.backtothefuture.domain.store.repository;

import com.backtothefuture.domain.store.Store;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StoreRepository extends JpaRepository<Store, Long> {

    Optional<Store> findById(Long id);

    @Query("SELECT store FROM Store store "
            + "WHERE (:cursor = 0L OR store.id < :cursor) "
            + "ORDER BY store.id DESC")
    List<Store> findStoresBy(
            @Param("cursor") Long cursor,
            Pageable pageable
    );

    boolean existsByName(String name);
}
