package faang.school.projectservice.validator.stage;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertThrows;

public class StageInvitationDtoValidatorTest {
    @InjectMocks
    StageInvitationDtoValidator stageInvitationDtoValidator;

    @Mock
    TeamMemberRepository teamMemberRepository;

    private Long authorId;
    private Long invitedId;
    private Long authorTeamId;
    private Long invitedTeamId;
    private TeamMember author;
    private TeamMember invited;
    private Team authorTeam;
    private Team invitedTeam;

    @BeforeEach
    public void setUp() {
        authorId = 1L;
        invitedId = 2L;
        authorTeamId = 3L;
        invitedTeamId = 4L;

        author = new TeamMember();
        invited = new TeamMember();
        authorTeam = new Team();
        invitedTeam = new Team();

        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test getting IllegalArgumentException when ids equals")
    public void testValidateEqualsId() {
        assertThrows(IllegalArgumentException.class,
                () -> stageInvitationDtoValidator.validateEqualsId(authorId, authorId));
    }

    @Test
    @DisplayName("Test that invited and author are in the one Team")
    public void testValidateInvitedMemberTeam() {
        authorTeam.setId(authorTeamId);
        invitedTeam.setId(invitedTeamId);
        author.setTeam(authorTeam);
        invited.setTeam(invitedTeam);
        Mockito.when(teamMemberRepository.findById(authorId)).thenReturn(author);
        Mockito.when(teamMemberRepository.findById(invitedId)).thenReturn(invited);

        assertThrows(RuntimeException.class,
                () -> stageInvitationDtoValidator.validateInvitedMemberTeam(authorId, invitedId));
    }
}
