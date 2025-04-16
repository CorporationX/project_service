package faang.school.projectservice.service;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.excepion.DataValidationException;
import faang.school.projectservice.excepion.FileException;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.s3.S3Service;
import faang.school.projectservice.dto.FileDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class VacancyService {
    private static final int MAX_FILE_LINE = 512;
    private final S3Service s3Service;
    private final VacancyRepository vacancyRepository;

    @Transactional
    public VacancyDto addCover(long vacancyId, MultipartFile cover) {
        Vacancy vacancy = getAndValidateVacancy(vacancyId);

        String key = String.format("%s/%d-%s", vacancy.getId() + vacancy.getName(),
                System.currentTimeMillis(), cover.getOriginalFilename());

        FileDto fileDto = new FileDto(
                cover.getOriginalFilename(),
                key,
                cover.getContentType(),
                compressImage(cover)
        );

        vacancy.setCoverImageKey(key);
        vacancyRepository.save(vacancy);
        s3Service.uploadFile(fileDto);

        return VacancyDto.builder()
                .vacancyId(vacancy.getId())
                .coverImageKey(key)
                .build();
    }

    private Vacancy getAndValidateVacancy(long vacancyId) {
        Vacancy vacancy = vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("vacancy with id " + vacancyId + " not exists"));

        if (vacancy.getCoverImageKey() != null) {
            throw new DataValidationException("vacancy already has a cover");
        }
        return vacancy;
    }

    public byte[] compressImage(MultipartFile file) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            Thumbnails.of(file.getInputStream())
                    .size(MAX_FILE_LINE, MAX_FILE_LINE)
                    .keepAspectRatio(true)
                    .outputFormat("jpg")
                    .toOutputStream(outputStream);
        } catch (IOException e) {
            throw new FileException("couldn't read the file", e);
        }

        return outputStream.toByteArray();
    }
}
