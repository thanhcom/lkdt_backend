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

    // ================= EDIT  =================
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

        // Update componentId (nếu gửi lên)
        if (componentId != null) {
            Component newComponent = componentService.getComponentById(componentId);
            schematic.setComponent(newComponent);
        }

        // Update text
        if (schematicName != null) {
            schematic.setSchematicName(schematicName);
        }

        if (description != null) {
            schematic.setDescription(description);
        }

        // Update PDF/ZIP
        if (schematicFile != null && !schematicFile.isEmpty()) {
            // Xóa file cũ
            deleteFile(schematic.getSchematicFile());

            // Upload file mới
            String newFileUrl;
            try {
                newFileUrl = uploadFile(schematicFile);
            } catch (Exception e) {
                throw new AppException(ErrCode.UPDATE_COMPONENT_SCHEMATIC_FAIL);
            }
            schematic.setSchematicFile(newFileUrl);
        }

        // Update images
        if (schematicImages != null && !schematicImages.isEmpty()) {
            // Xóa ảnh cũ
            deleteImages(schematic.getSchematicImage());

            // Upload ảnh mới
            List<String> newImages;
            try {
                newImages = uploadImages(schematicImages);
            } catch (Exception e) {
                throw new AppException(ErrCode.UPDATE_COMPONENT_SCHEMATIC_FAIL);
            }
            schematic.setSchematicImage(String.join(",", newImages));
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
            log.info("Deleted File : {} ", extractPublicId(fileUrl));
        } catch (Exception e) {
            log.warn("Failed to delete file on Cloudinary: {}", fileUrl, e);
        }
    }

    private void deleteImages(String imagesCsv) {
        if (imagesCsv == null || imagesCsv.isBlank()) return;
        String[] urls = imagesCsv.split(",");
        for (String url : urls) {
            deleteFile(url);
            log.info("Deleted Image  : {} ", extractPublicId(url));
        }
    }

    public String extractPublicId(String url) {
        if (url == null || url.isBlank()) return "";

        try {
            String withoutParams = url.split("\\?")[0];
            String[] parts = withoutParams.split("/upload/");
            if (parts.length < 2) return "";

            String afterUpload = parts[1]; // v123456/somefolder/file.pdf
            String[] parts2 = afterUpload.split("/", 2);
            if (parts2.length < 2) return "";

            String path = parts2[1]; // somefolder/file.pdf

            return path.substring(0, path.lastIndexOf('.')); // bỏ .pdf / .png
        } catch (Exception e) {
            return "";
        }
    }

}
