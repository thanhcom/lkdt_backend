package thanhcom.site.lkdt.dto.response;

import lombok.Data;
import thanhcom.site.lkdt.dto.OrderItemDTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private Long customerId;
    private OffsetDateTime orderDate;
    private BigDecimal totalAmount;
    private String status;
    private List<OrderItemDTO> items;
}