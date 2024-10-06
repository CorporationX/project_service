package faang.school.projectservice.service.project;

import faang.school.projectservice.exception.ApiException;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.Optional;

import static faang.school.projectservice.service.project.util.ProjectServiceErrorMessages.PROJECT_NOT_FOUND;
import static faang.school.projectservice.util.project.ProjectFabric.buildProject;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    private static final long PROJECT_ID = 1L;
    private static final long PROJECT_NEW_SIZE = 2L;

    @Mock
    private ProjectJpaRepository projectJpaRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    @DisplayName("Update storage size successful")
    void testUpdateStorageSize() {
        Project project = buildProject(PROJECT_ID);
        when(projectJpaRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));
        projectService.updateStorageSize(PROJECT_ID, BigInteger.valueOf(PROJECT_NEW_SIZE));

        verify(projectJpaRepository).save(project);
    }

    @Test
    @DisplayName("Given wrong project id when find then throw exception")
    void testFindByIdNotFoundException() {
        when(projectJpaRepository.findById(PROJECT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.findById(PROJECT_ID))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(PROJECT_NOT_FOUND, PROJECT_ID);
    }

    @Test
    @DisplayName("Find project by id successful")
    void testFindByIdSuccessful() {
        Project project = buildProject(PROJECT_ID);
        when(projectJpaRepository.findById(PROJECT_ID)).thenReturn(Optional.of(project));

        assertThat(projectService.findById(PROJECT_ID))
                .isEqualTo(project);
    }

    @Test
    @DisplayName("Save project successful")
    void testSave() {
        Project project = buildProject(PROJECT_ID);
        projectService.save(project);

        verify(projectJpaRepository).save(project);
    }
}
