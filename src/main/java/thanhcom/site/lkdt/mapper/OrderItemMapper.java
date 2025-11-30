package thanhcom.site.lkdt.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import thanhcom.site.lkdt.dto.OrderItemDTO;
import thanhcom.site.lkdt.entity.OrderItem;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "component.id", target = "componentId")
    OrderItemDTO toDTO(OrderItem entity);
    List<OrderItemDTO> toDTOs(List<OrderItem> entityList);

    @Mapping(target = "order", ignore = true)        // được gán trong OrderService
    @Mapping(target = "component.id", source = "componentId")
    OrderItem toEntity(OrderItemDTO dto);
    List<OrderItem> toEntities(List<OrderItemDTO> dtoList);
}
