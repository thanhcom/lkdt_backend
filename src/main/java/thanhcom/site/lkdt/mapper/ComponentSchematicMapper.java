package thanhcom.site.lkdt.mapper;

import org.mapstruct.Mapper;
import thanhcom.site.lkdt.dto.ComponentSchematicDto;
import thanhcom.site.lkdt.entity.ComponentSchematic;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComponentSchematicMapper {
    ComponentSchematic toEntity(ComponentSchematicDto componentSchematicDto);
    ComponentSchematicDto toDto(ComponentSchematic componentSchematic);

    List<ComponentSchematic> toListEntity(List<ComponentSchematicDto> componentSchematicDto);
    List<ComponentSchematicDto> toListDto(List<ComponentSchematic> componentSchematic);
}
