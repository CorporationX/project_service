package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.mapper.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.MomentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectServiceValidator validator;
    private final SubProjectMapper subProjectMapper;
    private final ProjectMapper projectMapper;
    private final MomentService momentService;

    public ProjectDto createSubProject(CreateSubProjectDto subProjectDto) {
        Project parentProject = validator.getParentAfterValidateSubProject(subProjectDto);
        Project subProject = subProjectMapper.toEntity(subProjectDto);
        subProject.setParentProject(parentProject);
        subProject.setStatus(ProjectStatus.CREATED);
        projectRepository.save(subProject);

        return projectMapper.toDto(subProject);
    }

    public ProjectDto updateSubProject(Long subProjectId, UpdateSubProjectDto updateDto) {
        Project subProject = validator.validateSubProjectForUpdate(subProjectId, updateDto);
        if (validator.readyToNewMoment(subProject, updateDto.getStatus())) {
            momentService.addMomentByName(subProject, "All subprojects finished");
        }
        subProjectMapper.updateEntity(updateDto, subProject);
        subProject.setUpdatedAt(LocalDateTime.now());
        return projectMapper.toDto(projectRepository.save(subProject));
    }

}
