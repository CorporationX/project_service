package faang.school.projectservice.validator;

import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
<<<<<<< HEAD
public class ProjectValidator {
    public void validateSubProjectVisibility(ProjectVisibility parentProjectVisibility,
                                             ProjectVisibility childProjectVisibility) {
        if (parentProjectVisibility == ProjectVisibility.PUBLIC &&
                childProjectVisibility == ProjectVisibility.PRIVATE) {
            throw new IllegalStateException("Your cannot create Private subproject from Public project");
        }
    }
    private final ProjectRepository projectRepository;
    private final String VALIDATION_MASSAGE_NAME_OF_PROJECT_IS_EXISTS =
            "The owner with id:%d already has a project with  name:%s";
    public void validateProjectByOwnerWithNameOfProject(ProjectDto projectDto) {
       if(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())){
           throw new DataValidationException(String.format(VALIDATION_MASSAGE_NAME_OF_PROJECT_IS_EXISTS,
                   projectDto.getOwnerId(), projectDto.getName()));
       }
    }
}
