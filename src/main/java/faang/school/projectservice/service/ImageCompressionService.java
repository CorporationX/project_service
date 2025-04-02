package faang.school.projectservice.service;

import faang.school.projectservice.config.multipartfile.CustomMultipartFile;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class ImageCompressionService {

    public MultipartFile compressFile(MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Thumbnails.of(inputStream)
                    .size(512, 512)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);

            byte[] compressedImageBytes = outputStream.toByteArray();

            MultipartFile compressFile = new CustomMultipartFile(
                    file.getName(), file.getOriginalFilename(), file.getContentType(), compressedImageBytes);
            return compressFile;
        } catch (IOException e) {
            log.error("Failed to compress file: {}. Error: {}", file.getOriginalFilename(), e.getMessage());
            throw new RuntimeException("Error compressing file %s".formatted(file), e);
        }
    }
}
