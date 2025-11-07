package thanhcom.site.lkdt.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import thanhcom.site.lkdt.dto.ComponentSchematicDto;
import thanhcom.site.lkdt.entity.ComponentSchematic;
import thanhcom.site.lkdt.mapper.ComponentSchematicMapper;
import thanhcom.site.lkdt.responseApi.ResponseApi;
import thanhcom.site.lkdt.service.ComponentSchematicService;

import java.util.List;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("components_schematic")
public class ComponentSchematicController {
    ComponentSchematicService componentSchematicService;
    ComponentSchematicMapper componentSchematicMapper;

    @GetMapping("/all")
    public ResponseEntity<?> GetAll()
    {
        List<ComponentSchematic> allComponentSchematics = componentSchematicService.getAllComponentSchematics();
        ResponseApi<List<ComponentSchematicDto>> responseApi = new ResponseApi<>();
        responseApi.setData(componentSchematicMapper.toListDto(allComponentSchematics));
        responseApi.setResponseCode(2001);
        responseApi.setMessenger("Lấy Schematic Thành Công ");
        return ResponseEntity.ok(responseApi);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> GetByID(@PathVariable Long id)
    {
        ComponentSchematic componentSchematics = componentSchematicService.getComponentSchematicById(id);
        ResponseApi<ComponentSchematicDto> responseApi = new ResponseApi<>();
        responseApi.setData(componentSchematicMapper.toDto(componentSchematics));
        responseApi.setResponseCode(2001);
        responseApi.setMessenger("Lấy Schematic Thành Công ");
        return ResponseEntity.ok(responseApi);
    }

    @PostMapping("/edit/{id}" )
    public ResponseEntity<?> Edit(@PathVariable Long id , @RequestBody ComponentSchematicDto componentSchematic) {
        ComponentSchematic componentSchematic1 = componentSchematicService.updateComponentSchematic(id, componentSchematicMapper.toEntity(componentSchematic));
        ResponseApi<ComponentSchematicDto> responseApi = new ResponseApi<>();
        responseApi.setData(componentSchematicMapper.toDto(componentSchematic1));
        responseApi.setResponseCode(2003);
        responseApi.setMessenger("Sửa Schematic Thành Công ");
        return ResponseEntity.ok(responseApi);
    }

    @PutMapping("/add" )
    public ResponseEntity<?> Add(@RequestBody ComponentSchematicDto componentSchematic) {
        ComponentSchematic componentSchematic1 = componentSchematicService.createComponentSchematic(componentSchematicMapper.toEntity(componentSchematic));
        ResponseApi<ComponentSchematicDto> responseApi = new ResponseApi<>();
        responseApi.setData(componentSchematicMapper.toDto(componentSchematic1));
        responseApi.setResponseCode(2002);
        responseApi.setMessenger("Thêm Schematic Thành Công ");
        return ResponseEntity.ok(responseApi);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> Delete(@PathVariable Long id) {
        componentSchematicService.deleteComponentSchematic(id);
        ResponseApi<Void> responseApi = new ResponseApi<>();
        responseApi.setResponseCode(2004);
        responseApi.setMessenger("Xóa Schematic Thành Công ");
        return ResponseEntity.ok(responseApi);
    }
}
