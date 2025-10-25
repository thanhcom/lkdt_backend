package thanhcom.site.lkdt.mapper;

import org.mapstruct.Mapper;
import thanhcom.site.lkdt.dto.request.ComponentRequest;
import thanhcom.site.lkdt.dto.response.ComponentResponse;
import thanhcom.site.lkdt.entity.Component;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComponentMapper {
    // Mapping methods Response
    ComponentResponse ResToDto(Component component);
    Component ResToEntity(ComponentResponse componentResponse);
    List<ComponentResponse> ResToDtoList(List<Component> components);
    List<Component> ResToEntityList(List<ComponentResponse> componentResponses);
    // Mapping methods Request
    ComponentRequest ReqToDto(Component component);
    Component ReqToEntity(ComponentRequest componentRequest);
    List<ComponentRequest> ReqToDtoList(List<Component> components);
    List<Component> ReqToEntityList(List<ComponentRequest> componentRequests);
}
