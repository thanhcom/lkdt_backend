package thanhcom.site.lkdt.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import thanhcom.site.lkdt.dto.response.OrderResponse;

import java.time.OffsetDateTime;
@Data
@NoArgsConstructor
public class OrderHistoryDto {
    Long id;
    OrderDTO order;
    String action;
    String description;
    OffsetDateTime createdAt;
    String updatedBy;
}
