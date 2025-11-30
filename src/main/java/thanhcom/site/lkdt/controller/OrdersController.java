package thanhcom.site.lkdt.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thanhcom.site.lkdt.dto.CustomerDTO;
import thanhcom.site.lkdt.dto.OrderDTO;
import thanhcom.site.lkdt.dto.response.OrderResponse;
import thanhcom.site.lkdt.entity.Orders;
import thanhcom.site.lkdt.mapper.OrderMapper;
import thanhcom.site.lkdt.responseApi.ResponseApi;
import thanhcom.site.lkdt.responseApi.ResponsePage;
import thanhcom.site.lkdt.service.OrdersService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("orders")
public class OrdersController {
    OrdersService ordersService;
    OrderMapper orderMapper;

    @GetMapping("/all")
    public ResponseEntity<?> getAllComponents() {
        List<OrderResponse> orderResponses = orderMapper.toResponses(ordersService.getAllOrders());
        ResponseApi<List<OrderResponse>> responseApi = new ResponseApi<>();
        responseApi.setData(orderResponses);
        responseApi.setResponseCode(2001);
        responseApi.setMessenger("Lấy danh sách đơn hàng thành công");
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchOrders(
            @RequestParam(value = "orderId", required = false) Long orderId,
            @RequestParam(value = "customerId", required = false) Long customerId,
            @RequestParam(value = "minTotal", required = false) BigDecimal minTotal,
            @RequestParam(value = "maxTotal", required = false) BigDecimal maxTotal,
            @RequestParam(value = "dateFrom", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateFrom,
            @RequestParam(value = "dateTo", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dateTo,
            @RequestParam(value = "keyword", required = false) String keyword,
            Pageable pageable
    ) {
        ResponseApi<List<?>> responseApi = new ResponseApi<>();
        Page<Orders> page = ordersService.searchOrders(
                orderId, customerId, minTotal, maxTotal, dateFrom, dateTo, keyword, pageable
        );
        Page<OrderResponse> dtoPage = page.map(orderMapper::toResponse);
        responseApi.setResponseCode(2001);
        responseApi.setMessenger("Tìm kiếm đơn hàng thành công");
        responseApi.setData(dtoPage.getContent());
        int pageNo = page.getNumber() + 1; // 0-based → 1-based
        responseApi.setPageInfo(
                ResponsePage.builder()
                        .currentPage(pageNo)
                        .pageSize(page.getSize())
                        .totalPage(page.getTotalPages())
                        .totalElement(page.getTotalElements())
                        .isEmpty(page.isEmpty())
                        .isFirst(page.isFirst())
                        .isLast(page.isLast())
                        .hasNext(page.hasNext())
                        .hasPrevious(page.hasPrevious())
                        .sortInfo(page.getSort().toString())
                        .hasContent(page.hasContent())
                        .hashCode(page.hashCode())
                        .build()
        );
        return ResponseEntity.ok(responseApi);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        OrderResponse orderResponse = orderMapper.toResponse(ordersService.getOrderById(id));
        ResponseApi<OrderResponse> responseApi = new ResponseApi<>();
        responseApi.setData(orderResponse);
        responseApi.setResponseCode(2001);
        responseApi.setMessenger("Lấy đơn hàng thành công");
        return ResponseEntity.ok(responseApi);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDTO) {
        OrderResponse orderResponse = orderMapper.toResponse(ordersService.createOrder(orderMapper.toEntity(orderDTO)));
        ResponseApi<OrderResponse> responseApi = new ResponseApi<>();
        responseApi.setData(orderResponse);
        responseApi.setResponseCode(2002);
        responseApi.setMessenger("Tạo đơn hàng thành công");
        return ResponseEntity.ok(responseApi);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody OrderDTO orderDTO) {
        OrderResponse orderResponse = orderMapper.toResponse(ordersService.updateOrder(id, orderMapper.toEntity(orderDTO)));
        ResponseApi<OrderResponse> responseApi = new ResponseApi<>();
        responseApi.setData(orderResponse);
        responseApi.setResponseCode(2003);
        responseApi.setMessenger("Cập nhật đơn hàng thành công");
        return ResponseEntity.ok(responseApi);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        ordersService.deleteOrder(id);
        ResponseApi<Void> responseApi = new ResponseApi<>();
        responseApi.setResponseCode(2004);
        responseApi.setMessenger("Xóa đơn hàng thành công");
        return ResponseEntity.ok(responseApi);
    }
}
