package faang.school.projectservice.service.project.update_subproject_param;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.model.ProjectVisibility;

public class UpdateSubProjectVisibility implements UpdateSubProjectParam{
    @Override
    public boolean isExecutable(UpdateSubProjectDto paramDto) {
        return paramDto.getVisibility() != null;
    }

    @Override
    public void execute(ProjectDto projectDto, UpdateSubProjectDto paramDto) {
        ProjectVisibility visibility = paramDto.getVisibility();
        if (visibility == ProjectVisibility.PRIVATE) {
            projectDto.getChildren().
                    forEach(project -> {

                        UpdateSubProjectDto subProjectDto = new UpdateSubProjectDto();
                        subProjectDto.setVisibility(visibility);
                        execute(project, subProjectDto);
                    });
        }
        projectDto.setVisibility(visibility);
    }
}
