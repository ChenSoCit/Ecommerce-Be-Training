package com.java.TrainningJV.services.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.java.TrainningJV.common.enums.OrderStatus;
import com.java.TrainningJV.dtos.request.OrderDetailRequest;
import com.java.TrainningJV.exceptions.BadRequestException;
import com.java.TrainningJV.exceptions.ResourceNotFoundException;
import com.java.TrainningJV.mappers.mapper.OrderDetailMapper;
import com.java.TrainningJV.mappers.mapper.OrderMapper;
import com.java.TrainningJV.mappers.mapper.ProductMapper;
import com.java.TrainningJV.mappers.mapperCustom.OrderDetailMapperCustom;
import com.java.TrainningJV.mappers.mapperCustom.OrderMapperCustom;
import com.java.TrainningJV.mappers.mapperCustom.ProductMapperCustom;
import com.java.TrainningJV.models.Order;
import com.java.TrainningJV.models.OrderDetails;
import com.java.TrainningJV.models.Product;
import com.java.TrainningJV.services.OrderDetailService;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j(topic = "ORDER DETAIL SERVICE")

public class OrderDetailServiceImpl implements OrderDetailService {
    @Autowired
    OrderDetailMapper orderDetailMapper;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderMapperCustom orderMapperCustom;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    ProductMapperCustom productMapperCustom;

    @Autowired
    OrderDetailMapperCustom orderDetailMapperCustom;

    @Override
    @Transactional
    public OrderDetails insertOrderDetails(OrderDetailRequest req) {
        log.info("Insert Order Details, {}", req);

        if (req.getNumberOfProducts() == null || req.getNumberOfProducts() <= 0) {
            throw new BadRequestException("numberOfProducts must be > 0");
        }

        // Lấy order & check trạng thái
        Order order = orderMapper.selectByPrimaryKey(req.getOrderId());
        if (order == null) throw new ResourceNotFoundException("Order", "id", req.getOrderId());
        if (order.getStatus() != OrderStatus.pending)
            throw new BadRequestException("Order status must be PENDING to add details");

        // Lấy product & kiểm tra tồn kho
        Product product = productMapper.selectByPrimaryKey(req.getProductId());
        if (product == null) throw new ResourceNotFoundException("Product", "id", req.getProductId());
        int qty = req.getNumberOfProducts();

        // Tính tiền (server-side)
        BigDecimal unitPrice = product.getPrice();
        if (unitPrice == null) throw new IllegalStateException("Product price is null");
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(qty));

        // Tạo entity và insert
        OrderDetails detail = OrderDetails.builder()
                .orderId(req.getOrderId())
                .productId(req.getProductId())
                .price(unitPrice)
                .numberOfProducts(qty)
                .totalMoney(lineTotal)
                .color(req.getColor())
                .build();

        int rows = orderDetailMapper.insert(detail);
        if (rows != 1) {
            log.error("Insert Order Details failed");
            throw new BadRequestException("Insert Order Details failed");
        }

        // Trừ kho 
        int stockRows = productMapperCustom.updateStock(req.getProductId(), qty);
        if (stockRows != 1)
            throw new BadRequestException("Not enough stock for productId=" + req.getProductId());

        // Cộng tổng tiền đơn
        int moneyRows = orderMapperCustom.totalMoneyOrder(order.getId(), lineTotal);
        if (moneyRows != 1) throw new BadRequestException("Update order total failed");

        log.info("Insert Order Details successful with ID: {}", detail.getId());

