package com.backtothefuture.domain.product.repository;

import com.backtothefuture.domain.product.Product;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p.store.member.id FROM Product p WHERE p.id = :productId AND p.store.id = :storeId")
    Optional<Long> findMemberIdByStoreIdAndProductId(@Param("storeId") Long storeId, @Param("productId") Long productId);

    //TODO: 락 휙득 대기 시간이 길어질 때 발생하는 예외를 어떻게 처리할 것인지,,
    @Lock(value = LockModeType.PESSIMISTIC_WRITE) // 베타 락 휙득
    @Query("select p from Product p where p.id = :productId")
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")}) // 락 휙득을 위해 3초까지 대기
    Optional<Product> findProductWithPessimisticLockById(@Param("productId") Long productId);

    boolean existsById(Long id);

    Optional<Product> findById(Long id);

    List<Product> findAll();

    List<Product> findAllByStoreId(Long storeId);
}
