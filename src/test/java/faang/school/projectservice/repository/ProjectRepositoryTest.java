package faang.school.projectservice.repository;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void saveProjectShouldPersistDataWithAllRequiredProperties() {
        Project project = new Project();
        project.setName("Test Project");
        project.setStatus(ProjectStatus.CREATED);
        project.setVisibility(ProjectVisibility.PUBLIC);

        Project savedProject = projectRepository.save(project);
        assertNotNull(savedProject.getId());
        assertEquals("Test Project", savedProject.getName());

        Project retrievedProject = projectRepository.getProjectById(savedProject.getId());
        assertNotNull(retrievedProject);
        assertEquals("Test Project", retrievedProject.getName());
    }

    @Test
    void saveProjectShouldThrowExceptionWhenNameIsNull() {
        Project project = new Project();
        project.setStatus(ProjectStatus.CREATED);
        project.setVisibility(ProjectVisibility.PUBLIC);

        assertThrows(DataIntegrityViolationException.class, () -> projectRepository.save(project));
    }

    @Test
    void saveProjectShouldThrowExceptionWhenStatusIsNull() {
        Project project = new Project();
        project.setName("Test Project");
        project.setVisibility(ProjectVisibility.PUBLIC);

        assertThrows(DataIntegrityViolationException.class, () -> projectRepository.save(project));
    }

    @Test
    void saveProjectShouldThrowExceptionWhenVisiblityIsNull() {
        Project project = new Project();
        project.setName("Test Project");
        project.setStatus(ProjectStatus.CREATED);

        assertThrows(DataIntegrityViolationException.class, () -> projectRepository.save(project));
    }
}