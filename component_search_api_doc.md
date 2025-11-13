# Tài liệu API Tìm kiếm Component

## 1. Endpoint

| Phương thức | URL | Mô tả |
|------------|-----|------|
| GET | `/components/search` | Tìm kiếm các component theo keyword, id, stockQuantity, phân trang và sắp xếp |

---

## 2. Tham số Query

| Tham số | Bắt buộc | Kiểu | Mô tả |
|---------|----------|------|------|
| `keyword` | Không | String | Từ khóa tìm kiếm trên nhiều trường text (`name`, `type`, `manufacturer`, `packageField`, `location`) |
| `id` | Không | Long | Lọc theo ID component cụ thể |
| `stockQuantity` | Không | Integer | Lọc theo số lượng tồn kho |
| `page` | Không | Integer | Trang hiện tại (bắt đầu từ 0). Mặc định: 0 |
| `size` | Không | Integer | Số bản ghi trên mỗi trang. Mặc định: 20 |
| `sort` | Không | String | Sắp xếp theo trường, ví dụ: `sort=name,asc` hoặc `sort=stockQuantity,desc`. Có thể dùng nhiều trường sort |

---

## 3. Ví dụ URL

### 3.1 Chỉ tìm kiếm keyword
```
GET /components/search?keyword=ST
```

### 3.2 Tìm theo ID
```
GET /components/search?id=5
```

### 3.3 Tìm theo stockQuantity
```
GET /components/search?stockQuantity=10
```

### 3.4 Kết hợp keyword + ID + stockQuantity
```
GET /components/search?keyword=ST&id=5&stockQuantity=10
```

### 3.5 Phân trang và sắp xếp
```
GET /components/search?keyword=ST&page=1&size=5&sort=name,asc
```
- Sắp xếp nhiều trường:
```
GET /components/search?keyword=ST&sort=name,asc&sort=stockQuantity,desc
```

### 3.6 Không truyền tham số
```
GET /components/search
```
- Trả về tất cả component, page=0, size=20, không sắp xếp

---

## 4. Response JSON

```json
{
  "currentPage": 1,
  "pageSize": 5,
  "totalPages": 3,
  "totalElements": 12,
  "content": [
    {
      "id": 5,
      "name": "ST-Motor",
      "type": "Motor",
      "manufacturer": "ACME",
      "packageField": "0603",
      "stockQuantity": 10,
      "location": "Shelf A1"
    }
  ]
}
```

### Giải thích các trường

| Trường | Mô tả |
|--------|------|
| `currentPage` | Trang hiện tại mà client đang đứng (bắt đầu từ 1) |
| `pageSize` | Số bản ghi trên mỗi trang |
| `totalPages` | Tổng số trang có thể |
| `totalElements` | Tổng số bản ghi thỏa điều kiện tìm kiếm |
| `content` | Danh sách component trong trang hiện tại |

---

## 5. Ghi chú sử dụng

1. Tìm kiếm theo keyword là **không phân biệt hoa thường** và **contains**: tìm tất cả record mà keyword xuất hiện trong các trường text.
2. `id` và `stockQuantity` chỉ áp dụng khi có giá trị.
3. Phân trang mặc định: page=0, size=20, không sắp xếp nếu không truyền.
4. Có thể truyền nhiều trường sort; áp dụng theo thứ tự.
5. Kết hợp các filter với phân trang và sắp xếp để tìm kiếm linh hoạt.

---

## 6. Ví dụ test bằng Postman

- Phương thức: GET
- URL: `/components/search`
- Params:

| Key | Value |
|-----|-------|
| keyword