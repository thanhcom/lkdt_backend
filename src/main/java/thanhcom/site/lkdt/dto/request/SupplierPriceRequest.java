package thanhcom.site.lkdt.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SupplierPriceRequest {
    private Long supplierId;
    private BigDecimal price;
    private Integer leadTime;
}