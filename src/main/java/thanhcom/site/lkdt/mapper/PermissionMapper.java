package thanhcom.site.lkdt.mapper;

import org.mapstruct.Mapper;
import thanhcom.site.lkdt.dto.PermissionDto;
import thanhcom.site.lkdt.entity.Permission;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionDto toEntity(Permission permission);
    Permission toDto(PermissionDto permissionDto);

    List<PermissionDto> toEntityList(List<Permission> permissions);
    List<Permission> toDtoList(List<PermissionDto> permissionDtos);
}
