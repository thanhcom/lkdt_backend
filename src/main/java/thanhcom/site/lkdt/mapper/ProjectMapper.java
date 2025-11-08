package thanhcom.site.lkdt.mapper;

import org.mapstruct.Mapper;
import thanhcom.site.lkdt.dto.ProjectDto;
import thanhcom.site.lkdt.entity.Project;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDto toEntity(Project project);
    Project toDto(ProjectDto project);

    List<ProjectDto> toEntityList(List<Project> projects);
    List<Project> toDtoList(List<ProjectDto> projectDtos);
}
