package thanhcom.site.lkdt.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import thanhcom.site.lkdt.dto.OrderDTO;
import thanhcom.site.lkdt.dto.OrderHistoryDto;
import thanhcom.site.lkdt.dto.OrderItemDTO;
import thanhcom.site.lkdt.entity.OrderHistory;
import thanhcom.site.lkdt.entity.OrderItem;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderHistoryMapper {

    // --- Map một OrderHistory sang OrderHistoryDto ---
    @Mapping(target = "order", expression = "java(mapOrder(orderHistory.getOrder()))")
    OrderHistoryDto toDto(OrderHistory orderHistory);

    // --- Map list ---
    default List<OrderHistoryDto> toDtoList(List<OrderHistory> orderHistories) {
        return orderHistories.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // --- Map DTO sang entity (ngược lại) ---
    // Nếu bạn muốn hỗ trợ, có thể implement tương tự
    OrderHistory toEntity(OrderHistoryDto orderHistoryDto);

    default List<OrderHistory> toEntityList(List<OrderHistoryDto> dtos) {
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    // --- Mapping nested Order sang OrderDTO ---
    default OrderDTO mapOrder(thanhcom.site.lkdt.entity.Orders  order) {
        if (order == null) return null;
        OrderDTO dto = new OrderDTO();
        if (order.getCustomer() != null) {
            dto.setCustomerId(order.getCustomer().getId());
        }
        if (order.getItems() != null) {
            List<OrderItemDTO> items = order.getItems().stream()
                    .map(this::mapOrderItem)
                    .collect(Collectors.toList());
            dto.setItems(items);
        }
        return dto;
    }

    // --- Mapping OrderItem sang OrderItemDTO ---
    default OrderItemDTO mapOrderItem(OrderItem item) {
        if (item == null) return null;
        OrderItemDTO dto = new OrderItemDTO();
        if (item.getComponent() != null) {
            dto.setComponentId(item.getComponent().getId());
            dto.setComponentName(item.getComponent().getName());
        }
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setTotal(item.getTotal());
        return dto;
    }
}
