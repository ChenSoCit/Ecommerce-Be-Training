package com.java.TrainningJV.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.java.TrainningJV.common.StatsRange;
import com.java.TrainningJV.dtos.request.OrderRequest;
import com.java.TrainningJV.dtos.response.ApiResponse;
import com.java.TrainningJV.services.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Validated
@Slf4j(topic = "ORDER-CONTROLLER")
@RequiredArgsConstructor
@RequestMapping("api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    public ApiResponse getOderById(@PathVariable int id){
        log.info("get order by id: {}", id);
        return ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("get order sucessfully")
            .data(orderService.findOrderById(id))
        .build();
    }
    
    @GetMapping("")
    public ApiResponse findAllOrder(){
        log.info("get all orders");
        return ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("get order sucessfully")
            .data(orderService.getOrders())
        .build();
    }

    @GetMapping("/stats/{userId}")
    public ApiResponse statisOrder(@PathVariable int userId
                                    , @RequestParam(required = false) StatsRange range
                                    , @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from
                                    , @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to )
        {
        log.info("");
        return ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("order statistics successfull")
            .data(orderService.staticsOrder(userId, range, from, to))
        .build();
    }

    @PostMapping("")
    public ApiResponse createOrder(@Valid @RequestBody OrderRequest request){
        log.info("create order: {}", request);
        return ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("create order successfully")
            .data(orderService.createOrder(request))
        .build();
    }

    @PostMapping("/{cartId}/order-cart")
    public ApiResponse createOrderFormCart(@PathVariable int cartId){
        log.info("create order form cart wih id: " + cartId);
        return ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("create order form cart successfull")
            .data(orderService.createOrderFormCart(cartId))
        .build();
    }


    @GetMapping("/oders-userid/{userId}")
    public ApiResponse getOrderByUserId(@PathVariable int userId){
        log.info("get order by id: {}", userId);
        return ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("get order by id successfully")
            .data(orderService.getOrdersByUserId(userId))
        .build();
    }

    @GetMapping("/order-by/day")
    public ApiResponse getOrderByDay(){
        return ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("arrange order total money by day")
            .data(orderService.findOrderByDay())
        .build();
    }

    @GetMapping("/order-by/week")
    public ApiResponse getOrderByWeek(){
        return ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("arrange order total money by week")
            .data(orderService.findOrderByWeek())
        .build();
    }

    @GetMapping("/order-by/month")
    public ApiResponse getOrderByMonth(){
        return ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("arrange order total money by month")
            .data(orderService.findOrderByMonth())
        .build();
    }

    @GetMapping("/order-by/year")
    public ApiResponse getOrderByYear(){
        return ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("arrange order total money by year")
            .data(orderService.findOrderByYear())
        .build();
    }

    @GetMapping("/arrange")
    public ApiResponse getOrderByArrange(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        log.info("Get orders between dates: {} to {}", startDate, endDate);
        return ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("arrange order total money by arrange")
            .data(orderService.findOrdersByRange(startDate, endDate))
        .build();
    }

    @PutMapping("/{orderId}")
    public ApiResponse updateOrder(@Valid @PathVariable int orderId,@Valid @RequestBody OrderRequest request){
        log.info("update order: {}", request);
        return ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("update order successfully")
            .data(orderService.updateOrder(orderId, request))
        .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteOrder(@Valid @PathVariable int id){
        log.info("delete order by id: {}", id);
        orderService.deleteOrder(id);
        return ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("delete order successfully")
            .data(id)
        .build();
    }
}
