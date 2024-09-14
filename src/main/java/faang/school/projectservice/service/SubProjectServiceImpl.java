package faang.school.projectservice.service;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.repository.ProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class SubProjectServiceImpl implements SubProjectService{
    private ProjectRepository projectRepository;
    private ProjectMapper projectMapper;

    public CreateSubProjectDto createSubProject(CreateSubProjectDto subProjectDto) {
        projectRepository.getProjectById(subProjectDto.getParentProjectId());
        var project = projectRepository.save(projectMapper.toEntity(subProjectDto));
        return projectMapper.toDTO(project);
    }
}
