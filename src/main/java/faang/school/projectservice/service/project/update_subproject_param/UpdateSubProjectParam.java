package faang.school.projectservice.service.project.update_subproject_param;


import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;

public interface UpdateSubProjectParam {
    boolean isExecutable(UpdateSubProjectDto paramDto);

    void execute(ProjectDto projectDto, UpdateSubProjectDto paramDto);
}
