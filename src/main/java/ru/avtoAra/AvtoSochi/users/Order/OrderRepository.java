package ru.avtoAra.AvtoSochi.users.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o JOIN FETCH o.items i JOIN FETCH i.product WHERE o.users.id = :userId")
    List<Order> findByUsersId(Long userId);

    List<Order> findByCustomerEmailContainingIgnoreCase(String phone);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByStatusAndCustomerEmailContainingIgnoreCase(OrderStatus status, String phone);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items")
    List<Order> findAllWithItems();
}
