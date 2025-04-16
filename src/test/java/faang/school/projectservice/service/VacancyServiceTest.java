package faang.school.projectservice.service;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.excepion.DataValidationException;
import faang.school.projectservice.excepion.FileException;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.VacancyRepository;
import faang.school.projectservice.service.s3.S3Service;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {

    @InjectMocks
    private VacancyService vacancyService;

    @Mock
    private S3Service s3Service;

    @Mock
    private VacancyRepository vacancyRepository;

    private final long vacancyId = 1;
    private final String vacancyName = "name";
    private final String vacancyStartImageKey = String.format("%s/", vacancyId + vacancyName);
    private final MultipartFile cover = initFile();

    private Vacancy vacancy;

    @BeforeEach
    void setUp() {
        vacancy = Vacancy.builder().id(vacancyId).name(vacancyName).build();
    }


    @Test
    void addCover_shouldAdd() {
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));

        VacancyDto vacancyDto = vacancyService.addCover(vacancyId, cover);

        assertTrue(vacancyDto.getCoverImageKey().startsWith(vacancyStartImageKey));
        assertEquals(vacancyId, vacancyDto.getVacancyId());
        verify(vacancyRepository, times(1)).findById(vacancyId);
        verify(vacancyRepository, times(1)).save(vacancy);
        verify(s3Service, times(1)).uploadFile(any());
    }

    @Test
    void addCover_shouldExceptionWhenVacancyNotExists() {
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vacancyService.addCover(vacancyId, cover));
        verify(vacancyRepository, times(1)).findById(vacancyId);
    }

    @Test
    void addCover_shouldExceptionWhenVacancyAlreadyHasImage() {
        vacancy.setCoverImageKey(vacancyStartImageKey);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));

        assertThrows(DataValidationException.class, () -> vacancyService.addCover(vacancyId, cover));
        verify(vacancyRepository, times(1)).findById(vacancyId);
    }

    @Test
    void addCover_shouldExceptionWhenCoverImageIsEmpty() {
        MultipartFile cover = new MockMultipartFile("name", new byte[0]);
        when(vacancyRepository.findById(vacancyId)).thenReturn(Optional.of(vacancy));

        assertThrows(FileException.class, () -> vacancyService.addCover(vacancyId, cover));
        verify(vacancyRepository, times(1)).findById(vacancyId);
    }

    private MultipartFile initFile() {
        try{
            BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "jpg", baos);
            byte[] imageData = baos.toByteArray();

            return new MockMultipartFile(
                    vacancyName,
                    "cover.jpg",
                    "image/jpeg",
                    imageData
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}