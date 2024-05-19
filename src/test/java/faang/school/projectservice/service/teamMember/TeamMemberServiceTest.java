package faang.school.projectservice.service.teamMember;

import faang.school.projectservice.exception.RoleProcessingException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.testData.internship.InternshipTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static faang.school.projectservice.model.TeamRole.DEVELOPER;
import static faang.school.projectservice.model.TeamRole.INTERN;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceTest {
    @InjectMocks
    private TeamMemberService teamMemberService;
    @Mock
    private TeamMemberRepository teamMemberRepository;


    @Nested
    class HiringInternsTest {
        private Internship internship;
        private Team internshipTeam;

        @BeforeEach
        void setUp() {
            InternshipTestData internshipTestData = new InternshipTestData();
            internship = internshipTestData.getInternship();
            internshipTeam = internshipTestData.getTeamMemberTestData().getInternshipTeam();
        }

        @DisplayName("should hire all interns when all interns completed all tasks")
        @Test
        void hireAllInternsTest() {
            List<TeamMember> teamMembers = internshipTeam.getTeamMembers();
            int expectedInternshipTeamSize = teamMembers.size();


            assertDoesNotThrow(() -> teamMemberService.hireInterns(internship));

            assertEquals(expectedInternshipTeamSize, teamMembers.size());
            verify(teamMemberRepository, times(expectedInternshipTeamSize)).save(any(TeamMember.class));

            teamMembers.forEach(intern -> {
                assertFalse(intern.getRoles().contains(INTERN));
            });
        }

        @DisplayName("should hire not all interns when some of them didn't completed all tasks")
        @Test
        void hireNotAllInternsTest() {
            TeamMember internToBeFired = internship.getInterns().get(0);
            List<Task> internToBeFiredTasks = internToBeFired.getStages().get(0).getTasks();
            internToBeFiredTasks.add(Task.builder().status(TaskStatus.TODO).build());

            List<TeamMember> teamMembers = internshipTeam.getTeamMembers();
            int expectedInternshipTeamSize = teamMembers.size() - 1;

            assertDoesNotThrow(() -> teamMemberService.hireInterns(internship));

            assertEquals(expectedInternshipTeamSize, teamMembers.size());
            verify(teamMemberRepository, times(expectedInternshipTeamSize)).save(any(TeamMember.class));
            teamMembers.forEach(intern -> assertFalse(intern.getRoles().contains(INTERN)));
        }

        @DisplayName("should throw exception when intern doesn't have INTERN role")
        @Test
        void assignRolesTest() {
            TeamMember internWithoutRole = internship.getInterns().get(0);
            internWithoutRole.getRoles().clear();
            internWithoutRole.getRoles().add(DEVELOPER);

            assertThrows(RoleProcessingException.class,
                    () -> teamMemberService.hireInterns(internship));

            verifyNoInteractions(teamMemberRepository);
        }
    }
}