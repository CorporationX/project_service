package faang.school.projectservice.service.project.pic;

import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Component
public class PicProcessor {
    private static final int HORIZONTAL_MAX_WIDTH = 1080;
    private static final int HORIZONTAL_MAX_HEIGHT = 566;
    private static final int SQUARE_MAX_SIZE = 1080;

    public ImageSize getImageSize(MultipartFile pic) {
        ImageSize imageSize = new ImageSize();

        BufferedImage image = null;
        try {
            image = ImageIO.read(pic.getInputStream());

            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();

            imageSize.setOriginalWidth(originalWidth);
            imageSize.setOriginalHeight(originalHeight);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return imageSize;
    }

    public ByteArrayOutputStream getPicBaosByLength(MultipartFile pic) {
        ByteArrayOutputStream picBaos = new ByteArrayOutputStream();
        try {
            BufferedImage image = ImageIO.read(pic.getInputStream());
            int originalWidth = image.getWidth();
            int originalHeight = image.getHeight();

            int newWidth = originalWidth;
            int newHeight = originalHeight;

            if (originalWidth > originalHeight) {
                if (originalWidth > HORIZONTAL_MAX_WIDTH || originalHeight > HORIZONTAL_MAX_HEIGHT) {
                    double aspectRatio = (double) originalHeight / originalWidth;
                    newWidth = HORIZONTAL_MAX_WIDTH;
                    newHeight = (int) (HORIZONTAL_MAX_WIDTH * aspectRatio);
                    if (newHeight > HORIZONTAL_MAX_HEIGHT) {
                        newHeight = HORIZONTAL_MAX_HEIGHT;
                        newWidth = (int) (HORIZONTAL_MAX_HEIGHT / aspectRatio);
                    }
                }
            } else if (originalWidth == originalHeight) {
                if (originalWidth > SQUARE_MAX_SIZE) {
                    newWidth = SQUARE_MAX_SIZE;
                    newHeight = SQUARE_MAX_SIZE;
                }
            } else {
                if (originalWidth > SQUARE_MAX_SIZE || originalHeight > SQUARE_MAX_SIZE) {
                    double aspectRatio = (double) originalHeight / originalWidth;
                    newWidth = SQUARE_MAX_SIZE;
                    newHeight = (int) (SQUARE_MAX_SIZE * aspectRatio);
                    if (newHeight > SQUARE_MAX_SIZE) {
                        newHeight = SQUARE_MAX_SIZE;
                        newWidth = (int) (SQUARE_MAX_SIZE / aspectRatio);
                    }
                }
            }

            log.info("Image compression has started " + pic.getOriginalFilename() +
                    ", to the size: " + newWidth + "x" + newHeight);
            Thumbnails.of(image)
                    .size(newWidth, newHeight)
                    .outputFormat("jpeg")
                    .toOutputStream(picBaos);
        } catch (IOException e) {
            log.error("IOException: ", e);
            throw new RuntimeException(e);
        }
        log.info("Image compression " + pic.getOriginalFilename() + " completed.");
        return picBaos;
    }

    public ObjectMetadata getPicMetaData(MultipartFile pic, ByteArrayOutputStream picBaos, int originalWidth, int originalHeight) {
        ObjectMetadata picMetadata = new ObjectMetadata();
        picMetadata.setContentLength(picBaos.size());
        picMetadata.setContentType(pic.getContentType());
        picMetadata.addUserMetadata("originalFileName", pic.getOriginalFilename());
        picMetadata.addUserMetadata("originalWidth", String.valueOf(originalWidth));
        picMetadata.addUserMetadata("originalHeight", String.valueOf(originalHeight));

        return picMetadata;
    }
}