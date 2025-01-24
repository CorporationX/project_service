package faang.school.projectservice.repository;

import faang.school.projectservice.exception.DataNotFoundException;
import faang.school.projectservice.model.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectRepositoryAdapter {
    private final ProjectRepository projectRepository;

    public Project getProjectById(long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(String.format("Проект с id:%s не найден!", id)));
    }
}
