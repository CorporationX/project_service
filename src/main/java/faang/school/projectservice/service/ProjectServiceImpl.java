package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectDto getProjectById(Long id) {
        return projectRepository.findById(id)
                .map(projectMapper::toProjectDto)
                .orElseThrow(() -> new ProjectNotFoundException("Проект не найден: " + id));
    }

    @Override
    public List<ProjectDto> getProjectsByIds(List<Long> ids) {
        return projectRepository.findAllById(ids)
                .stream()
                .map(projectMapper::toProjectDto)
                .toList();
    }
}
