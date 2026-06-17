package com.thannong.store.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Controller phục vụ ảnh sản phẩm đã upload.
 * Endpoint: GET /api/images/products/{imageName}
 * PUBLIC: không cần xác thực (để hiển thị ảnh trên storefront)
 */
@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Value("${app.upload.dir:uploads/products}")
    private String uploadDir;

    /**
     * Trả về file ảnh sản phẩm theo tên file.
     * Frontend gọi: <img src="http://localhost:8080/api/images/products/ten-anh.jpg">
     *
     * @param imageName tên file ảnh (ví dụ: "dau-goi-thao-duoc.jpg")
     * @return Resource chứa dữ liệu ảnh
     */
    @GetMapping("/products/{imageName}")
    public ResponseEntity<Resource> getProductImage(@PathVariable String imageName) {
        try {
            Path imagePath = Paths.get(uploadDir).resolve(imageName);
            Resource resource = new UrlResource(imagePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                // FALLBACK: Đọc ảnh mặc định từ classpath (jar) nếu không tìm thấy ở thư mục upload ngoài
                resource = new org.springframework.core.io.ClassPathResource("default-images/" + imageName);
                if (!resource.exists() || !resource.isReadable()) {
                    return ResponseEntity.notFound().build();
                }
            }

            // Xác định content type dựa trên đuôi file
            MediaType mediaType = MediaType.IMAGE_JPEG;
            if (imageName.toLowerCase().endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            } else if (imageName.toLowerCase().endsWith(".gif")) {
                mediaType = MediaType.IMAGE_GIF;
            } else if (imageName.toLowerCase().endsWith(".webp")) {
                mediaType = MediaType.parseMediaType("image/webp");
            }

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/debug")
    public ResponseEntity<?> debugImages() {
        java.util.Map<String, Object> debugInfo = new java.util.HashMap<>();
        String testFile = "dau-goi-thao-duoc-thien-nhien-vui.jpg";
        org.springframework.core.io.ClassPathResource res = new org.springframework.core.io.ClassPathResource("default-images/" + testFile);
        debugInfo.put("testFile", testFile);
        debugInfo.put("exists", res.exists());
        try {
            debugInfo.put("url", res.getURL().toString());
            debugInfo.put("contentLength", res.contentLength());
        } catch (Exception e) {
            debugInfo.put("error", e.getMessage());
        }
        return ResponseEntity.ok(debugInfo);
    }

}
