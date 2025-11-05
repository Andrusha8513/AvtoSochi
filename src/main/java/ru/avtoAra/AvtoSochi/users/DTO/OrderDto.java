package ru.avtoAra.AvtoSochi.users.DTO;

import ru.avtoAra.AvtoSochi.users.Order.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {
    private Long id;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private double totalPrice;
    private List<OrderItemDto> items;
    private String customerEmail;
    private long  daysSinceOrder;
    private double summItem;

    public OrderDto(Long id,
                    LocalDateTime orderDate,
                    OrderStatus status,
                    double totalPrice,
                    double summItem,
                    String customerEmail,
                    List<OrderItemDto> items,
                    long daysSinceOrder


                    ) {
        this.id = id;
        this.orderDate = orderDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.customerEmail = customerEmail;
        this.items = items;
        this.daysSinceOrder = daysSinceOrder;
        this.summItem = summItem;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getCustomerEmail(){
        return customerEmail;
    }
    public void setCustomerEmail(String cutomerEmail){
        this.customerEmail = cutomerEmail;
    }

    public List<OrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDto> items) {
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

    public long getDaysSinceOrder() {
        return daysSinceOrder;
    }

    public void setDaysSinceOrder(long daysSinceOrder) {
        this.daysSinceOrder = daysSinceOrder;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getSummItem() {
        return summItem;
    }

    public void setSummItem(double summItem) {
        this.summItem = summItem;
    }
}
