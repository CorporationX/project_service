package faang.school.projectservice.service.project;

import faang.school.projectservice.exception.ApiException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

import static faang.school.projectservice.service.project.util.ProjectServiceErrorMessages.PROJECT_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectJpaRepository projectJpaRepository;

    @Transactional
    public void updateStorageSize(Long projectId, BigInteger storageSize) {
        Project project = findById(projectId);
        project.setStorageSize(storageSize);

        save(project);
    }

    @Transactional(readOnly = true)
    public Project findById(long id) {
        return projectJpaRepository.findById(id).orElseThrow(() ->
                new ApiException(PROJECT_NOT_FOUND, HttpStatus.NOT_FOUND, id));
    }

    @Transactional
    public Project save(Project project) {
        return projectJpaRepository.save(project);
    }

    @Transactional(readOnly = true)
    public Project getProjectById(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }
}
