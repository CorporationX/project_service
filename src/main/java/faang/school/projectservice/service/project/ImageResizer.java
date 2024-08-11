package faang.school.projectservice.service.project;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class ImageResizer {
    public InputStream resizeImage(InputStream file,
                                   int targetWidth,
                                   int targetHeight) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(file)
                .size(targetWidth, targetHeight)
                .outputFormat("jpeg")
                .toOutputStream(outputStream);
        byte[] data = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        log.info("Размерфайла изменЁн.");
        return inputStream;
    }
}
