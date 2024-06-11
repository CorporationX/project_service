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

import static faang.school.projectservice.exception.file.FileExceptionMessage.CALCULATE_IMAGE_SIZE;
import static faang.school.projectservice.exception.file.FileExceptionMessage.CONVERTING_IMAGE_TO_INPUT_STREAM;
import static faang.school.projectservice.exception.file.FileExceptionMessage.CONVERTING_INPUT_STREAM_TO_COVER;

@RequiredArgsConstructor
@Service
@Slf4j
public class ImageService {
    
    public BufferedImage convertInputStreamToImage(InputStream inputStream) {
        try {
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            log.error(CONVERTING_INPUT_STREAM_TO_COVER.getMessage(),e);
            throw new FileException(CONVERTING_INPUT_STREAM_TO_COVER.getMessage());
        }
    }
    
    public BufferedImage convertImageToCover(Supplier<ImageResource> imageSupplier) {
        return imageSupplier.get().convertToCover();
    }
    
    public InputStream convertBufferedImageToInputStream(BufferedImage image) {
        try {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
                ImageIO.write(image,"jpg", bas);
            byte[] bytes = bas.toByteArray();
            return new ByteArrayInputStream(bytes);
        } catch (IOException e) {
            log.error(CONVERTING_IMAGE_TO_INPUT_STREAM.getMessage(),e);
            throw new FileException(CONVERTING_IMAGE_TO_INPUT_STREAM.getMessage());
        }
    }
    
    public Long calculateImageSize(BufferedImage image) {
        try(ByteArrayOutputStream tmp = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", tmp);
            tmp.close();
            return (long) tmp.size();
        } catch (IOException e) {
            log.error(CALCULATE_IMAGE_SIZE.getMessage(),e);
            throw new FileException(CALCULATE_IMAGE_SIZE.getMessage());
        }
    }
}
