package thanhcom.site.lkdt.mapper;

import org.mapstruct.Mapper;
import thanhcom.site.lkdt.dto.response.SupplierResponse;
import thanhcom.site.lkdt.entity.Supplier;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierResponse ResToDto(Supplier supplier);
}
