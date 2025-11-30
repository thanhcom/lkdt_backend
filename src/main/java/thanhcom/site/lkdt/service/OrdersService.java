package thanhcom.site.lkdt.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import thanhcom.site.lkdt.entity.Customer;
import thanhcom.site.lkdt.entity.Orders;
import thanhcom.site.lkdt.repository.OrdersRepository;
import thanhcom.site.lkdt.specification.CustomerSpecification;
import thanhcom.site.lkdt.specification.OrdersSpecification;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrdersService {
    OrdersRepository ordersRepository;

    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }

    public Page<Orders> searchOrders(
            Long orderId,
            Long customerId,
            BigDecimal minTotal,
            BigDecimal maxTotal,
            OffsetDateTime dateFrom,
            OffsetDateTime dateTo,
            String keyword,
            Pageable pageable
    ) {
        Specification<Orders> spec = OrdersSpecification.hasOrderId(orderId)
                .and(OrdersSpecification.hasCustomer(customerId))
                .and(OrdersSpecification.hasMinTotal(minTotal))
                .and(OrdersSpecification.hasMaxTotal(maxTotal))
                .and(OrdersSpecification.dateFrom(dateFrom))
                .and(OrdersSpecification.dateTo(dateTo))
                .and(OrdersSpecification.keyword(keyword));

        return ordersRepository.findAll(spec, pageable);
    }


    public Orders createOrder(Orders order) {
        return ordersRepository.save(order);
    }

    public Orders getOrderById(Long id) {
        return ordersRepository.findById(id).orElse(null);
    }

    public Orders updateOrder(Long id, Orders updatedOrder) {
        return ordersRepository.findById(id).map(order -> {
            order.setOrderDate(updatedOrder.getOrderDate());
            order.setStatus(updatedOrder.getStatus());
            order.setTotalAmount(updatedOrder.getTotalAmount());
            return ordersRepository.save(order);
        }).orElse(null);
    }

    public void deleteOrder(Long id) {
        ordersRepository.findById(id).map(order -> {
            ordersRepository.delete(order);
            return true;
        });
    }
}
