package thanhcom.site.lkdt.mapper;

import org.mapstruct.Mapper;
import thanhcom.site.lkdt.dto.request.SupplierPriceRequest;
import thanhcom.site.lkdt.dto.response.ComponentSupplierResponse;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SupplierPriceMapper {
    ComponentSupplierResponse toRequest(SupplierPriceRequest request);
    SupplierPriceRequest toResponse(ComponentSupplierResponse response);

    List<SupplierPriceRequest> toRequestList(List<ComponentSupplierResponse> responses);
    List<ComponentSupplierResponse> toResponseList(List<SupplierPriceRequest> requests);
}
