package com.codewithmosh.store.repositories;

import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @EntityGraph(attributePaths = "items.product")
    @Query("SELECT o from Order o where o.customer = :customer")
    List<Order> findAllByCustomer(@Param("customer") User customer);
}