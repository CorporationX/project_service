package faang.school.projectservice.service.impl;

import faang.school.projectservice.adapter.VacancyRepositoryAdapter;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.FileException;
import faang.school.projectservice.file.FileMultipartFile;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.service.ImageProcessor;
import faang.school.projectservice.service.S3Service;
import faang.school.projectservice.service.VacancyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class VacancyServiceImpl implements VacancyService {
    private static final String FOLDER_NAME = "vacancy";

    private final VacancyRepositoryAdapter vacancyRepositoryAdapter;
    private final ImageProcessor imageProcessor;
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
        BufferedImage resizeImage = imageProcessor.resizeImage(file);
        return imageProcessor.convertImageToMultipartFile(resizeImage, file.getName(), file.getOriginalFilename(),
                file.getContentType());
    }


    private void checkCanDeleteCover(Vacancy vacancy, Long userId) {
        boolean isVacancyOwner = Objects.equals(vacancy.getCreatedBy(), userId);
        boolean isProjectOwner = Objects.equals(vacancy.getProject().getOwnerId(), userId);
        boolean isProjectManager = vacancy.getProject().getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(teamMember -> Objects.equals(teamMember.getId(), userId))
                .flatMap(teamMember -> teamMember.getRoles().stream())
                .anyMatch(teamRole -> teamRole == TeamRole.MANAGER);
        if (!(isVacancyOwner && isProjectOwner && isProjectManager)) {
            log.error("The user {} does not have enough rights to delete the vacancy cover", userId);
            throw new DataValidationException(String.format("The user %d does not have enough rights to delete the " +
                    "vacancy cover", userId));
        }
    }
}
