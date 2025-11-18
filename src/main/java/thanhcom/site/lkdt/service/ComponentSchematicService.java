package thanhcom.site.lkdt.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import thanhcom.site.lkdt.entity.Component;
import thanhcom.site.lkdt.entity.ComponentSchematic;
import thanhcom.site.lkdt.repository.ComponentSchematicRepository;
import thanhcom.site.lkdt.dto.response.UploadResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ComponentSchematicService {

    ComponentSchematicRepository componentSchematicRepository;
    CloudinaryService cloudinaryService;
    ComponentService componentService;

    // ================= CREATE =================
    public ComponentSchematic createComponentSchematic(
            Long componentId,
            String schematicName,
            MultipartFile schematicFile,
            List<MultipartFile> schematicImages,
            String description
    ) throws Exception {
        Component component = componentService.getComponentById(componentId);

        ComponentSchematic schematic = new ComponentSchematic();
        schematic.setComponent(component);
        schematic.setSchematicName(schematicName);
        schematic.setDescription(description);

        // Upload PDF/ZIP
        if (schematicFile != null && !schematicFile.isEmpty()) {
            log.info("Uploading schematicFile: {}", schematicFile.getOriginalFilename());
            schematic.setSchematicFile(uploadFile(schematicFile));
        } else {
            log.warn("schematicFile is null or empty");
        }

        // Upload nhiều ảnh
        if (schematicImages != null && !schematicImages.isEmpty()) {
            log.info("Uploading {} images", schematicImages.size());
            schematic.setSchematicImage(String.join(",", uploadImages(schematicImages)));
        } else {
            log.warn("schematicImages is null or empty");
        }

        return componentSchematicRepository.save(schematic);
    }

    // ================= Helper =================
    private String uploadFile(MultipartFile file) throws Exception {
        UploadResponse response = cloudinaryService.upload(file, null);
        return response.getSecureUrl();
    }

    private List<String> uploadImages(List<MultipartFile> images) throws Exception {
        List<String> urls = new ArrayList<>();
        for (MultipartFile img : images) {
            if (img != null && !img.isEmpty()) {
                log.info("Uploading image: {}", img.getOriginalFilename());
                urls.add(uploadFile(img));
            } else {
                log.warn("Image file is null or empty");
            }
        }
        return urls;
    }

    // ================= READ =================
    public ComponentSchematic getById(Long id) {
        return componentSchematicRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ComponentSchematic not found with id: " + id));
    }

    public List<ComponentSchematic> getAll() {
        return componentSchematicRepository.findAll();
    }

    // ================= DELETE =================
    @Transactional
    public void deleteComponentSchematic(Long id) {
        ComponentSchematic schematic = getById(id);

        deleteFile(schematic.getSchematicFile());
        deleteImages(schematic.getSchematicImage());

        componentSchematicRepository.delete(schematic);
        log.info("Deleted ComponentSchematic id={} and all files on Cloudinary", id);
    }

    private void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;
        try {
            cloudinaryService.delete(extractPublicId(fileUrl));
        } catch (Exception e) {
            log.warn("Failed to delete file on Cloudinary: {}", fileUrl, e);
        }
    }

    private void deleteImages(String imagesCsv) {
        if (imagesCsv == null || imagesCsv.isBlank()) return;
        List<String> urls = Arrays.asList(imagesCsv.split(","));
        for (String url : urls) deleteFile(url);
    }

    public String extractPublicId(String url) {
        if (url == null || url.isBlank()) return "";
        String[] parts = url.split("/");
        String last = parts[parts.length - 1];
        return last.contains(".") ? last.substring(0, last.lastIndexOf('.')) : last;
    }
}
