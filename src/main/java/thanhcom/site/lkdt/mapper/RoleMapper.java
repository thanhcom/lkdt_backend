package thanhcom.site.lkdt.mapper;

import org.mapstruct.Mapper;
import thanhcom.site.lkdt.dto.request.RoleRequest;
import thanhcom.site.lkdt.dto.response.RoleResponse;
import thanhcom.site.lkdt.entity.Role;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleRequest reqToDto(String roleName);
    Role dtoToReq(RoleRequest roleRequest);
    Set<Role> listDtoToReq(Set<RoleRequest> roleRequests);
    Set<RoleRequest> listReqToDto(Set<String> roleNames);


    RoleResponse resToDto(Role role);
    Role dtoToRes(RoleResponse roleRepository);
    Set<Role> listDtoToRes(Set<RoleResponse> roleRepositories);
    Set<RoleResponse> listResToDto(Set<Role> roles);
}
