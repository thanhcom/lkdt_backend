package thanhcom.site.lkdt.mapper;

import org.mapstruct.Mapper;
import thanhcom.site.lkdt.dto.SupplierDto;
import thanhcom.site.lkdt.entity.Supplier;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierDto ResToDto(Supplier supplier);
    List<SupplierDto> ResToDtoList(List<Supplier> suppliers);


    Supplier DtoToRes(SupplierDto supplierResponse);
    List<Supplier> DtoToResList(List<SupplierDto> supplierResponses);
}
