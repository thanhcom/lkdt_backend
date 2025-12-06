package thanhcom.site.lkdt.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thanhcom.site.lkdt.dto.OrderHistoryDto;
import thanhcom.site.lkdt.entity.OrderHistory;
import thanhcom.site.lkdt.mapper.OrderHistoryMapper;
import thanhcom.site.lkdt.responseApi.ResponseApi;
import thanhcom.site.lkdt.responseApi.ResponsePage;
import thanhcom.site.lkdt.service.OrderHistoryService;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/order-history")
public class OrderHistoryController {

    OrderHistoryService orderHistoryService;
    OrderHistoryMapper orderHistoryMapper;

    @GetMapping("/search")
    public ResponseEntity<?> searchOrderHistories(
            @RequestParam(value = "customerId", required = false) Long customerId,
            @RequestParam(value = "customerName", required = false) String customerName,
            @RequestParam(value = "componentId", required = false) Long componentId,
            @RequestParam(value = "componentName", required = false) String componentName,
            @RequestParam(value = "action", required = false) String action,
            @RequestParam(value = "updatedBy", required = false) String updatedBy,
            @RequestParam(value = "createdFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime createdFrom,
            @RequestParam(value = "createdTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime createdTo,
            Pageable pageable
    ) {
        Page<OrderHistory> page = orderHistoryService.searchOrderHistories(
                customerId, customerName,
                componentId, componentName,
                createdFrom, createdTo,
                action, updatedBy,
                pageable
        );

        Page<OrderHistoryDto> dtoPage = page.map(orderHistoryMapper::toDto);

        ResponseApi<List<OrderHistoryDto>> responseApi = new ResponseApi<>();
        responseApi.setData(dtoPage.getContent());
        responseApi.setPageInfo(ResponsePage.builder()
                .currentPage(page.getNumber() + 1)
                .pageSize(page.getSize())
                .totalPage(page.getTotalPages())
                .totalElement(page.getTotalElements())
                .isEmpty(page.isEmpty())
                .isFirst(page.isFirst())
                .isLast(page.isLast())
                .hashCode(page.hashCode())
                .sortInfo(page.getSort().toString())
                .hasNext(page.hasNext())
                .hasContent(page.hasContent())
                .hasPrevious(page.hasPrevious())
                .build()
        );

        responseApi.setResponseCode(2000);
        responseApi.setMessenger("Tìm kiếm lịch sử đơn hàng thành công");
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderHistoryById(@PathVariable Long id) {
        OrderHistory history = orderHistoryService.getOrderHistoryById(id);
        ResponseApi<OrderHistoryDto> responseApi = new ResponseApi<>();
        responseApi.setData(orderHistoryMapper.toDto(history));
        responseApi.setResponseCode(2001);
        responseApi.setMessenger("Lấy lịch sử đơn hàng theo ID thành công");
        return ResponseEntity.ok(responseApi);
    }
}
