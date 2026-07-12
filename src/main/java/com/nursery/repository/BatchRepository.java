package com.nursery.repository;

import com.nursery.entity.Batch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BatchRepository extends JpaRepository<Batch, UUID> {

    List<Batch> findByDeletedFalse();

    List<Batch> findByOrderIdAndDeletedFalseOrderBySowDateAsc(UUID orderId);

    Optional<Batch> findByIdAndDeletedFalse(UUID id);

    boolean existsByOrderIdAndDeletedFalse(UUID orderId);

    /** Total ordered portion seeded for an order = its seededQty (a computed value, never stored on the order). */
    @Query("select coalesce(sum(b.ordered), 0) from Batch b where b.orderId = :orderId and b.deleted = false")
    int sumOrdered(@Param("orderId") UUID orderId);
}
