# Orders Search API Documentation

## 1. Endpoint

```
GET /api/orders/search
```

### Mô tả
API này cho phép tìm kiếm đơn hàng (`Orders`) dựa trên nhiều điều kiện lọc động, hỗ trợ phân trang (pagination) và sắp xếp (sorting).

---

## 2. Tham số query

| Tham số | Loại | Mặc định | Mô tả | Ví dụ |
|---------|------|----------|-------|-------|
| `orderId` | Long | - | Lọc theo ID đơn hàng | `?orderId=123` |
| `customerId` | Long | - | Lọc theo ID khách hàng | `?customerId=5` |
| `minTotal` | BigDecimal | - | Tổng đơn tối thiểu | `?minTotal=1000` |
| `maxTotal` | BigDecimal | - | Tổng đơn tối đa | `?maxTotal=5000` |
| `dateFrom` | OffsetDateTime | - | Ngày bắt đầu lọc | `?dateFrom=2025-11-01T00:00:00Z` |
| `dateTo` | OffsetDateTime | - | Ngày kết thúc lọc | `?dateTo=2025-11-30T23:59:59Z` |
| `keyword` | String | - | Tìm kiếm tên khách hàng (customer) | `?keyword=thanh` |
| `page` | int | 0 | Trang hiện tại (0-based) | `?page=0` |
| `size` | int | 10 | Số item trên mỗi trang | `?size=20` |
| `sort` | String | - | Sắp xếp theo field và thứ tự `asc/desc` | `?sort=orderDate,desc` |

---

## 3. Response

```json
{
  "responseCode": 2001,
  "messenger": "Tìm kiếm đơn hàng thành công",
  "data": [
    { /* OrderResponse DTO */ }
  ],
  "pageInfo": {
    "currentPage": 1,
    "pageSize": 20,
    "totalPage": 5,
    "totalElement": 100,
    "isEmpty": false,
    "isFirst": true,
    "isLast": false,
    "hasNext": true,
    "hasPrevious": false,
    "sortInfo": "orderDate: DESC",
    "hasContent": true,
    "hashCode": 12345678
  }
}
```

### Giải thích các trường `pageInfo`
- `currentPage`: Trang hiện tại (1-based)
- `pageSize`: Số item trên mỗi trang
- `totalPage`: Tổng số trang
- `totalElement`: Tổng số đơn hàng thỏa mãn điều kiện
- `isEmpty`: Trang hiện tại có rỗng không
- `isFirst`: Có phải trang đầu tiên không
- `isLast`: Có phải trang cuối không
- `hasNext`: Có trang kế tiếp không
- `hasPrevious`: Có trang trước không
- `sortInfo`: Thông tin sắp xếp
- `hasContent`: Có dữ liệu không
- `hashCode`: Hashcode của page (tham khảo, không bắt buộc)

---

## 4. Ví dụ Request

```
GET /api/orders/search?customerId=5&minTotal=100&keyword=thanh&page=0&size=20&sort=orderDate,desc
```

### Mô tả
- Tìm các đơn hàng của khách hàng có ID = 5
- Tổng tiền ≥ 100
- Tên khách hàng chứa "thanh" (không phân biệt hoa thường)
- Phân trang: trang 0, 20 đơn hàng/trang
- Sắp xếp theo `orderDate` giảm dần

---

## 5. Chú ý

1. Nếu tham số không truyền → sẽ không lọc theo trường đó.
2. Keyword search dùng `LIKE` với `%keyword%`, **không phân biệt chữ hoa/chữ thường**.
3. Pagination và sorting hoàn toàn dựa trên `Pageable`.
4. Controller sẽ map `Orders` entity → `OrderResponse` DTO.
5. Specification hỗ trợ lọc động, null parameters sẽ được bỏ qua.

---

## 6. Quy trình xử lý

1. Client gọi `GET /api/orders/search` với các query params.
2. Controller nhận request → gọi `OrdersService.searchOrders(...)`.
3. Service tạo Specification từ tất cả params.
4. Repository thực hiện `findAll(spec, pageable)` → trả về `Page<Orders>`.
5. Controller map entity → DTO, thêm paging info, trả về `ResponseApi` JSON.

---

**Hoàn tất**.

