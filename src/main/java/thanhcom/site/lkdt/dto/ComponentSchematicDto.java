package thanhcom.site.lkdt.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentSchematicDto {
    private Long id;
    private String schematicName;
    private String schematicFile;
    private List<String> schematicImages; // List URL ảnh
    private String description;
    private OffsetDateTime createdAt;
}
