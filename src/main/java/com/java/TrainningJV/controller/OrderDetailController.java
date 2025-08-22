package com.java.TrainningJV.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.TrainningJV.dtos.request.OrderDetailRequest;
import com.java.TrainningJV.dtos.response.ApiResponse;
import com.java.TrainningJV.services.OrderDetailService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/v1/order-detail")
@Slf4j(topic = "ORDER DETAIL CONTROLLER")
public class OrderDetailController {
    @Autowired
    private OrderDetailService orderDetailService;

    @PostMapping("")
    public ApiResponse insertOrderDetail(@RequestBody OrderDetailRequest request){
        log.info("Insert Order Detail, {}", request);
        return ApiResponse.builder()
                .message("Insert Order Detail")
                .status(HttpStatus.OK.value())
                .data(orderDetailService.insertOrderDetails(request))
                .build();
    }

    @GetMapping("/{orderId}")
    public ApiResponse getOrderDetailById(@PathVariable int orderId){
        log.info("Get Order Detail by Order Id, {}", orderId);
        return ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("Get Order Detail by Order Id")
            .data(orderDetailService.selectOrderDetailsByOrderId(orderId))
        .build();
    }

    @GetMapping("/all")
    public ApiResponse getAllOrderDetail(){
        log.info("Get all Order Details");
        return ApiResponse.builder()
            .status(HttpStatus.OK.value())
            .message("Get all Order Details")
            .data(orderDetailService.selectAllOrderDetails())
        .build();
    }

    @GetMapping("/all-order/{orderId}")
    public ApiResponse getAllOrderDetailByOrderId(@PathVariable int orderId){
        log.info("Get all Order Details by Order Id, {}", orderId);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Get all Order Details by Order Id " + orderId)
                .data(orderDetailService.selectAllOrderDetails(orderId))
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse deleteOrderDetail(@PathVariable int id){
        log.info("Delete Order Detail by Order Id, {}", id);
        orderDetailService.deleteById(id);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Delete Order Detail by Order Id: "+ id)
                .data("")
                .build();
    }

    @PutMapping("/{id}")
    public ApiResponse updateOrderDetail(@PathVariable int id, @RequestBody OrderDetailRequest request){
        log.info("Update Order Detail by Order Id, {}", id);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Update Order Detail by Order Id")
                .data(orderDetailService.updateOrderDetails(id, request))
                .build();
    }
}
