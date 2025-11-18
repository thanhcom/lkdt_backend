package thanhcom.site.lkdt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import thanhcom.site.lkdt.entity.ComponentSchematic;
import thanhcom.site.lkdt.service.ComponentSchematicService;

import java.util.List;

@RestController
@RequestMapping("/api/schematics")
@RequiredArgsConstructor
public class ComponentSchematicController {

    private final ComponentSchematicService schematicService;

    // CREATE
    @PostMapping
    public ResponseEntity<ComponentSchematic> create(
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
        return ResponseEntity.ok(created);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<ComponentSchematic>> getAll() {
        return ResponseEntity.ok(schematicService.getAll());
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ComponentSchematic> getById(@PathVariable Long id) {
        return ResponseEntity.ok(schematicService.getById(id));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        schematicService.deleteComponentSchematic(id);
        return ResponseEntity.noContent().build();
    }
}
