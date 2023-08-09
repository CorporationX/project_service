package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.SubprojectDtoForCreate;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubprojectService {
    private final ProjectRepository projectRepository;

    @Transactional
    public void createSubproject(Long parentProjectId, SubprojectDtoForCreate subprojectDto) {
        Project parentProject = projectRepository.getProjectById(parentProjectId);


//        parentProject
    }

    private Project getProject(Long id) {
        return projectRepository.getProjectById(id);
    }
}
