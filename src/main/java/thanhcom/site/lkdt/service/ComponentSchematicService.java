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
import thanhcom.site.lkdt.enums.ErrCode;
import thanhcom.site.lkdt.exception.AppException;
import thanhcom.site.lkdt.repository.ComponentSchematicRepository;
import thanhcom.site.lkdt.dto.response.UploadResponse;

import java.util.ArrayList;
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

        // Upload PDF/ZIP as raw
        if (schematicFile != null && !schematicFile.isEmpty()) {
            log.info("Uploading schematicFile: {}", schematicFile.getOriginalFilename());
            // Chỉnh sửa: gọi upload với resource_type = "raw"
            UploadResponse response = cloudinaryService.upload(schematicFile, null, "raw");
            schematic.setSchematicFile(response.getSecureUrl());
        } else {
            log.warn("schematicFile is null or empty");
        }

        // Upload images
        if (schematicImages != null && !schematicImages.isEmpty()) {
            log.info("Uploading {} images", schematicImages.size());
            schematic.setSchematicImage(String.join(",", uploadImages(schematicImages)));
        } else {
            log.warn("schematicImages is null or empty");
        }

        return componentSchematicRepository.save(schematic);
    }

    // ================= Helper =================

    // Upload image (không cần chỉnh sửa PDF nữa)
    private String uploadImageFile(MultipartFile file) throws Exception {
        UploadResponse response = cloudinaryService.upload(file, null, "image"); // resource_type = image
        return response.getSecureUrl();
    }

    private List<String> uploadImages(List<MultipartFile> images) throws Exception {
        List<String> urls = new ArrayList<>();
        for (MultipartFile img : images) {
            if (img != null && !img.isEmpty()) {
                log.info("Uploading image: {}", img.getOriginalFilename());
                urls.add(uploadImageFile(img));
            } else {
                log.warn("Image file is null or empty");
            }
        }
        return urls;
    }

    // ================= EDIT =================
    @Transactional
    public ComponentSchematic updateComponentSchematic(
            Long id,
            Long componentId,
            String schematicName,
            MultipartFile schematicFile,
            List<MultipartFile> schematicImages,
            String description
    ){

        ComponentSchematic schematic = getById(id);

        // Update componentId
        if (componentId != null) {
            Component newComponent = componentService.getComponentById(componentId);
            schematic.setComponent(newComponent);
        }

        // Update text
        if (schematicName != null) schematic.setSchematicName(schematicName);
        if (description != null) schematic.setDescription(description);

        // Update PDF/ZIP
        if (schematicFile != null && !schematicFile.isEmpty()) {
            deleteFile(schematic.getSchematicFile());
            try {
                // Chỉnh sửa: upload PDF/ZIP với resource_type = "raw"
                UploadResponse response = cloudinaryService.upload(schematicFile, null, "raw");
                schematic.setSchematicFile(response.getSecureUrl());
            } catch (Exception e) {
                throw new AppException(ErrCode.UPDATE_COMPONENT_SCHEMATIC_FAIL);
            }
        }

        // Update images
        if (schematicImages != null && !schematicImages.isEmpty()) {
            deleteImages(schematic.getSchematicImage());
            try {
                List<String> newImages = new ArrayList<>();
                for (MultipartFile img : schematicImages) {
                    if (img != null && !img.isEmpty()) {
                        newImages.add(uploadImageFile(img));
                    }
                }
                schematic.setSchematicImage(String.join(",", newImages));
            } catch (Exception e) {
                throw new AppException(ErrCode.UPDATE_COMPONENT_SCHEMATIC_FAIL);
            }
        }

        return componentSchematicRepository.save(schematic);
    }

    // ================= READ =================
    public ComponentSchematic getById(Long id) {
        return componentSchematicRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrCode.COMPONENT_SCHEMATIC_NOTFOUND));
    }

    public List<ComponentSchematic> getAll() {
        return componentSchematicRepository.findAll();
    }

    public List<ComponentSchematic> getByComponentId(Long componentId) {
        return componentSchematicRepository.findByComponent_Id(componentId);
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
            // Chỉnh sửa: dùng deleteByUrl để xóa đúng PDF/Images
            cloudinaryService.deleteByUrl(fileUrl);
            log.info("Deleted File: {}", fileUrl);
        } catch (Exception e) {
            log.warn("Failed to delete file on Cloudinary: {}", fileUrl, e);
        }
    }

    private void deleteImages(String imagesCsv) {
        if (imagesCsv == null || imagesCsv.isBlank()) return;
        String[] urls = imagesCsv.split(",");
        for (String url : urls) {
            deleteFile(url);
        }
    }
}
