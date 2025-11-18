package thanhcom.site.lkdt.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import thanhcom.site.lkdt.dto.ComponentSchematicDto;
import thanhcom.site.lkdt.entity.ComponentSchematic;

@Mapper(componentModel = "spring")
public interface ComponentSchematicMapper {

    @Mapping(target = "schematicImages", source = "schematicImage", qualifiedByName = "stringToList")
    ComponentSchematicDto toDto(ComponentSchematic entity);

    @Mapping(target = "schematicImage", source = "schematicImages", qualifiedByName = "listToString")
    ComponentSchematic toEntity(ComponentSchematicDto dto);

    List<ComponentSchematic> toListEntity(List<ComponentSchematicDto> dtoList);

    List<ComponentSchematicDto> toListDto(List<ComponentSchematic> entityList);

    @Named("stringToList")
    static List<String> stringToList(String schematicImage) {
        if (schematicImage == null || schematicImage.isBlank()) return List.of();
        return Arrays.asList(schematicImage.split(","));
    }

    @Named("listToString")
    static String listToString(List<String> schematicImages) {
        if (schematicImages == null || schematicImages.isEmpty()) return "";
        return String.join(",", schematicImages);
    }
}

