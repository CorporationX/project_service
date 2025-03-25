package faang.school.projectservice.service.cover;

import faang.school.projectservice.exception.EmptyFileException;
import faang.school.projectservice.exception.VacancyNotFoundException;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CoverServiceImpl implements CoverService {
    public static final String FILE_CANT_BE_EMPTY = "File can't be empty";
    public static final String VACANCY_NOT_FOUND = "Vacancy with ID %d not found";
    public static final String COVER_PREFIX = "covers/";
    public static final String FILENAME_CANT_BE_NULL = "Filename can't be null";
    public static final int MAX_IMAGE_DIMENSION = 512;

    private final MinioClient minioClient;
    private final VacancyRepository vacancyRepository;

    @Value("${services.minio.bucket.cover}")
    private String bucketName;

    @Override
    public String uploadCover(MultipartFile file, Long vacancyId) {
        Optional<Vacancy> vacancyOptional = vacancyRepository.findById(vacancyId);
        if (vacancyOptional.isEmpty()) {
            String message = String.format(VACANCY_NOT_FOUND, vacancyId);
            log.error(message);
            throw new VacancyNotFoundException(message);
        }
        String filename = file.getOriginalFilename();

        try {
            BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build();
            if (!minioClient.bucketExists(bucketExistsArgs)) {
                MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build();
                minioClient.makeBucket(makeBucketArgs);
            }
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("MiniIO client exception. {}", e.getMessage());
        }

        String fileExtension = filename.substring(filename.lastIndexOf("."));
        String generatedKey = COVER_PREFIX + UUID.randomUUID() + file.getContentType() + fileExtension;
        Vacancy vacancy = vacancyOptional.get();
        vacancy.setCoverImageKey(generatedKey);
        return generatedKey;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            log.error(FILE_CANT_BE_EMPTY);
            throw new EmptyFileException(FILE_CANT_BE_EMPTY);
        }
        String filename = file.getOriginalFilename();
        if (filename == null) {
            log.error(FILENAME_CANT_BE_NULL);
            throw new IllegalArgumentException(FILENAME_CANT_BE_NULL);
        }
    }
}
