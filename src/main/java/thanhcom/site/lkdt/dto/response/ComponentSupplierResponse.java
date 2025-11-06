package thanhcom.site.lkdt.dto.response;

import lombok.Data;
import thanhcom.site.lkdt.entity.Component;
import thanhcom.site.lkdt.entity.Supplier;

@Data
public class ComponentSupplierResponse {
    private Supplier supplier;
    private Component component;
    private Double price;
    private Integer leadTime;
}