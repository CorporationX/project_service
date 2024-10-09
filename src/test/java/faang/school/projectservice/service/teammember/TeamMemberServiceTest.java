package faang.school.projectservice.service.teammember;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamMemberServiceTest {

    private static final List<Long> IDS = new ArrayList<>();
    private static final Long ID_ONE = 1L;
    private static final Long ID_TWO = 2L;
    @InjectMocks
    private TeamMemberService teamMemberService;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    private TeamMember teamMemberOne;
    private TeamMember teamMemberTwo;


    @Nested
    class PositiveTest {

        @BeforeEach
        void init() {
            IDS.add(ID_ONE);
            IDS.add(ID_TWO);

            teamMemberOne = TeamMember.builder()
                    .id(ID_ONE)
                    .build();

            teamMemberTwo = TeamMember.builder()
                    .id(ID_TWO)
                    .build();
        }

        @Test
        @DisplayName("Returning the user by ID")
        void whenAppealDbThenReturnById() {
            when(teamMemberRepository.findById(ID_ONE)).thenReturn(Optional.of(teamMemberOne));

            TeamMember result = teamMemberService.getTeamMemberById(ID_ONE);

            assertNotNull(result);
            assertEquals(result, teamMemberOne);

            verify(teamMemberRepository).findById(ID_ONE);
        }

        @Test
        @DisplayName("Returning a list of all users by ID")
        void whenAppealDbThenReturnAllById() {
            List<TeamMember> stageRoles = new ArrayList<>(Arrays.asList(teamMemberOne, teamMemberTwo));

            when(teamMemberRepository.findAllById(IDS)).thenReturn(stageRoles);

            List<TeamMember> result = teamMemberService.getAllById(IDS);

            assertNotNull(result);
            assertEquals(result, stageRoles);

            verify(teamMemberRepository).findAllById(IDS);
        }
    }

    @Nested
    class NegativeTest {

        @Test
        @DisplayName("We are returning a message that the user has not been found")
        void whenAppealDbThenReturnEntityNotFoundException() {
            when(teamMemberRepository.findById(ID_ONE)).thenReturn(Optional.empty());

            assertEquals("Team member with id " + ID_ONE + " does not exist!",
                    assertThrows(EntityNotFoundException.class, () -> {
                        teamMemberService.getTeamMemberById(ID_ONE);
                    }).getMessage());

            verify(teamMemberRepository).findById(ID_ONE);
        }
    }
}