package thanhcom.site.lkdt.dto.response;

import lombok.Data;
import thanhcom.site.lkdt.dto.SupplierDto;

@Data
public class ComponentSupplierResponse {
    private SupplierDto supplier;
    //private ComponentResponse component;
    private Double price;
    private Integer leadTime;
}