package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.CreateSubProjectDto;
import faang.school.projectservice.dto.client.SubProjectDto;
import faang.school.projectservice.dto.client.UpdateSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SubProjectService {

    SubProjectDto createSubProject(CreateSubProjectDto subProjectDto);

    SubProjectDto updateSubProject(UpdateSubProjectDto updateSubProjectDto);

    List<SubProjectDto> getSubprojects(Project project, String filterName, ProjectStatus filterStatus);
}
