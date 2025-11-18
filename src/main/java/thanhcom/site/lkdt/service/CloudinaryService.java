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

import jakarta.validation.constraints.NotBlank;
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
            "image/png", "image/jpeg", "image/webp", "video/mp4", "application/pdf"
    );

    private static final long MAX_BYTES = 10 * 1024 * 1024L; // 10 MB

    /**
     * Upload file lên Cloudinary
     */
    public UploadResponse upload(MultipartFile file, String publicIdPrefix) throws IOException {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("File trống");
        if (file.getSize() > MAX_BYTES)
            throw new IllegalArgumentException("File quá lớn: " + MAX_BYTES);

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType))
            throw new IllegalArgumentException("Loại file không hợp lệ: " + contentType);

        Map<String, Object> options = ObjectUtils.asMap("resource_type", "auto");
        if (uploadFolder != null && !uploadFolder.isBlank()) options.put("folder", uploadFolder);
        if (publicIdPrefix != null && !publicIdPrefix.isBlank()) {
            options.put("public_id", publicIdPrefix);
            options.put("overwrite", true);
        }

        // fix lỗi Unrecognized file parameter
        byte[] bytes = file.getBytes();
        @SuppressWarnings("unchecked")
        Map<String, Object> result = cloudinary.uploader().upload(bytes, options);

        String publicId = result.getOrDefault("public_id", "").toString();
        String secureUrl = result.getOrDefault("secure_url", result.get("url")).toString();
        return new UploadResponse(publicId, secureUrl);
    }

    /**
     * Xóa file theo publicId
     */
    public Map<String, Object> delete(@NotBlank String publicId) throws Exception {
        if (publicId == null || publicId.isBlank())
            throw new IllegalArgumentException("publicId không được để trống");
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    /**
     * Xóa file theo URL (tự tách publicId từ URL)
     */
    public Map<String, Object> deleteByUrl(@NotBlank String url) throws Exception {
        if (url == null || url.isBlank())
            throw new IllegalArgumentException("URL không được để trống");

        // Lấy publicId từ URL
        String[] parts = url.split("/");
        String filename = parts[parts.length - 1]; // ví dụ: image123.jpg
        String publicId = filename.contains(".") ? filename.substring(0, filename.lastIndexOf(".")) : filename;
        return delete(publicId);
    }
}
