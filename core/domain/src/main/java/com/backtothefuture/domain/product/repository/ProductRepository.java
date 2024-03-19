package com.backtothefuture.domain.product.repository;

import com.backtothefuture.domain.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p.store.member.id FROM Product p WHERE p.id = :productId AND p.store.id = :storeId")
    Optional<Long> findMemberIdByStoreIdAndProductId(@Param("storeId") Long storeId, @Param("productId") Long productId);

    boolean existsById(Long id);

    Optional<Product> findById(Long id);

    List<Product> findAll();
}