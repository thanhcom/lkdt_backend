package thanhcom.site.lkdt.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thanhcom.site.lkdt.dto.ComponentDetail;
import thanhcom.site.lkdt.dto.request.ComponentCreateRequest;
import thanhcom.site.lkdt.dto.request.ComponentRequest;
import thanhcom.site.lkdt.dto.response.ComponentResponse;
import thanhcom.site.lkdt.entity.Component;
import thanhcom.site.lkdt.mapper.ComponentMapper;
import thanhcom.site.lkdt.responseApi.ResponseApi;
import thanhcom.site.lkdt.service.ComponentService;
import thanhcom.site.lkdt.service.ComponentSupplierService;

import java.util.List;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("components")
public class ComponentController {
    ComponentMapper componentMapper;
    ComponentService componentService;
    @GetMapping("/all")
    public ResponseEntity<?> getAllComponents() {
        List<ComponentResponse> components = componentMapper.ResToDtoList(componentService.getAllComponents());
        ResponseApi<List<ComponentResponse>> responseApi = new ResponseApi<>();
        responseApi.setData(components);
        responseApi.setResponseCode(2001);
        responseApi.setMessenger("Lấy danh sách linh kiện thành công");
        return ResponseEntity.ok(responseApi);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?>  getComponentById(@PathVariable Long id) {
        Component component = componentService.getComponentById(id);
        ResponseApi<ComponentResponse> responseApi = new ResponseApi<>();
        responseApi.setData(componentMapper.ResToDto(component));
        responseApi.setResponseCode(2002);
        responseApi.setMessenger("Lấy thông tin linh kiện thành công");
        return ResponseEntity.ok(responseApi);
    }
    @GetMapping("/type/{type}")
    public ResponseEntity<?> getComponentsByType(@PathVariable String type) {
        List<Component> components = componentService.getComponentsByType(type);
        ResponseApi<List<Component>> responseApi = new ResponseApi<>();
        responseApi.setData(components);
        responseApi.setResponseCode(2003);
        responseApi.setMessenger("Lấy danh sách linh kiện theo loại thành công");
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<?> getComponentsBySupplier(@PathVariable Long supplierId) {
        List<Component> components = componentService.getComponentsBySupplier(supplierId);
        ResponseApi<List<ComponentResponse>> responseApi = new ResponseApi<>();
        responseApi.setData(componentMapper.ResToDtoList(components));
        responseApi.setResponseCode(2004);
        responseApi.setMessenger("Lấy danh sách linh kiện theo nhà cung cấp thành công");
        return ResponseEntity.ok(responseApi);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createComponent(@Valid @RequestBody ComponentRequest componentRequest) {
        // Implementation for creating a new component
        return ResponseEntity.ok().body(componentRequest);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<ResponseApi<?>> updateComponent(
            @PathVariable Long id,
            @Valid @RequestBody ComponentCreateRequest componentRequest) {
        Component updatedComponent = componentService.updateComponent(id, componentRequest);
        ResponseApi<Component> response = new ResponseApi<>();
        response.setResponseCode(2003); // business code ví dụ: 2003 = Update success
        response.setMessenger("Cập nhật linh kiện thành công");
        response.setData(updatedComponent);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ResponseApi<ComponentDetail>> getComponentDetail(@PathVariable Long id) {
        ComponentDetail component = componentService.getComponentDetail(id);
        ResponseApi<ComponentDetail> response = new ResponseApi<>();
        response.setResponseCode(2004); // business code ví dụ: 2004 = Get detail success
        response.setMessenger("Lấy chi tiết linh kiện thành công");
        response.setData(component);
        return ResponseEntity.ok().body(response);
    }



}
