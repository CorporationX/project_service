package faang.school.projectservice.service.s3;

import faang.school.projectservice.exception.FileException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class CoverHandler {
    private final long MAX_COVER_SIZE = 5242880L;
    private final int MAX_HEIGHT = 566;
    private final int MAX_WIDTH = 1080;

    public void checkCoverSize(MultipartFile file) {
        if (file.getSize() > MAX_COVER_SIZE) {
            throw new IllegalArgumentException("Превышен размер обложки");
        }
    }

    public void checkCoverResolution(MultipartFile file) {
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        if (originalHeight == originalWidth) {
            if (originalHeight > MAX_WIDTH) {
                throw new FileException("Размер обложки не должен быть больше 1080x1080");
            }
        } else {
            if (originalHeight > MAX_HEIGHT || originalWidth > MAX_WIDTH) {
                throw new FileException("Размер обложки не должен быть больше 1080x566");
            }
        }
    }

    //Страшный душный метод проверки и сжатия обложки
    public byte[] checkCoverAndResize(MultipartFile file) {
        BufferedImage originalImage = null;
        BufferedImage resizedImage = null;
        try {
            originalImage = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        double ration = (double) originalWidth / originalHeight;

        if (ration == 1){
            if (originalHeight > 1080){
                resizedImage = resizeImage(originalImage, 1080, 1080);
            }
        } else if (ration < 1){
            if (originalHeight > 1080){
                resizedImage = resizeImage(originalImage, (int) (1080*ration), 1080);
            }
        } else if (ration > 1 && ration < (double) 1080 /566){
            if (originalHeight > 566){
                resizedImage = resizeImage(originalImage, (int) (566*ration), 566);
            }
        } else if (ration > (double) 1080 /566){
            if (originalWidth > 1080){
                resizedImage = resizeImage(originalImage, 1080, (int) (1080/ration));
            }
        }
        if (resizedImage == null){
            resizedImage = originalImage;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(resizedImage, "jpg", outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return outputStream.toByteArray(); //как потом перевести byte[] в InputStream?
    }       //все способы требуют создание нового специального класса ради одного этого действия

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }
}
