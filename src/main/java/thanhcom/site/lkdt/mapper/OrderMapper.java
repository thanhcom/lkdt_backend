package thanhcom.site.lkdt.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import thanhcom.site.lkdt.dto.OrderDTO;
import thanhcom.site.lkdt.dto.response.OrderResponse;
import thanhcom.site.lkdt.entity.Orders;

import java.util.List;

@Mapper(componentModel = "spring", uses = { OrderItemMapper.class ,  DateTimeMapper.class})
public interface OrderMapper {
    @Mapping(source = "customer.id", target = "customerId")
    OrderDTO toDTO(Orders entity);

    // Map OrderDTO → Orders entity
    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(target = "items", ignore = true)   // gán thủ công trong Service
    @Mapping(target = "totalAmount", ignore = true)
    Orders toEntity(OrderDTO dto);

    OrderResponse toResponse(Orders entity);
    Orders toEntityFromResponse(OrderResponse dto);
    List<OrderResponse> toResponses(List<Orders> entities);

}
