package thanhcom.site.lkdt.mapper;

import org.mapstruct.Mapper;
import thanhcom.site.lkdt.dto.OrderHistoryDto;
import thanhcom.site.lkdt.entity.OrderHistory;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderHistoryMapper {
    OrderHistoryDto toDto(OrderHistory orderHistory);
    OrderHistory toEntity(OrderHistoryDto orderHistoryDto);

    List<OrderHistoryDto> toDtoList(List<OrderHistory> orderHistories);
    List<OrderHistory> toEntityList(List<OrderHistoryDto> orderHistoryDtos);
}
