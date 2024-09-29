package faang.school.projectservice.service.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;

    @Transactional
    public void updateStorageSize(Long projectId, BigInteger storageSize) {
        Project project = projectRepository.getProjectById(projectId);
        project.setStorageSize(storageSize);

        projectRepository.save(project);
    }
}
