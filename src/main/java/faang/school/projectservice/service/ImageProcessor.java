package faang.school.projectservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;

public interface ImageProcessor {
    BufferedImage resizeImage(MultipartFile file);

    MultipartFile convertImageToMultipartFile(BufferedImage image, String name, String originalName,
                                              String contentType);
}
