package thanhcom.site.lkdt.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import thanhcom.site.lkdt.dto.response.UploadResponse;
import thanhcom.site.lkdt.responseApi.ResponseApi;
import thanhcom.site.lkdt.service.CloudinaryService;

import java.util.Map;

@RestController
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@RequestMapping("/upload")
public class CloudinaryController {
    CloudinaryService cloudinaryService;
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam(value = "publicId", required = false) String publicId) {
        ResponseApi<UploadResponse> responseApi = new ResponseApi<>();
        try {
            UploadResponse upload = cloudinaryService.upload(file, publicId);
            responseApi.setData(upload);
            responseApi.setResponseCode(1001);
            responseApi.setMessenger("Upload thành công");
            return ResponseEntity.ok(responseApi);
        } catch (Exception e) {
            responseApi.setData(null);
            responseApi.setResponseCode(1002);
            responseApi.setMessenger("Upload thất bại: " + e.getMessage());
            return ResponseEntity.badRequest().body(responseApi);
        }
    }

    @DeleteMapping("/delete/{publicId}")
    public ResponseEntity<?> deleteFile(@PathVariable String publicId) {
        ResponseApi<Map<String, Object>> responseApi = new ResponseApi<>();
        try {
            Map<String, Object> result = cloudinaryService.delete(publicId);
            responseApi.setData(result);
            responseApi.setResponseCode(1001);
            responseApi.setMessenger("Xóa file thành công");
            return ResponseEntity.ok(responseApi);
        } catch (Exception e) {
            responseApi.setData(null);
            responseApi.setResponseCode(1002);
            responseApi.setMessenger("Xóa file thất bại: " + e.getMessage());
            return ResponseEntity.badRequest().body(responseApi);
        }
    }

}
