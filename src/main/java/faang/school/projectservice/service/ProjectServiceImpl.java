package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.ProjectNameExistException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectJpaRepository projectRepository;
    private final UserContext userContext;
    private final ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    @Override
    public ProjectDto create(ProjectDto projectDto) {
        ensureOwnerIsSet(projectDto);
        validateByOwnerIdAndName(projectDto.getOwnerId(), projectDto.getName());
        projectDto.setStatus(ProjectStatus.CREATED);
        Project createdProject = projectRepository.save(projectMapper.toProject(projectDto));
        return projectMapper.toDto(createdProject);
    }

    private void ensureOwnerIsSet(ProjectDto projectDto) {
        if (projectDto.getOwnerId() == null) {
            projectDto.setOwnerId(userContext.getUserId());
        }
    }

    private void validateByOwnerIdAndName(Long ownerId, String name) {
        if (projectRepository.existsByOwnerIdAndName(ownerId, name)) {
            throw new ProjectNameExistException(
                    String.format("This user already have a project with name : %s", name));
        }
    }

}
