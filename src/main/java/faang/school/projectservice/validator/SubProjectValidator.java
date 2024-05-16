package faang.school.projectservice.validator;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import static faang.school.projectservice.model.ProjectStatus.COMPLETED;
import static faang.school.projectservice.model.ProjectVisibility.PRIVATE;
import static faang.school.projectservice.model.ProjectVisibility.PUBLIC;

@Component
public class SubProjectValidator {

    public void validateSubProjectVisibility(Project parent, CreateSubProjectDto subProjectDto) {
        if (parent.getVisibility().equals(PRIVATE) &&
                subProjectDto.getVisibility().equals(PUBLIC)) {
            throw new IllegalArgumentException("Нельзя добавить публичный подпроект с приватному проекту");
        }
    }

    public boolean isAllSubProjectsCompleted(Project parent) {
        return parent.getChildren().stream().allMatch(pr -> pr.getStatus().equals(COMPLETED));
    }

    public void validCorrectVisibility(ProjectDto projectDto, Project parent) {
        if (projectDto.getVisibility().equals(PUBLIC) && parent.getVisibility().equals(PRIVATE)) {
            throw new IllegalArgumentException("Нельзя установить публичный статус подпроекта для приватного проекта");
        }

    }
}
