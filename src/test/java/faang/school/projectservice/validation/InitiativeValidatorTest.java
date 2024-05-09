package faang.school.projectservice.validation;

import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class InitiativeValidatorTest {
    @Mock
    private StageRepository stageRepository;
    @InjectMocks
    private InitiativeValidator validator;

    private InitiativeDto dto;
    private TeamMember curator;
    private Project project;

    @BeforeEach
    void init() {
        dto = InitiativeDto.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .curatorId(2L)
                .projectId(3L)
                .status(InitiativeStatus.IN_PROGRESS)
                .stageIds(List.of(4L, 5L, 6L))
                .build();

        project = Project.builder().id(7L).build();

        curator = TeamMember.builder()
                .id(8L)
                .userId(2L)
                .team(Team.builder()
                        .project(Project.builder()
                                .id(7L)
                                .build())
                        .build())
                .build();
    }

    @Test
    void validateNullProject() {
        dto.setProjectId(null);
        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(dto));
        assertEquals("initiative projectId must not be null", e.getMessage());
    }

    @Test
    void validateNullName() {
        dto.setName(null);
        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(dto));
        assertEquals("initiative name must not be null", e.getMessage());
    }

    @Test
    void validateBlankName() {
        dto.setName("  ");
        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(dto));
        assertEquals("initiative name must not be null", e.getMessage());
    }

    @Test
    void validateNullDescription() {
        dto.setDescription(null);
        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(dto));
        assertEquals("initiative description must not be null", e.getMessage());
    }

    @Test
    void validateBlankDescription() {
        dto.setDescription("   ");
        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(dto));
        assertEquals("initiative description must not be null", e.getMessage());
    }

    @Test
    void validateNullCuratorId() {
        dto.setCuratorId(null);
        DataValidationException e = assertThrows(DataValidationException.class, () -> validator.validate(dto));
        assertEquals("initiative curatorId must not be null", e.getMessage());
    }

    @Test
    void validate() {
        assertDoesNotThrow(() -> validator.validate(dto));
    }

    @Test
    void validateCuratorAndProjectDifferentProjects() {
        project.setId(10L);
        DataValidationException e = assertThrows(DataValidationException.class,
                () -> validator.validateCuratorAndProject(curator, project));
        assertEquals("curator not in the initiative project", e.getMessage());
    }

    @Test
    void validateCuratorAndProject() {
        assertDoesNotThrow(() -> validator.validateCuratorAndProject(curator, project));
    }
}