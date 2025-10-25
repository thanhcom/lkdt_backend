package thanhcom.site.lkdt.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thanhcom.site.lkdt.dto.request.ComponentRequest;
import thanhcom.site.lkdt.dto.response.ComponentResponse;
import thanhcom.site.lkdt.entity.Component;
import thanhcom.site.lkdt.mapper.ComponentMapper;
import thanhcom.site.lkdt.responseApi.ResponseApi;
import thanhcom.site.lkdt.service.ComponentService;

import java.util.List;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ComponentController {
    ComponentMapper componentMapper;
    ComponentService componentService;
    @GetMapping("/components")
    public ResponseEntity<?> getAllComponents() {
        List<ComponentResponse> components = componentMapper.ResToDtoList(componentService.getAllComponents());
        ResponseApi<List<ComponentResponse>> responseApi = new ResponseApi<>();
        responseApi.setData(components);
        responseApi.setResponseCode(2001);
        responseApi.setMessenger("Lấy danh sách linh kiện thành công");
        return ResponseEntity.ok(responseApi);
    }
    @GetMapping("/components/{id}")
    public ResponseEntity<?>  getComponentById(@PathVariable Long id) {
        Component component = componentService.getComponentById(id);
        ResponseApi<Component> responseApi = new ResponseApi<>();
        responseApi.setData(component);
        responseApi.setResponseCode(2002);
        responseApi.setMessenger("Lấy thông tin linh kiện thành công");
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/components/type/{type}")
    public ResponseEntity<?> getComponentsByType(@PathVariable String type) {
        List<Component> components = componentService.getComponentsByType(type);
        ResponseApi<List<Component>> responseApi = new ResponseApi<>();
        responseApi.setData(components);
        responseApi.setResponseCode(2003);
        responseApi.setMessenger("Lấy danh sách linh kiện theo loại thành công");
        return ResponseEntity.ok(responseApi);
    }

    @PostMapping("/components")
    public ResponseEntity<?> createComponent(@Valid @RequestBody ComponentRequest componentRequest) {
        // Implementation for creating a new component
        return ResponseEntity.ok().body(componentRequest);
    }

    @PutMapping("/components/{id}")
    public ResponseEntity<ResponseApi<?>> updateComponent(
            @PathVariable Long id,
            @Valid @RequestBody ComponentRequest componentRequest) {
        Component updatedComponent = componentService.updateComponentById(id, componentRequest);
        ResponseApi<Component> response = new ResponseApi<>();
        response.setResponseCode(2003); // business code ví dụ: 2003 = Update success
        response.setMessenger("Cập nhật linh kiện thành công");
        response.setData(updatedComponent);
        return ResponseEntity.ok().body(response);
    }



}
