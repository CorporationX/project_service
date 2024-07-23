package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateSubProjectDtoValidatorImpl implements CreateSubProjectDtoValidator {
    private final ProjectRepository projectRepository;

    @Override
    public void validateOnCreate(CreateSubProjectDto projectDto) {
        validateVisibility(projectDto);
        validateIsParentProjectExist(projectDto);
    }
    @Override
    public void validateOnUpdate(UpdateSubProjectDto projectDto) {
        validateIsProjectExist(projectDto);
        validateIsSubProjectsHaveSameStatus(projectDto);
    }

    private void validateVisibility(CreateSubProjectDto projectDto) {
        Project project = projectRepository.getProjectById(projectDto.getParentId());
        if (project.getVisibility().equals(ProjectVisibility.PUBLIC)
                && projectDto.getVisibility().equals(ProjectVisibility.PRIVATE)) {
            throw new ValidationException("Project visibility is public");
        }
    }

    private void validateIsParentProjectExist(CreateSubProjectDto projectDto) {
        if (projectRepository.existsById(projectDto.getParentId())) {
            return;
        } else throw new ValidationException("Parent project does not exist");

    }
    private void validateIsProjectExist(UpdateSubProjectDto projectDto) {
        if (projectRepository.existsById(projectDto.getId())) {
            return;
        } else throw new ValidationException("Project does not exist");

    }
    private void validateIsSubProjectsHaveSameStatus(UpdateSubProjectDto projectDto) {
        List<String> projectNamesWithWrongStatus = new ArrayList<>();
        for(Project p:projectRepository.getProjectById(projectDto.getId()).getChildren()){
            if(!p.getStatus().equals(projectDto.getStatus())){
                projectNamesWithWrongStatus.add(p.getName());
            }
        }
        if(!projectNamesWithWrongStatus.isEmpty()){
            throw new ValidationException("Sub projects with names:"+
                    projectNamesWithWrongStatus+" does not have same status as parent project");
        }
    }

}
