package thanhcom.site.lkdt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import thanhcom.site.lkdt.dto.response.ComponentSupplierResponse;
import thanhcom.site.lkdt.entity.Transaction;

import java.time.OffsetDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private List<ComponentSupplierResponse> componentSuppliers;
}
