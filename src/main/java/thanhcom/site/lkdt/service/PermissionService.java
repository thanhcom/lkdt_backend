package thanhcom.site.lkdt.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import thanhcom.site.lkdt.entity.Permission;
import thanhcom.site.lkdt.entity.Role;
import thanhcom.site.lkdt.repository.PermissionRepository;
import thanhcom.site.lkdt.repository.RoleRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    RoleRepository roleRepository;
    public List<Permission> getAllPermissions(){
        return permissionRepository.findAll();
    }
    public Permission getPermissionById(Long id){
        return permissionRepository.findById(id).orElse(null);
    }

    public List<Permission> getPermissionsByUser(Long userId){
        return permissionRepository.getPermissionsByUser(userId);
    }

    public List<Permission> getPermissionsByRole(Long roleId){
        Role role = roleRepository.findById(roleId).orElse(null);
        if(role != null){
            return new ArrayList<>(role.getPermissions());
        }
        return new ArrayList<>();
    }

}
