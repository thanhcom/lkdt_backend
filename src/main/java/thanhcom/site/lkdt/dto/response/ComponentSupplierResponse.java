package thanhcom.site.lkdt.dto.response;

import lombok.Data;

@Data
public class ComponentSupplierResponse {
    private SupplierResponse supplier;
    //private ComponentResponse component;
    private Double price;
    private Integer leadTime;
}