package faang.school.projectservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.InputStream;

public interface ImageProcessor {

    BufferedImage resizeImage(MultipartFile file);

    InputStream convertImageToInputStream(BufferedImage image, String contentType);

}
