package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.client.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.client.subproject.ProjectDto;
import faang.school.projectservice.mapper.subproject.SubProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.subproject.ValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final SubProjectMapper subProjectMapper;
    private final ValidatorService validatorService;
    private final ProjectRepository projectRepository;

    public ProjectDto create(ProjectDto projectDto) {

        //TODO create tests
        CreateSubProjectDto subProjectDto = subProjectMapper.mapToSubDto(projectDto);
        validatorService.isParentProjectExists(subProjectDto.getParentProject().getId());
        validatorService.isProjectExists(subProjectDto.getName());
        Project parentProject = projectRepository.findById(subProjectDto.getParentProject().getId());
        Project projectToSave = subProjectMapper.mapToEntity(subProjectDto);
        projectToSave.setParentProject(parentProject);
        return subProjectMapper.mapToProjectDto(projectRepository.save(projectToSave));
    }
}
