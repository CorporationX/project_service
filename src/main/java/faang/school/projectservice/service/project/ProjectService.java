package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.mapper.moment.ProjectMapper;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Transactional(readOnly = true)
    public ProjectDto getProjectDtoById(long id) {
        var project = projectRepository.getProjectById(id);
        return projectMapper.toDto(project);
    }
}
