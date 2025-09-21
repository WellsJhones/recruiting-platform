package com.wells.recruiting.platform.recruiting.platform.controler;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class ImageUploadControler {

    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/png",
            "image/jpeg",
            "image/jpg",
            "image/gif");

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadFile(@RequestParam("image") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "No file selected"));
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "Invalid file type"));
        }

        String uploadDir = "/var/www/html/uploads";
        File dir = new File(uploadDir);
        if (!dir.exists() && !dir.mkdirs()) {
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Failed to create upload directory"));
        }

        // Generate a random filename with the original extension
        String extension = "";
        String originalName = file.getOriginalFilename();
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = java.util.UUID.randomUUID().toString() + extension;

        File dest = new File(dir, fileName);

        try {
            // Validate and re-encode image to strip payloads
            javax.imageio.ImageIO.setUseCache(false);
            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(file.getInputStream());
            if (img == null) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "Invalid image file"));
            }
            // Default to png if extension is not a supported format
            String format = extension.replace(".", "").toLowerCase();
            if (!(format.equals("png") || format.equals("jpg") || format.equals("jpeg") || format.equals("gif"))) {
                format = "png";
                fileName = java.util.UUID.randomUUID().toString() + ".png";
                dest = new File(dir, fileName);
            }
            javax.imageio.ImageIO.write(img, format, dest);
            String imageUrl = "http://wellsjhones.com.br/uploads/" + fileName;
            return ResponseEntity.ok(java.util.Map.of("imageUrl", imageUrl));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(java.util.Map.of("error", "File upload failed"));
        }
    }

}
