package faang.school.projectservice.service.cover;

import faang.school.projectservice.exception.EmptyFileException;
import faang.school.projectservice.exception.FileProcessingException;
import faang.school.projectservice.exception.VacancyNotFoundException;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoverServiceImpl implements CoverService {
    public static final String FILE_CANT_BE_EMPTY = "File can't be empty";
    public static final String VACANCY_NOT_FOUND = "Vacancy with ID %d not found";
    public static final String COVER_PREFIX = "covers/";
    public static final String FILENAME_CANT_BE_NULL = "Filename can't be null or blank";
    public static final String UPLOADED_FILE_IS_NOT_A_VALID_IMAGE = "Uploaded file is not a valid image";
    public static final String IMAGE_PROCESSING_FAILED = "Failed to process image";
    public static final String UNEXPECTED_ERROR_DURING_COVER_UPLOAD = "Unexpected error during cover upload";
    public static final String UNSUPPORTED_IMAGE_FORMAT = "Unsupported image format. Allowed: JPG, JPEG, PNG";
    public static final Set<String> AVAILABLE_FILE_EXTENSIONS = Set.of("jpg", "jpeg", "png");

    private final MinioClient minioClient;
    private final VacancyRepository vacancyRepository;

    @Value("${services.minio.cover.max-image-dimension}")
    private static int maxImageDimension;

    @Value("${services.minio.cover.bucket}")
    private static String bucketName;

    @Override
    public String uploadCover(MultipartFile file, Long vacancyId) {
        validateFile(file);
        Vacancy vacancy = findVacancyById(vacancyId);

        String filename = file.getOriginalFilename();
        String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);
        String generatedKey = COVER_PREFIX + UUID.randomUUID() + fileExtension;
        try {
            BufferedImage croppedImage = cropImage(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(croppedImage, fileExtension, outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(imageBytes);

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(generatedKey)
                    .stream(inputStream, imageBytes.length, -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (IOException e) {
            log.error("{}: {}", IMAGE_PROCESSING_FAILED, e.getMessage());
            throw new FileProcessingException(IMAGE_PROCESSING_FAILED, e);
        } catch (Exception e) {
            log.error("{}: {}", UNEXPECTED_ERROR_DURING_COVER_UPLOAD, e.getMessage());
            throw new FileProcessingException(UNEXPECTED_ERROR_DURING_COVER_UPLOAD, e);
        }
        vacancy.setCoverImageKey(generatedKey);
        return generatedKey;
    }

    private BufferedImage cropImage(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            log.error(UPLOADED_FILE_IS_NOT_A_VALID_IMAGE);
            throw new IllegalArgumentException(UPLOADED_FILE_IS_NOT_A_VALID_IMAGE);
        }
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int maxDimension = Math.max(originalHeight, originalWidth);

        return maxDimension <= maxImageDimension ? originalImage : Thumbnails.of(originalImage)
                .size(maxImageDimension, maxImageDimension)
                .asBufferedImage();
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            log.error(FILE_CANT_BE_EMPTY);
            throw new EmptyFileException(FILE_CANT_BE_EMPTY);
        }
        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            log.error(FILENAME_CANT_BE_NULL);
            throw new IllegalArgumentException(FILENAME_CANT_BE_NULL);
        }
        String fileExtension = filename.substring(filename.lastIndexOf(".") + 1);
        if (!AVAILABLE_FILE_EXTENSIONS.contains(fileExtension)) {
            log.error("Unsupported file format: {}", fileExtension);
            throw new IllegalArgumentException(UNSUPPORTED_IMAGE_FORMAT);
        }
    }

    private Vacancy findVacancyById(Long vacancyId) {
        Optional<Vacancy> vacancyOptional = vacancyRepository.findById(vacancyId);
        if (vacancyOptional.isEmpty()) {
            String message = String.format(VACANCY_NOT_FOUND, vacancyId);
            log.error(message);
            throw new VacancyNotFoundException(message);
        }
        return vacancyOptional.get();
    }
}
