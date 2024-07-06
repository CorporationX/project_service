package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.exception.DataProjectValidation;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectValidator {

    private final ProjectRepository projectRepository;
    private static final long MAX_PIC_SIZE = 5_242_880L;

    public void checkIsNull(ProjectDto projectDto) {
        if (projectDto == null) {
            throw new DataProjectValidation("Аргумент projectDto не может быть null");
        }
    }

    public void checkIsNull(ProjectFilterDto projectFilterDto) {
        if (projectFilterDto == null) {
            throw new DataProjectValidation("Аргумент projectFilterDto не может быть null");
        }
    }

    public void checkIsNull(Long projectId) {
        if (projectId == null) {
            throw new DataProjectValidation("Аргумент projectId не может быть null");
        }
    }

    public void checkExistProject(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataProjectValidation("Проект с таким названием уже существует");
        }
    }

    public void checkMaxSize(MultipartFile file) {
        if (file.getSize() > MAX_PIC_SIZE) {
            log.error("Thr file size (" + file.getSize() + ") exceeds the maximum");
            throw new DataValidationException("The maximum file size has been exceeded");
        }
    }

    public void checkProjectInDB(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            log.error("The project with this id was not found: " + projectId);
            throw new DataValidationException("The project with this id was not found: " + projectId);
        }
    }

    public void checkExistPicId(Project project) {
        if (project.getCoverImageId() == null) {
            log.error("The project has the c id: "+ project.getId() +" avatar is missing");
            throw new DataValidationException("The project has the c id: "+ project.getId() +" avatar is missing");
        }
    }
}