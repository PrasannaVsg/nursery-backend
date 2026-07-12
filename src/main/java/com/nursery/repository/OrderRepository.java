package com.nursery.repository;

import com.nursery.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByDeletedFalseOrderByUpdatedAtDesc();

    Optional<Order> findByIdAndDeletedFalse(UUID id);
}
