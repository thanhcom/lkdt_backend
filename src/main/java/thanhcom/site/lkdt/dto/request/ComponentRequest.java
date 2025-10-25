package thanhcom.site.lkdt.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ComponentRequest {
    @NotBlank(message = "COMPONENT_NAME_BLANK")
    private String name;
    @Size(min = 2 , max = 50, message = "COMPONENT_NAME_SIZE")
    private String type;
    private String specification;
    private String manufacturer;
    private String packageField;
    private String unit;
    private Integer stockQuantity;
    private String location;
    private OffsetDateTime createdAt;
}