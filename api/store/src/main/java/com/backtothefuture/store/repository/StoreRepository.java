package com.backtothefuture.store.repository;

import com.backtothefuture.domain.store.Store;
import com.backtothefuture.store.dto.response.StoreResponse;
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
    List<Store> findStoresByCursor(
            @Param("cursor") Long cursor,
            Pageable pageable
    );

    @Query("SELECT store FROM Store store "
            + "WHERE (:sortingIndex = 0L OR store.sortingIndex < :sortingIndex) "
            + "ORDER BY store.sortingIndex DESC, store.id DESC")
    List<Store> findStoresBySortingIndex(
            @Param("sortingIndex") Long sortingIndex,
            Pageable pageable
    );

    @Query("SELECT new com.backtothefuture.store.dto.response.StoreResponse("
            + "store.id, store.sortingIndex, store.name, store.image, store.averageRating, store.totalRatingCount, "
            + "store.startTime, store.endTime, ("
            + "6371 * ACOS("
            + "COS(RADIANS(:latitude)) "
            + "* COS(RADIANS(store.latitude)) "
            + "* COS(RADIANS(store.longitude) - RADIANS(:longitude)) "
            + "+ SIN(RADIANS(:latitude)) * SIN(RADIANS(store.latitude))"
            + "))) AS distance "
            + "FROM Store store "
            + "ORDER BY 9"
    )
    List<StoreResponse> findStoresByLocation(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            Pageable pageable
    );

    boolean existsByName(String name);
}
