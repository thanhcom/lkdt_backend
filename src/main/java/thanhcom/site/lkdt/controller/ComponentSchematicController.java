package thanhcom.site.lkdt.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import thanhcom.site.lkdt.dto.ComponentSchematicDto;
import thanhcom.site.lkdt.dto.response.ComponentResponse;
import thanhcom.site.lkdt.entity.ComponentSchematic;
import thanhcom.site.lkdt.mapper.ComponentSchematicMapper;
import thanhcom.site.lkdt.responseApi.ResponseApi;
import thanhcom.site.lkdt.service.ComponentSchematicService;

import java.util.List;

@RestController
@RequestMapping("/schematic")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ComponentSchematicController {

    ComponentSchematicService schematicService;
    ComponentSchematicMapper schematicMapper;

    // CREATE
    @PostMapping("/create")
    public ResponseEntity<?> create(
            @RequestParam Long componentId,
            @RequestParam String schematicName,
            @RequestParam(required = false) MultipartFile schematicFile,
            @RequestParam(required = false) List<MultipartFile> schematicImages,
            @RequestParam(required = false) String description
    ) throws Exception {

        if (schematicFile != null) {
            System.out.println("Received schematicFile: " + schematicFile.getOriginalFilename());
        } else {
            System.out.println("schematicFile is null");
        }

        if (schematicImages != null) {
            System.out.println("Received " + schematicImages.size() + " images");
        } else {
            System.out.println("schematicImages is null");
        }

        ComponentSchematic created = schematicService.createComponentSchematic(
                componentId, schematicName, schematicFile, schematicImages, description
        );
        ResponseApi<ComponentSchematicDto> responseApi = new ResponseApi<>();
        responseApi.setMessenger("Tạo sơ đồ linh kiện thành công");
        responseApi.setData(schematicMapper.toDto(created));
        responseApi.setResponseCode(2001);
        return ResponseEntity.ok(responseApi);
    }
    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestParam(required = false) Long componentId,
            @RequestParam(required = false) String schematicName,
            @RequestParam(required = false) MultipartFile schematicFile,
            @RequestParam(required = false) List<MultipartFile> schematicImages,
            @RequestParam(required = false) String description
    ) throws Exception {

        ComponentSchematic updated = schematicService.updateComponentSchematic(
                id,
                componentId,
                schematicName,
                schematicFile,
                schematicImages,
                description
        );

        ResponseApi<ComponentSchematicDto> responseApi = new ResponseApi<>();
        responseApi.setMessenger("Sửa sơ đồ linh kiện thành công");
        responseApi.setResponseCode(2001);
        responseApi.setData(schematicMapper.toDto(updated));

        return ResponseEntity.ok(responseApi);
    }


    // READ ALL
    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        List<ComponentSchematic> all = schematicService.getAll();
        ResponseApi<List<ComponentSchematicDto>> responseApi = new ResponseApi<>();
        responseApi.setMessenger("Lấy danh sách sơ đồ linh kiện thành công");
        responseApi.setData(schematicMapper.toListDto(all));
        responseApi.setResponseCode(2001);
        return ResponseEntity.ok(responseApi);
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        ComponentSchematic byId = schematicService.getById(id);
        ResponseApi<ComponentSchematicDto> responseApi = new ResponseApi<>();
        responseApi.setMessenger("Lấy sơ đồ linh kiện thành công");
        responseApi.setData(schematicMapper.toDto(byId));
        responseApi.setResponseCode(2001);
        return ResponseEntity.ok(responseApi);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        schematicService.deleteComponentSchematic(id);
        ResponseApi<?> responseApi = new ResponseApi<>();
        responseApi.setMessenger("Xóa sơ đồ linh kiện thành công");
        responseApi.setResponseCode(2001);
        return ResponseEntity.ok(responseApi);
    }
}
