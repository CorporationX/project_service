package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.dto.vacancy.VacancyCoverDto;
import faang.school.projectservice.exception.BusinessException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.NoSuchPhotoException;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.multipartfile.CustomMultipartFile;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
@Service
public class VacancyService {
    private static final int MAX_SIZE = 512;
    private static final Logger log = LoggerFactory.getLogger(VacancyService.class);

    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final AmazonS3Service amazonS3Service;
    private final UserService userService;

    @Transactional
    public VacancyCoverDto addVacancyCover(Long vacancyId, MultipartFile file) {
        Vacancy vacancy = findById(vacancyId);
        UserDto userDto = userService.getUser(vacancy.getProject().getOwnerId());

        userService.checkUser(userDto.id());

        MultipartFile resizedFile = resizeImage(file);
        String key = amazonS3Service.uploadFile("vacancy", resizedFile);
        vacancy.setCoverImageKey(key);
        return vacancyMapper.toCoverDto(vacancy);
    }

    @Transactional
    public VacancyCoverDto deleteVacancyCover(Long vacancyId) {
        Vacancy vacancy = findById(vacancyId);
        UserDto userDto = userService.getUser(vacancy.getProject().getOwnerId());

        userService.checkUser(userDto.id());

        String key = vacancy.getCoverImageKey();

        try {
            amazonS3Service.deleteFIle(key);
        } catch (NoSuchPhotoException exception) {
            log.error(exception.getMessage());
        } finally {
            vacancy.setCoverImageKey(null);
        }

        return vacancyMapper.toCoverDto(vacancy);
    }

    public Vacancy findById(@NotNull Long vacancyId) {
        return vacancyRepository.findById(vacancyId)
                .orElseThrow(() -> new EntityNotFoundException("Вакансия с таким id не найдена"));
    }

    private MultipartFile resizeImage(MultipartFile file) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            ImageIO.write(changeSize(file, MAX_SIZE), "jpg", outputStream);
        } catch (IOException e) {
            throw new BusinessException(e.getMessage());
        }

        return new CustomMultipartFile(file.getName(),
                file.getOriginalFilename(),
                file.getContentType(),
                outputStream.toByteArray());
    }

    private BufferedImage changeSize(MultipartFile file, int maxSize) throws IOException {
        int newHeight;
        int newWidth;

        BufferedImage image = ImageIO.read(file.getInputStream());


        int originalHeight = image.getHeight();
        int originalWidth = image.getWidth();

        if (originalHeight > originalWidth) {
            newHeight = maxSize;
            newWidth = (int) (originalWidth * ((double) maxSize / originalHeight));
        } else {
            newWidth = maxSize;
            newHeight = (int) (originalHeight * ((double) maxSize / originalWidth));
        }

        BufferedImage resizedImage = new BufferedImage(
                newWidth,
                newHeight,
                BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(image, 0, 0, newWidth, newHeight, null);
        graphics2D.dispose();

        return resizedImage;
    }
}
