package ru.avtoAra.AvtoSochi.users.Order;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;
import ru.avtoAra.AvtoSochi.users.EmailService;
import ru.avtoAra.AvtoSochi.users.Product.Product;
import ru.avtoAra.AvtoSochi.users.Product.ProductRepository;
import ru.avtoAra.AvtoSochi.users.UserRepository;
import ru.avtoAra.AvtoSochi.users.Users;
import ru.avtoAra.AvtoSochi.users.DTO.CartItemDto;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import ru.avtoAra.AvtoSochi.users.DTO.OrderDto;
import ru.avtoAra.AvtoSochi.users.DTO.OrderItemDto;
import ru.avtoAra.AvtoSochi.users.DTO.ProductDto;

import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository,
                        EmailService emailService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public Order createOrder(Map<Long, CartItemDto> cart, String userEmail) {
        Users users = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("пользователь не найден"));


        Order order = new Order(); // создаю новый объект заказа(то есть сам заказ)
        order.setUsers(users); // связываю заказ и пользователя
        order.setCustomerEmail(users.getEmail());// сохраняю номер пользователя
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);

        double totalPrice = 0;
        for (CartItemDto itemDto : cart.values()) {
            Product product = productRepository.findById(itemDto.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Продукт с таким ID" + itemDto.getId() + "не найден"));

            OrderItem orderItem = new OrderItem();//создаю новый ЭЛЕМЕНТ ЗАКАЗА(то одну часть заказа)
            orderItem.setOrder(order); // двязь с объектом заказа
            orderItem.setProduct(product);// с товаром
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());
            order.getItems().add(orderItem);// добавляю в заказ

            totalPrice += product.getPrice() * itemDto.getQuantity();
        }
        order.setTotalPrice(totalPrice);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }


    @Transactional(readOnly = true)
    public List<Order> findOrdersForUser(Long userId) {
        return orderRepository.findByUsersId(userId);
    }

    public List<Order> findByEmail(String email) {
        return orderRepository.findByCustomerEmailContainingIgnoreCase(email);
    }

    @Transactional
    public Order updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ с таким ID" + orderId + " не найден"));

        OrderStatus oldStatus = order.getStatus();

        if (oldStatus != OrderStatus.IN_TRANSIT && newStatus == OrderStatus.IN_TRANSIT) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                int newQuantity = product.getQuantity() - item.getQuantity();
                if (newQuantity < 0) {
                    throw new IllegalArgumentException("Недостаточно товара '" + product.getName() + "' на складе.");
                }
                product.setQuantity(newQuantity);

                productRepository.save(product);

            }
        } else if (oldStatus != OrderStatus.RETURN_COMPLETED && newStatus == OrderStatus.RETURN_COMPLETED) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() + item.getQuantity());

                productRepository.save(product);
            }
        }
        order.setStatus(newStatus);
        Order updateOrder = orderRepository.save(order);
        try {
            emailService.sendOrderStatusUpdate(order.getCustomerEmail() , orderId , oldStatus , newStatus);
            System.out.println("Уведомление отправленно на :" + order.getCustomerEmail());
        }catch (Exception e){
            System.out.println("Ошибка отправки уведомления " + e.getMessage());
        }
        return updateOrder;
    }


    @Transactional
    public Order cancelOrder(Long orderId, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказа с таким id" + orderId + " нет"));


        if (!order.getCustomerEmail().equals(userEmail)) {
            throw new IllegalArgumentException("Заказ не принадлежит пользователю");
        }

        if (order.getStatus() != OrderStatus.PLACED &&
                order.getStatus() != OrderStatus.IN_TRANSIT) {
            throw new IllegalArgumentException("Отмена возможна только для заказов в статусе 'Оформлен' или 'В пути'");
        }

        if (ChronoUnit.DAYS.between(order.getOrderDate(), LocalDateTime.now()) > 14) {
            throw new IllegalArgumentException("Возврат товара возможен только в первые 14 дней после получения");

        }
        order.setStatus(OrderStatus.Canceled);
        return orderRepository.save(order);
    }

    @Transactional
    public Order returnOrder(Long orderId, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказа с таким id" + orderId + "нет"));


        if (!order.getCustomerEmail().equals(userEmail)) {
            throw new IllegalArgumentException("Заказ пользователю не принадлежит");
        }

        if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.AT_PICKUP_POINT) {
            throw new IllegalArgumentException("Возврат возможен только для товаров в статусе  'В пункте выдачи' и 'Товар у покупателя'");
        }
        if (order.getStatus() == OrderStatus.DELIVERED) {
            if (ChronoUnit.DAYS.between(order.getOrderDate(), LocalDateTime.now()) > 14) {
                throw new IllegalArgumentException("Возврат возможет только в первые 14 дней после получения товара");
            }
        }

        order.setStatus(OrderStatus.RETURN_REQUESTED);
        return orderRepository.save(order);
    }


    public Order findById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ с таким ID " + orderId + " не найден"));
    }


    @Transactional(readOnly = true)
    public List<OrderDto> findOrdersForUserAsDto(Long userId) {
        List<Order> orders = orderRepository.findByUsersId(userId);

        return orders.stream().map(order -> {
            long daysSinceOrder = ChronoUnit.DAYS.between(order.getOrderDate(), LocalDateTime.now());

            return new OrderDto(
                    order.getId(),
                    order.getOrderDate(),
                    order.getStatus(),
                    order.getTotalPrice(),
                    order.getSumm(),
                    order.getCustomerEmail(),
                    order.getItems()
                            .stream().map(item -> new OrderItemDto(
                                    new ProductDto(item.getProduct().getName(), item.getProduct().getArticleNumber()),
                                    item.getQuantity(),
                                    item.getPriceAtPurchase()
                            )).collect(Collectors.toList()),
                    daysSinceOrder
            );
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Order> findStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> findByStatusAsDto(OrderStatus status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDto> findByStatusAndEmailAsDto(OrderStatus status, String userEmail) {
        List<Order> orders = orderRepository.findByStatusAndCustomerEmailContainingIgnoreCase(status, userEmail);
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDto createOrderAsDto(Map<Long, CartItemDto> cart, String userEmail) {
        Order order = createOrder(cart, userEmail);
        return convertToDto(order);
    }

    public OrderDto convertToDto(Order order) {
        long daysSinceOrder = ChronoUnit.DAYS.between(order.getOrderDate(), LocalDateTime.now());
        List<OrderItemDto> itemDtos = order.getItems().stream()
                .map(item -> new OrderItemDto(
                        new ProductDto(
                                item.getProduct().getArticleNumber(),
                                item.getProduct().getBrand()
                        ),
                        item.getQuantity(),
                        item.getPriceAtPurchase()
                ))
                .collect(Collectors.toList());

        return new OrderDto(
                order.getId(),
                order.getOrderDate(),
                order.getStatus(),
                order.getSumm(),
                order.getTotalPrice(),
                order.getCustomerEmail(),
                itemDtos,
                daysSinceOrder

        );
    }

    @Transactional(readOnly = true)
    public OrderDto findOrderByIdAsDto(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Заказ с таким ID " + orderId + " не найден"));
        return convertToDto(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDto> findOrdersByEmailAsDto(String email) {
        List<Order> orders = orderRepository.findByCustomerEmailContainingIgnoreCase(email);
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDto> getAllOrdersAsDto() {
        List<Order> orders = orderRepository.findAllWithItems();
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }



    @Transactional
    public Double calculateAndSaveTotalDeliveredSumm() {
        List<Order> ordersList = orderRepository.findByStatus(OrderStatus.DELIVERED);
        double tottalsumm = 0.0;
        for (Order order : ordersList) {
            double summ = 0.0;
            for (OrderItem item : order.getItems()) {
                summ += item.getPriceAtPurchase() * item.getQuantity();
            }
            order.setSumm(summ);
            tottalsumm += summ;

        }
        orderRepository.saveAll(ordersList);
        return tottalsumm;
    }



}
