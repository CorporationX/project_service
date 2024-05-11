package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validation.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);
    private final ProjectValidator projectValidator;
    private final UserContext userContext;

    public ProjectDto create(ProjectDto projectDto){
        if(projectDto.getOwnerId() == null){
            projectDto.setOwnerId(userContext.getUserId());
        }
        projectValidator.validateProjectByOwnerIdAndNameOfProject(projectDto);
        projectDto.setStatus(ProjectStatus.CREATED);
        Project createdProject = projectRepository.save(projectMapper.toProject(projectDto));
        return projectMapper.toDto(createdProject);
    }
}
