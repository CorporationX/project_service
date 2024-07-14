package faang.school.projectservice.validator.stage;

import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertThrows;

public class StageDtoValidatorTest {
    @InjectMocks
    private StageDtoValidator stageDtoValidator;

    @Mock
    private ProjectRepository projectRepository;

    private Long id;
    private List<StageRoles> stageRolesList;

    @BeforeEach
    public void setUp() {
        id = 1L;

        StageRoles stageRoles = new StageRoles();
        stageRolesList = new ArrayList<>(Collections.singletonList(stageRoles));

        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("testing that validateProjectId throws EntityNotFoundException when project with given id is not in the database")
    public void testValidateProjectId() {
        Mockito.when(projectRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> stageDtoValidator.validateProjectId(id));
    }

    @Test
    @DisplayName("testing that validateStageRolesCount throws IllegalArgumentException when count is not initialised")
    public void testValidateStageRolesCount() {
        assertThrows(IllegalArgumentException.class,
                () -> stageDtoValidator.validateStageRolesCount(stageRolesList));
    }
}
