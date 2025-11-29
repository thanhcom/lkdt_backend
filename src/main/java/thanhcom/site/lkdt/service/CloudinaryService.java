package thanhcom.site.lkdt.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import thanhcom.site.lkdt.dto.response.UploadResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryService {

    Cloudinary cloudinary;

    @NonFinal
    @Value("${cloudinary.upload_folder:thanhcom-uploads}")
    String uploadFolder;

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/png", "image/jpeg", "image/webp",
            "video/mp4", "application/pdf", "application/zip"
    );

    private static final long MAX_BYTES = 10 * 1024 * 1024L; // 10 MB

    // ================= UPLOAD =================
    public UploadResponse upload(MultipartFile file, String publicIdPrefix, String resourceType) throws IOException {

        if (file == null || file.isEmpty()) throw new IllegalArgumentException("File trống");
        if (file.getSize() > MAX_BYTES)
            throw new IllegalArgumentException("File quá lớn: " + MAX_BYTES);

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType))
            throw new IllegalArgumentException("Loại file không hợp lệ: " + contentType);

        if (resourceType == null || resourceType.isBlank()) resourceType = "auto";

        Map<String, Object> options = Map.of(
                "resource_type", resourceType,
                "folder", uploadFolder
        );

        if (publicIdPrefix != null && !publicIdPrefix.isBlank()) {
            options = Map.of(
                    "resource_type", resourceType,
                    "folder", uploadFolder,
                    "public_id", publicIdPrefix,
                    "overwrite", true
            );
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), options);

        return new UploadResponse(
                result.get("public_id").toString(),
                result.get("secure_url").toString()
        );
    }

    // ================= DELETE =================
    public Map<String, Object> delete(String publicId, String resourceType) throws Exception {
        if (publicId == null || publicId.isBlank()) return Map.of();
        if (resourceType == null || resourceType.isBlank()) resourceType = "auto";

        log.info("Deleting file Cloudinary publicId={} type={}", publicId, resourceType);
        return cloudinary.uploader().destroy(publicId, Map.of("resource_type", resourceType));
    }

    public Map<String, Object> deleteByUrl(String url) throws Exception {
        if (url == null || url.isBlank()) return Map.of();

        log.info("Deleting by URL: {}", url);

        // xác định type: image hoặc raw
        String type = url.contains("/image/") ? "image" : "raw";

        // lấy phần sau /upload/
        int uploadIndex = url.indexOf("/upload/");
        if (uploadIndex == -1) {
            log.warn("URL không hợp lệ: {}", url);
            return Map.of();
        }

        String afterUpload = url.substring(uploadIndex + 8); // bỏ "/upload/"
        // afterUpload: v1763978474/thanhcom-uploads/abcxyz.jpg

        // bỏ version (v123456)
        int slashIdx = afterUpload.indexOf('/');
        if (slashIdx == -1) {
            log.warn("URL không hợp lệ, thiếu folder: {}", url);
            return Map.of();
        }

        String withoutVersion = afterUpload.substring(slashIdx + 1); // thanhcom-uploads/abcxyz.jpg

        // bỏ extension nếu có
        String publicId = withoutVersion.contains(".")
                ? withoutVersion.substring(0, withoutVersion.lastIndexOf('.'))
                : withoutVersion;

        log.info("Extracted publicId={} type={}", publicId, type);

        return delete(publicId, type);
    }
}
