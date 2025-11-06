package thanhcom.site.lkdt.mapper;

import org.mapstruct.Mapper;
import thanhcom.site.lkdt.dto.response.ComponentSupplierResponse;
import thanhcom.site.lkdt.entity.ComponentSupplier;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComponentSupplierMapper {
    ComponentSupplierResponse ResToEntity(ComponentSupplier componentSupplierMapper);
    List<ComponentSupplierResponse> ResToEntityList(List<ComponentSupplier> componentSupplierMappers);

    ComponentSupplier EntityToRes(ComponentSupplierResponse response);
    List<ComponentSupplier> EntityToResList(List<ComponentSupplierResponse> responses);
}
