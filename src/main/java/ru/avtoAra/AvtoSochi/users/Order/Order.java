package ru.avtoAra.AvtoSochi.users.Order;

import jakarta.persistence.*;
import ru.avtoAra.AvtoSochi.users.Users;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private Users users;

    @OneToMany(mappedBy = "order" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderDate;
    private Double totalPrice;
    private String customerEmail;
    private double summ;

    public Order(){}

    public Order(String customerEmail,
                 Long id,
                 List<OrderItem> items,
                 LocalDateTime orderDate,
                 OrderStatus status,
                 Double totalPrice,
                 Users users) {
        this.customerEmail = customerEmail;
        this.id = id;
        this.items = items;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.users = users;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items)  {
        this.items = items;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public double getSumm(){
        return summ;
    }

    public void setSumm(double summ){
        this.summ = summ;
    }
}
