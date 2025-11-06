package thanhcom.site.lkdt.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class SupplierResponse {
    private Long id;
    private String name;
    private String contact;
    private String email;
    private String phone;
    private String address;
    private OffsetDateTime createdAt;
}
