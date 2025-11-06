package thanhcom.site.lkdt.dto;

import lombok.Builder;
import lombok.Data;
import thanhcom.site.lkdt.dto.response.ComponentSupplierResponse;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class ComponentDetail {
    private String name;
    private String type;
    private String specification;
    private String manufacturer;
    private String packageField;
    private String unit;
    private Integer stockQuantity;
    private String location;
    private OffsetDateTime createdAt;
    List<ComponentSupplierResponse> componentSuppliers;
}
