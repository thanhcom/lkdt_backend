package thanhcom.site.lkdt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    OffsetDateTime createdAt;
}
