package thanhcom.site.lkdt.dto.response;

import lombok.Builder;
import lombok.Data;
import thanhcom.site.lkdt.dto.TransactionDto;

import java.time.OffsetDateTime;

import java.util.List;

@Data
@Builder
public class ComponentResponse {
    private Long id;                      // 👈 nên có để client biết ID component
    private String name;
    private String type;
    private String specification;
    private String manufacturer;
    private String packageField;
    private String unit;
    private Integer stockQuantity;
    private String location;
    private OffsetDateTime createdAt;
}