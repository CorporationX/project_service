package faang.school.projectservice.service.subproject;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubSubProjectServiceValidateTest {
    @InjectMocks
    private SubProjectServiceValidate serviceValidate;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private StageJpaRepository stageRepository;

    @Test
    void testGetTeamsSuccess() {
        CreateSubProjectDto dto = CreateSubProjectDto.builder().teamsIds(List.of(1L, 2L)).build();
        List<Team> teams = serviceValidate.getTeams(dto);
        assertNotNull(teams);
        verify(teamRepository, times(1)).findAllById(dto.getTeamsIds());
    }

    @Test
    void testGetTeamsNull() {
        CreateSubProjectDto dto = CreateSubProjectDto.builder().build();
        List<Team> teams = serviceValidate.getTeams(dto);
        assertNull(teams);
    }

    @Test
    void testGetStagesSuccess() {
        CreateSubProjectDto dto = CreateSubProjectDto.builder().stagesIds(List.of(1L, 2L)).build();
        List<Stage> stages = serviceValidate.getStages(dto);
        assertNotNull(stages);
        verify(stageRepository, times(1)).findAllById(dto.getStagesIds());
    }

    @Test
    void testGetStagesNull() {
        CreateSubProjectDto dto = CreateSubProjectDto.builder().build();
        List<Stage> stages = serviceValidate.getStages(dto);
        assertNull(stages);
    }

    @Test
    void testIsVisibilityDtoAndProjectNotEqualsTrue() {
        CreateSubProjectDto dto = CreateSubProjectDto.builder().visibility(ProjectVisibility.PRIVATE).build();
        Project project = Project.builder().visibility(ProjectVisibility.PUBLIC).build();

        boolean result = serviceValidate.isVisibilityDtoAndProjectNotEquals(dto, project);
        assertTrue(result);
    }

    @Test
    void testIsVisibilityDtoAndProjectNotEqualsFalse() {
        CreateSubProjectDto dto = CreateSubProjectDto.builder().visibility(ProjectVisibility.PUBLIC).build();
        Project project = Project.builder().visibility(ProjectVisibility.PUBLIC).build();

        boolean result = serviceValidate.isVisibilityDtoAndProjectNotEquals(dto, project);
        assertFalse(result);
    }

    @Test
    void testIsStatusDtoAndProjectNotEqualsTrue() {
        CreateSubProjectDto dto = CreateSubProjectDto.builder().status(ProjectStatus.COMPLETED).build();
        Project project = Project.builder().status(ProjectStatus.IN_PROGRESS).build();

        boolean result = serviceValidate.isStatusDtoAndProjectNotEquals(dto, project);
        assertTrue(result);
    }

    @Test
    void testIsStatusDtoAndProjectNotEqualsFalse() {
        CreateSubProjectDto dto = CreateSubProjectDto.builder().status(ProjectStatus.COMPLETED).build();
        Project project = Project.builder().status(ProjectStatus.COMPLETED).build();

        boolean result = serviceValidate.isStatusDtoAndProjectNotEquals(dto, project);
        assertFalse(result);
    }
}