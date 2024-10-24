package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CampaignValidatorImplTest {

    private final CampaignValidatorImpl campaignValidator = new CampaignValidatorImpl();
    
    private TeamMemberDto teamMemberDto;

    @BeforeEach
    void setUp() {
        teamMemberDto = TeamMemberDto.builder()
                .userId(1L)
                .build();
    }

    @Test
    void testValidationCampaignCreator_Success() {
        ProjectDto project = new ProjectDto();
        project.setOwnerId(1L);
        teamMemberDto.setRoles(List.of(TeamRole.MANAGER));

        assertDoesNotThrow(() -> campaignValidator.validationCampaignCreator(teamMemberDto, project));
    }

    @Test
    void testValidationCampaignCreator_NoManagerRole() {
        ProjectDto project = new ProjectDto();
        project.setOwnerId(1L);
        String message = "User with id 1 cannot create";
        teamMemberDto.setRoles(List.of(TeamRole.DEVELOPER));

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> campaignValidator.validationCampaignCreator(teamMemberDto, project));

        assertEquals(message, thrown.getMessage());
    }

    @Test
    void testValidationCampaignCreator_NotProjectOwner() {
        ProjectDto project = new ProjectDto();
        project.setOwnerId(2L);
        String message = "User with id 1 cannot create";
        teamMemberDto.setRoles(List.of(TeamRole.MANAGER));

        DataValidationException thrown = assertThrows(DataValidationException.class,
                () -> campaignValidator.validationCampaignCreator(teamMemberDto, project));

        assertEquals(message, thrown.getMessage());
    }
}
