package faang.school.projectservice.service.project;

import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    public boolean findById(long id) {
        return projectRepository.findById(id).isPresent();
    }
}
