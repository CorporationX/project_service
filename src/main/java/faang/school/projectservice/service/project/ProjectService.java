package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectServiceValidator validator;
    private final SubProjectMapper subProjectMapper;
    private final ProjectMapper projectMapper;

    public ProjectDto createSubProject(CreateSubProjectDto subProjectDto) {
        Project parentProject = validator.getParentAfterValidateSubProject(subProjectDto);
        Project subProject = projectRepository.save(subProjectMapper.toEntity(subProjectDto, parentProject));
        subProject.setStatus(ProjectStatus.CREATED);
        return projectMapper.toDto(subProject);
    }

}
