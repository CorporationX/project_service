package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.MessageError;
import faang.school.projectservice.exception.TeamMemberNotFoundException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.service.SetUpFileForResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceValidatorTest extends SetUpFileForResource {
    @Mock
    TeamMemberJpaRepository teamMemberJpaRepository;
    @InjectMocks
    ResourceValidator resourceValidator;


    @Test
    public void testValidateForTeamMemberExistence_TeamMemberNotFound() {
        TeamMemberNotFoundException e = assertThrows(TeamMemberNotFoundException.class,
                () -> resourceValidator.validateForTeamMemberExistence(firstTeamMember.getUserId(), projectId)
        );

        assertEquals(e.getMessage(), String.format(MessageError.TEAM_MEMBER_BY_USER_AND_PROJECT_IDS_NOT_FOUND_EXCEPTION.getMessage(), firstTeamMember.getUserId(), projectId));
    }

    @Test
    public void testValidateForTeamMemberExistence() {
        when(teamMemberJpaRepository.findByUserIdAndProjectId(firstTeamMember.getUserId(), projectId)).thenReturn(firstTeamMember);
        assertEquals(firstTeamMember, resourceValidator.validateForTeamMemberExistence(firstTeamMember.getUserId(), projectId));
    }

    @Test
    public void testValidateForOwner_TeamMemberIsNotOwner() {
        when(teamMemberJpaRepository.findByUserIdAndProjectId(secondTeamMember.getUserId(), projectId)).thenReturn(secondTeamMember);
        assertThrows(DataValidationException.class, () -> resourceValidator.validateForOwner(secondTeamMember.getUserId(), firstResource));
    }

    @Test
    public void testValidateForOwner() {
        when(teamMemberJpaRepository.findByUserIdAndProjectId(firstTeamMember.getUserId(), projectId)).thenReturn(firstTeamMember);
        resourceValidator.validateForOwner(firstTeamMember.getUserId(), firstResource);
    }
}