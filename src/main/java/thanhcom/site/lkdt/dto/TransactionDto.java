package thanhcom.site.lkdt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private Long id;
    private String transactionType;
    private Integer quantity;
    private OffsetDateTime transactionDate;
    private String note;
    private ComponentDto component;
    private ProjectDto project;
}
