package thanhcom.site.lkdt.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {

    private Long customerId;            // khách mua hàng
    private List<OrderItemDTO> items;   // danh sách sản phẩm
}


