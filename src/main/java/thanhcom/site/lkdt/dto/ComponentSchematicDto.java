package thanhcom.site.lkdt.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentSchematicDto {
    private Long id;
    private String schematicName;
    private String schematicFile;
    private byte[] schematicImage;
    private String description;
    private OffsetDateTime createdAt;
}
