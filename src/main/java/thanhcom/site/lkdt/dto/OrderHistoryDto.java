package thanhcom.site.lkdt.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
@Data
@NoArgsConstructor
public class OrderHistoryDto {
    Long id;
    Long orderId;
    String action;
    String description;
    OffsetDateTime createdAt;
    String updatedBy;
}
