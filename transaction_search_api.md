# API Quản lý Transaction

API này cho phép tìm kiếm các `Transaction` theo nhiều tiêu chí: **component, project, type, thời gian**, hỗ trợ **phân trang**, trả về **DTO** để client sử dụng dễ dàng.

---

## Endpoint

```
GET /transactions/search
```

## Tham số Request

| Tham số         | Kiểu dữ liệu        | Bắt buộc | Mô tả |
|-----------------|------------------|----------|-------|
| `componentId`   | Long             | Không     | Lọc transaction theo **ID của component** |
| `componentName` | String           | Không     | Lọc transaction theo **tên component** (tìm gần đúng, không phân biệt hoa thường) |
| `projectId`     | Long             | Không     | Lọc transaction theo **ID của project** |
| `projectName`   | String           | Không     | Lọc transaction theo **tên project** (tìm gần đúng, không phân biệt hoa thường) |
| `type`          | String           | Không     | Lọc transaction theo **loại transaction** |
| `start`         | OffsetDateTime   | Không     | Lọc transaction từ thời điểm này trở đi (ISO-8601, ví dụ: `2025-11-13T12:00:00Z`) |
| `end`           | OffsetDateTime   | Không     | Lọc transaction đến thời điểm này (ISO-8601) |
| `page`          | int              | Không     | Số trang, mặc định = 0 |
| `size`          | int              | Không     | Kích thước trang, mặc định = 10 |

> Nếu không truyền tham số nào, API trả về tất cả transaction (có phân trang).

---

## Ví dụ Request

1. **Tìm tất cả transaction của component ID 5, page 0, size 10**

```
GET /transactions/search?componentId=5&page=0&size=10
```

2. **Tìm transaction của project "Dự án A" và loại "IMPORT" từ ngày 2025-11-01 đến 2025-11-13**

```
GET /transactions/search?projectName=Dự%20án%20A&type=IMPORT&start=2025-11-01T00:00:00Z&end=2025-11-13T23:59:59Z
```

3. **Tìm transaction của component "IC 555" và project ID 3**

```
GET /transactions/search?componentName=IC%20555&projectId=3
```
4. **Tìm transaction và sắp sếp theo component ID 3**

```
GET /transactions/search?sort=componentId,desc
```

---

## Ví dụ Response

```json
{
  "content": [
    {
      "id": 1,
      "componentName": "IC 555",
      "projectName": "Dự án A",
      "transactionType": "IMPORT",
      "transactionDate": "2025-11-13T08:00:00Z"
    },
    {
      "id": 2,
      "componentName": "IC 556",
      "projectName": "Dự án A",
      "transactionType": "IMPORT",
      "transactionDate": "2025-11-12T10:00:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5
  },
  "totalPages": 2,
  "totalElements": 8
}
```

---

## Ghi chú kỹ thuật

### 1. Spring Data JPA 3.5.x

- Không còn dùng `Specification.where()` vì deprecated.
- Kết hợp nhiều `Specification` bằng `.and()`:

```java
Specification<Transaction> spec = TransactionSpecification.hasComponentId(componentId)
        .and(TransactionSpecification.hasProjectId(projectId))
        .and(TransactionSpecification.hasComponentName(componentName))
        .and(TransactionSpecification.hasProjectName(projectName))
        .and(TransactionSpecification.hasTransactionType(type))
        .and(TransactionSpecification.betweenDates(start, end));
```

- Sau đó gọi repository:

```java
Page<Transaction> transactions = transactionRepository.findAll(spec, PageRequest.of(page, size));
```

### 2. Mapping Entity → DTO

- Sử dụng mapper để trả về DTO cho client:

```java
return transactions.map(transactionMapper::ToEntity);
```

### 3. Định dạng thời gian

- `OffsetDateTime` phải truyền theo chuẩn ISO-8601:  
  `yyyy-MM-dd'T'HH:mm:ssXXX`  
  Ví dụ: `2025-11-13T12:00:00Z`

---

## Hướng dẫn test bằng Postman

1. Mở Postman → Chọn **GET**.
2. URL: `http://localhost:8080/transactions/search`
3. Thêm query params như bảng tham số phía trên.
4. Nhấn **Send** → Xem JSON trả về.
5. Thử kết hợp nhiều filter để kiểm tra các trường hợp.