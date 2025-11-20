package ru.avtoAra.AvtoSochi.users.Order;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.avtoAra.AvtoSochi.users.UserService;
import ru.avtoAra.AvtoSochi.users.Users;
import ru.avtoAra.AvtoSochi.users.DTO.OrderDto;
import ru.avtoAra.AvtoSochi.users.DTO.CartItemDto;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {


    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {

        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderDto> createOrder(@RequestBody Map<Long, CartItemDto> cart,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {

            OrderDto orderDto = orderService.createOrderAsDto(cart, userDetails.getUsername());
            return ResponseEntity.ok(orderDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderDto>> getMyOrders(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Users users = userService.findEmail(userDetails.getUsername());
        return ResponseEntity.ok(orderService.findOrdersForUserAsDto(users.getId()));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable Long orderId,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            Order updateOrder = orderService.cancelOrder(orderId, userDetails.getUsername());
            OrderDto orderDto = orderService.convertToDto(updateOrder);
            return ResponseEntity.ok(orderDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{orderId}/return")
    public ResponseEntity<OrderDto> retrunOrder(@PathVariable Long orderId,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            Order order = orderService.returnOrder(orderId, userDetails.getUsername());
            OrderDto orderDto = orderService.convertToDto(order);
            return ResponseEntity.ok(orderDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


//    @PutMapping("/{orderId}/request-return")
//    public ResponseEntity<Order> requestReturn(@PathVariable Long orderId , @AuthenticationPrincipal UserDetails userDetails){
//      if(userDetails == null){
//          return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//      }
//        // Здесь можно добавить проверку, что заказ принадлежит пользователю
//      return ResponseEntity.ok(orderService.updateStatus(orderId, OrderStatus.RETURN_REQUESTED));
//    }

    @PutMapping("/{orderId}/request-return")
    public ResponseEntity<OrderDto> requestReturn(@PathVariable Long orderId,
                                                  @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Order updatedOrder = orderService.updateStatus(orderId, OrderStatus.RETURN_REQUESTED);
        OrderDto orderDto = orderService.convertToDto(updatedOrder); // Конвертируем в DTO
        return ResponseEntity.ok(orderDto);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orderDtos = orderService.getAllOrdersAsDto();
        return ResponseEntity.ok(orderDtos);
    }


    @GetMapping("admin/search")
    public ResponseEntity<List<OrderDto>> searchOrders(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) OrderStatus status) {

        try {
            if (id != null) {
                OrderDto orderDto = orderService.findOrderByIdAsDto(id);
                return ResponseEntity.ok(List.of(orderDto));
            }

            if (status != null && email != null && !email.isEmpty()) {
                List<OrderDto> orderDtos = orderService.findByStatusAndEmailAsDto(status, email);
                return ResponseEntity.ok(orderDtos);
            }

            if (status != null) {
                List<OrderDto> orderDtos = orderService.findByStatusAsDto(status);
                return ResponseEntity.ok(orderDtos);
            }
            if (email != null && !email.isEmpty()) {
                List<OrderDto> orderDtos = orderService.findOrdersByEmailAsDto(email);
                return ResponseEntity.ok(orderDtos);
            }

            List<OrderDto> orderDtos = orderService.getAllOrdersAsDto();
            return ResponseEntity.ok(orderDtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(List.of());
        }
    }


    @PutMapping("/admin/{orderId}/status")
    public ResponseEntity<OrderDto> updateOrderStatus(@PathVariable Long orderId,
                                                      @RequestBody Map<String, String> body) {
        try {
            OrderStatus newStatus = OrderStatus.valueOf(body.get("status"));
            Order updatedOrder = orderService.updateStatus(orderId, newStatus);
            OrderDto orderDto = orderService.convertToDto(updatedOrder);
            return ResponseEntity.ok(orderDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

@GetMapping("/admin/total-delivered-summ")
    public ResponseEntity<Double> getOrderSumm(){
        try {
            Double aDouble = orderService.calculateAndSaveTotalDeliveredSumm();
            return ResponseEntity.ok(aDouble);
        }catch (Exception e){
            return ResponseEntity.badRequest().build();
        }
}

}
//    @PutMapping("/admin/{orderId}/status")
//    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId ,
//                                                   @RequestBody Map<String , String> body){
//        try {
//            OrderStatus newStatus = OrderStatus.valueOf(body.get("status"));
//            Order updateOrder = orderService.updateStatus(orderId , newStatus);
//            return ResponseEntity.ok(updateOrder);
//        }catch (Exception e){
//            return ResponseEntity.badRequest().body(null);
//        }
//    }
//    @GetMapping("/admin/all")
//    public ResponseEntity<List<Order>> getAllOrders(){
//        return ResponseEntity.ok(orderService.getAllOrders());
//    }

//    @GetMapping("/admin/search")
//    public ResponseEntity<List<Order>> searchOrders(@RequestParam(required = false) Long id ,
//                                                    @RequestParam(required = false) String phone){
//        if(id != null){
//            try {
//                Order order = orderService.findById(id);
//                return ResponseEntity.ok(List.of(order));
//            } catch (IllegalArgumentException e) {
//                return ResponseEntity.ok(List.of());
//            }
//        }
//        if(phone != null && !phone.isEmpty()){
//            return ResponseEntity.ok(orderService.findByPhone(phone));
//        }
//        return ResponseEntity.ok(orderService.getAllOrders());
//    }

//    @GetMapping("/admin/search")
//    public ResponseEntity<List<OrderDto>> searchOrders(@RequestParam(required = false) Long id,
//                                                       @RequestParam(required = false) String phone) {
//        try {
//            if (id != null) {
//                OrderDto orderDto = orderService.findOrderByIdAsDto(id);
//                return ResponseEntity.ok(List.of(orderDto));
//            }
//            if (phone != null && !phone.isEmpty()) {
//                List<OrderDto> orderDtos = orderService.findOrdersByPhoneAsDto(phone);
//                return ResponseEntity.ok(orderDtos);
//            }
//
//            // Для случая без параметров
//            List<OrderDto> orderDtos = orderService.getAllOrdersAsDto();
//            return ResponseEntity.ok(orderDtos);
//
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.ok(List.of());
//        }
//    }