        return detail;
    }

    @Override
    public OrderDetails selectOrderDetailsByOrderId(int id) {
        log.info("Select Order Details, {}", id);
        OrderDetails exitingOrder = orderDetailMapper.selectByPrimaryKey(id);
        if(exitingOrder == null) {
            log.error("Select Order Details failed");
            throw new ResourceNotFoundException("Order", "id", id);
        }
        return orderDetailMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<OrderDetails> selectAllOrderDetails(int orderId) {
        log.info("Select all Order Details by Order Id: {}", orderId);
        List<OrderDetails> orderDetailsList = orderDetailMapperCustom.selectAllOrderDetailsByOrderId(orderId);
        if (orderDetailsList == null || orderDetailsList.isEmpty()) {
            log.error("No Order Details found for Order Id: {}", orderId);
            return List.of(); // Trả về danh sách rỗng nếu không có kết quả
        }
        return orderDetailsList;
    }

    @Override
    @Transactional
    public OrderDetails updateOrderDetails(Integer id, OrderDetailRequest req) {
        log.info("Update Order Details id={}, request={}", id, req);

        // 1) Lấy detail cũ
        OrderDetails oldDetail = orderDetailMapper.selectByPrimaryKey(id);
        if (oldDetail == null) throw new ResourceNotFoundException("OrderDetail", "id", id);

        // 2) Lấy order & check trạng thái
        Order order = orderMapper.selectByPrimaryKey(oldDetail.getOrderId());
        if (order == null) throw new ResourceNotFoundException("Order", "id", oldDetail.getOrderId());
        if (order.getStatus() != OrderStatus.pending) {
            throw new BadRequestException("Only PENDING order can be modified");
        }

        // 3) Không cho đổi orderId (tránh chuyển dòng sang đơn khác)
        if (req.getOrderId() != null && !req.getOrderId().equals(oldDetail.getOrderId())) {
            throw new BadRequestException("Changing orderId of an order detail is not allowed");
        }
        Integer orderId = oldDetail.getOrderId();

        // 4) Xác định product mới và số lượng mới
        Integer oldProductId = oldDetail.getProductId();
        Integer newProductId = (req.getProductId() != null) ? req.getProductId() : oldProductId;

        int oldQty = oldDetail.getNumberOfProducts() != null ? oldDetail.getNumberOfProducts() : 0;
        int newQty = (req.getNumberOfProducts() != null) ? req.getNumberOfProducts() : oldQty;
        if (newQty <= 0) throw new BadRequestException("numberOfProducts must be > 0");

        // 5) Lấy giá đơn vị để tính tiền
        // - Nếu KHÔNG đổi product: giữ nguyên đơn giá snapshot (oldDetail.getPrice()).
        // - Nếu ĐỔI product: lấy giá hiện tại từ products.
        BigDecimal unitPrice;
        if (newProductId.equals(oldProductId)) {
            unitPrice = oldDetail.getPrice(); // giữ snapshot cũ
            if (unitPrice == null) { // phòng khi cũ null
                Product p = productMapper.selectByPrimaryKey(newProductId);
                if (p == null) throw new ResourceNotFoundException("Product", "id", newProductId);
                unitPrice = p.getPrice();
            }
        } else {
            Product newProduct = productMapper.selectByPrimaryKey(newProductId);
            if (newProduct == null) throw new ResourceNotFoundException("Product", "id", newProductId);
            unitPrice = newProduct.getPrice();
        }

        BigDecimal oldLineTotal;
        if (oldDetail.getTotalMoney() != null) {
            // Nếu totalMoney đã được lưu trong DB thì dùng luôn
            oldLineTotal = oldDetail.getTotalMoney();
        } else if (oldDetail.getPrice() != null) {
            // Nếu chưa có totalMoney thì tính = price × số lượng
            oldLineTotal = oldDetail.getPrice().multiply(BigDecimal.valueOf(oldQty));
        } else {
            
            oldLineTotal = BigDecimal.ZERO;
        }
        BigDecimal newLineTotal = unitPrice.multiply(BigDecimal.valueOf(newQty));

        // 6) Điều chỉnh stock
        if (newProductId.equals(oldProductId)) {
            int deltaQty = newQty - oldQty;
            if (deltaQty > 0) {
                int r = productMapperCustom.updateStock(newProductId, deltaQty);
                if (r != 1) throw new BadRequestException("Not enough stock to increase quantity");
            } else if (deltaQty < 0) {
                productMapperCustom.updateIncreaseStock(newProductId, -deltaQty);
            }
        } else {
            // trả lại tồn kho sản phẩm cũ, rồi trừ kho sản phẩm mới
            productMapperCustom.updateIncreaseStock(oldProductId, oldQty);
            int r = productMapperCustom.updateStock(newProductId, newQty);
            if (r != 1) throw new BadRequestException("Not enough stock for new product");
        }

        // 7) Cập nhật OrderDetails
        OrderDetails toUpdate = OrderDetails.builder()
                .id(id)
                .orderId(orderId)
                .productId(newProductId)
                .price(unitPrice)
                .numberOfProducts(newQty)
                .totalMoney(newLineTotal)
                .color(req.getColor() != null ? req.getColor() : oldDetail.getColor())
                .build();  
        int rows = orderDetailMapper.updateByPrimaryKey(toUpdate);
        if (rows != 1) throw new IllegalArgumentException ("Update Order Detail failed");

        // 8) Cập nhật tổng tiền đơn theo delta
        BigDecimal delta = newLineTotal.subtract(oldLineTotal); // có thể âm/0/dương
        if (delta.compareTo(BigDecimal.ZERO) != 0) {
            int m = orderMapperCustom.totalMoneyOrder(orderId, delta);
            if (m != 1) throw new RuntimeException("Update order total failed");
        }


        return orderDetailMapper.selectByPrimaryKey(id);
    }


    @Override
    public void deleteByOrderId(int OrderId) {

    }

    @Override
    @Transactional
    public void deleteById(int id) {
        log.info("Delete Order Details, {}", id);

        // 1.Kiểm tra order details tồn tại
        OrderDetails orderDetails = orderDetailMapper.selectByPrimaryKey(id);
        if (orderDetails == null) {
            log.error("Delete Order Details failed");
            throw new ResourceNotFoundException("Order", "id:", id);
        }   

        // 2.Kiểm tra order tồn tại và trạng thái
        Order order = orderMapper.selectByPrimaryKey(orderDetails.getOrderId());
        if (order == null) {
            log.error("Delete Order Details failed, Order not found");
            throw new ResourceNotFoundException("Order", "id:", orderDetails.getOrderId());
        }
        if( order.getStatus() != OrderStatus.pending) {
            log.error("Delete Order Details failed, Order status is not PENDING");
            throw new BadRequestException("Only PENDING order can delete details");
        }

        // 3. Tinh tổng tiền đơn hàng cũ
        int qty = orderDetails.getNumberOfProducts() != null ? orderDetails.getNumberOfProducts() : 0;
        BigDecimal oldLineTotal;
        if (orderDetails.getTotalMoney() != null) {
            oldLineTotal = orderDetails.getTotalMoney();
        } else if (orderDetails.getPrice() != null) {
            oldLineTotal = orderDetails.getPrice().multiply(BigDecimal.valueOf(qty));
        } else {
            oldLineTotal = BigDecimal.ZERO;
        }

        // 4. Hoàn kho sản phẩm
        if(qty > 0) {
            int r = productMapperCustom.updateIncreaseStock(orderDetails.getProductId(), qty);
            if (r != 1) {
                log.error("Delete Order Details failed, Not enough stock to increase quantity");
                throw new BadRequestException("Not enough stock to increase quantity");
            }
        }

        // 5. Trừ tổng tiền đơn hàng
        if(oldLineTotal.compareTo(BigDecimal.ZERO) != 0) {
            int m = orderMapperCustom.totalMoneyOrder(orderDetails.getOrderId(), oldLineTotal.negate());
            if (m != 1) {
                log.error("Delete Order Details failed, Update order total failed");
                throw new BadRequestException("Update order total failed");
            }
        }

        // 6. Xóa order details
        int rows = orderDetailMapper.deleteByPrimaryKey(id);
        if(rows != 1) {
            log.error("Delete Order Details failed");
            throw new BadRequestException("Delete Order Details failed");
        }

        log.info("Deleted OrderDetail id={} (returned stock {}, decreased total {})", id, qty, oldLineTotal);
    }

    @Override
    public List<OrderDetails> selectAllOrderDetails() {
        log.info("Select all Order Details");
        List<OrderDetails> orderDetailsList = orderDetailMapperCustom.selectAllOrderDetails();

        return orderDetailsList;
    }
}
