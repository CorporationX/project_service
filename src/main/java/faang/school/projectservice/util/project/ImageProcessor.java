package faang.school.projectservice.util.project;

import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.projectservice.property.ProjectCoverProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageProcessor {

    private final ProjectCoverProperties projectCoverProperties;

    public Pair<InputStream, ObjectMetadata> processCover(MultipartFile file) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int maxWidth = projectCoverProperties.getMaxWidth();
        byte[] bytes;
        long sizeOfFile = file.getSize();
        log.info(String.valueOf(sizeOfFile));

        try {
            BufferedImage cover = ImageIO.read(file.getInputStream());
            if (cover.getWidth() > maxWidth) {
                cover = Scalr.resize(cover, maxWidth);
            }
            ImageIO.write(cover, projectCoverProperties.getImageExtension(), outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        bytes = outputStream.toByteArray();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bytes.length);
        metadata.setContentType(file.getContentType());
        metadata.setLastModified(Date.from(Instant.now()));
        InputStream inputStream =  new ByteArrayInputStream(bytes);

        return Pair.of(inputStream,  metadata);
    }
}
