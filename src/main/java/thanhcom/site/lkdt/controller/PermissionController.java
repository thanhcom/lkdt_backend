package thanhcom.site.lkdt.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import thanhcom.site.lkdt.dto.PermissionDto;
import thanhcom.site.lkdt.entity.Permission;
import thanhcom.site.lkdt.mapper.PermissionMapper;
import thanhcom.site.lkdt.responseApi.ResponseApi;
import thanhcom.site.lkdt.service.PermissionService;

import java.util.List;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/permission")
public class PermissionController {
    PermissionService permissionService;
    PermissionMapper permissionMapper;

    @GetMapping("/all")
    public ResponseEntity<?> getAllPermissions(){
        List<Permission> allPermissions = permissionService.getAllPermissions();
        ResponseApi<List<PermissionDto>> responseApi = new ResponseApi<>();
        responseApi.setResponseCode(200);
        responseApi.setMessenger("Lấy danh sách quyền thành công");
        responseApi.setData(permissionMapper.toEntityList(allPermissions));
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPermissionById(@PathVariable Long id){
        Permission permission = permissionService.getPermissionById(id);
        ResponseApi<PermissionDto> responseApi = new ResponseApi<>();
        responseApi.setResponseCode(200);
        responseApi.setMessenger("Lấy quyền thành công");
        responseApi.setData(permissionMapper.toEntity(permission));
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getListPermissionByUser(@PathVariable Long id){
        List<Permission> permissionList = permissionService.getPermissionsByUser(id);
        ResponseApi<List<PermissionDto>> responseApi = new ResponseApi<>();
        responseApi.setResponseCode(200);
        responseApi.setMessenger("Lấy danh sách quyền theo người dùng thành công");
        responseApi.setData(permissionMapper.toEntityList(permissionList));
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/role/{id}")
    public ResponseEntity<?> getListPermissionByRole(@PathVariable Long id){
        List<Permission> permissionList  = permissionService.getPermissionsByRole(id);
        ResponseApi<List<PermissionDto>> responseApi = new ResponseApi<>();
        responseApi.setResponseCode(200);
        responseApi.setMessenger("Lấy danh sách quyền theo vai trò thành công");
        responseApi.setData(permissionMapper.toEntityList(permissionList));
        return ResponseEntity.ok(responseApi);
    }



}
