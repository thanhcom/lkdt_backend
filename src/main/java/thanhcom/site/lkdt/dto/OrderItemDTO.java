package thanhcom.site.lkdt.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDTO {

    private Long componentId;   // ID linh kiện
    private Integer quantity;   // số lượng
    private BigDecimal price;   // giá thời điểm mua
    private BigDecimal total;   // tổng = quantity * price
}
