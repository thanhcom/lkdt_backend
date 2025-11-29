package thanhcom.site.lkdt.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class ComponentCreateRequest {
    private String name;
    private String type;
    private String manufacturer;
    private String packageField;
    private String unit;
    private String location;
    private Integer stockQuantity;
    private String specification;
    private List<SupplierPriceRequest> suppliers;
}