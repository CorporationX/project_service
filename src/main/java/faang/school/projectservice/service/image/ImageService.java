package faang.school.projectservice.service.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import faang.school.projectservice.dto.image.ImageResource;
import faang.school.projectservice.exception.FileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class ImageService {
    
    public BufferedImage convertInputStreamToImage(InputStream inputStream) {
        try {
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            String error = "IO exception while converting input stream to thumbnail";
            log.error(error,e);
            throw new FileException(error);
        }
    }
    
    public BufferedImage convertImageToThumbnail(Supplier<ImageResource> imageSupplier) {
        return imageSupplier.get().convertToThumbnail();
    }
    
    public InputStream convertBufferedImageToInputStream(BufferedImage image) {
        try {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
                ImageIO.write(image,"jpg", bas);
            byte[] bytes = bas.toByteArray();
            return new ByteArrayInputStream(bytes);
        } catch (IOException e) {
            String error = "IO exception while converting image to input stream";
            log.error(error,e);
            throw new FileException(error);
        }
    }
    
    public Long calculateImageSize(BufferedImage image) {
        try(ByteArrayOutputStream tmp = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", tmp);
            tmp.close();
            return (long) tmp.size();
        } catch (IOException e) {
            String error = "IO exception while calculating image size";
            log.error(error,e);
            throw new FileException(error);
        }
    }
    
}
