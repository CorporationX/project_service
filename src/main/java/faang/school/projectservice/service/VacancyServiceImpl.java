package faang.school.projectservice.service;

import faang.school.projectservice.adapter.VacancyRepositoryAdapter;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.file.FileMultipartFile;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class VacancyServiceImpl implements VacancyService {
    private static final String FOLDER_NAME = "vacancy";
    private static final int COVER_MAX_SIZE = 512;
    private final VacancyRepositoryAdapter vacancyRepositoryAdapter;
    private final S3Service s3Service;

    @Override
    public void addCover(Long id, MultipartFile file) {
        Vacancy vacancy = vacancyRepositoryAdapter.findById(id);
        String folder = String.format("%s/%d", FOLDER_NAME, id);
        MultipartFile fileAfterCheck = checkAndConvertFile(file);
        String coverImageKey = s3Service.uploadFile(fileAfterCheck, folder);
        vacancy.setCoverImageKey(coverImageKey);
        vacancyRepositoryAdapter.save(vacancy);
        log.info("Cover of vacancy with id {} has been successfully added", id);
    }

    @Override
    public InputStream getVacancyCover(Long id) {
        Vacancy vacancy = vacancyRepositoryAdapter.findById(id);
        String coverImageKey = vacancy.getCoverImageKey();
        return s3Service.downloadFile(coverImageKey);
    }

    @Override
    public void deleteVacancyCover(Long id, Long userId) {
        Vacancy vacancy = vacancyRepositoryAdapter.findById(id);
        checkCanDeleteCover(vacancy, userId);
        String coverImageKey = vacancy.getCoverImageKey();
        s3Service.deleteFile(coverImageKey);
        vacancy.setCoverImageKey(new String());
        vacancyRepositoryAdapter.save(vacancy);
        log.info("Cover of vacancy with id {} has been successfully deleted", id);
    }

    private MultipartFile checkAndConvertFile(MultipartFile file) {
        try {
            double ratio = 0.0;
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage.getWidth() > COVER_MAX_SIZE || bufferedImage.getHeight() > COVER_MAX_SIZE) {
                if (bufferedImage.getWidth() > bufferedImage.getHeight()) {
                    ratio = (double) COVER_MAX_SIZE / bufferedImage.getWidth();
                } else {
                    ratio = (double) COVER_MAX_SIZE / bufferedImage.getHeight();
                }
                int widthNew = (int) (ratio * bufferedImage.getWidth());
                int heightNew = (int) (ratio * bufferedImage.getHeight());
                BufferedImage resizeImage = resizeImage(bufferedImage, widthNew, heightNew);
                log.info("Image {} resized.", file.getOriginalFilename());
                return convertImageToMultipartFile(resizeImage, file);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileException(e.getMessage());
        }
        return file;
    }

    private MultipartFile convertImageToMultipartFile(BufferedImage resizeImage, MultipartFile file) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            String fileType = file.getContentType().substring(file.getContentType().lastIndexOf("/") + 1);
            ImageIO.write(resizeImage, fileType, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileException(e.getMessage());
        }
        byte[] imageBytes = outputStream.toByteArray();
        return new FileMultipartFile(file.getName(),
                file.getOriginalFilename(),
                file.getContentType(),
                imageBytes,
                imageBytes.length);
    }

    private BufferedImage resizeImage(BufferedImage bufferedImage, int width, int height) {
        Image resultingImage = bufferedImage.getScaledInstance(width, height, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    private void checkCanDeleteCover(Vacancy vacancy, Long userId) {
        boolean isVacancyOwner = Objects.equals(vacancy.getCreatedBy(), userId);
        boolean isProjectOwner = Objects.equals(vacancy.getProject().getOwnerId(), userId);
        boolean isProjectManager = vacancy.getProject().getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(teamMember -> Objects.equals(teamMember.getId(), userId))
                .flatMap(teamMember -> teamMember.getRoles().stream())
                .filter(teamRole -> Objects.equals(teamRole, TeamRole.MANAGER))
                .findAny()
                .isEmpty();
        if (!(isVacancyOwner || isProjectOwner || isProjectManager)) {
            log.error("The user {} does not have enough rights to delete the vacancy cover", userId);
            throw new DataValidationException(String.format("The user %d does not have enough rights to delete the " +
                    "vacancy cover", userId));
        }
    }
}
