package faang.school.projectservice.service.teammember;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.teammember.AddTeamMemberDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.dto.teammember.TeamMemberFilterDto;
import faang.school.projectservice.dto.teammember.UpdateTeamMemberDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.teammember.TeamMemberMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamMemberServiceTest {

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private TeamMemberMapper teamMemberMapper;

    @Mock
    private UserContext userContext;

    @Mock
    private List<Filter<TeamMemberFilterDto, TeamMember>> teamMemberFilters;

    @InjectMocks
    private TeamMemberService teamMemberService;

    private TeamMember teamMember;
    private TeamMemberDto teamMemberDto;
    private UpdateTeamMemberDto updateTeamMemberDto;
    private AddTeamMemberDto addTeamMemberDto;

    @BeforeEach
    public void setup() {
        teamMember = TeamMember.builder()
                .id(2L)
                .userId(2L)
                .roles(List.of(TeamRole.DEVELOPER))
                .nickname("OldNickname")
                .lastModified(LocalDateTime.now())
                .build();

        teamMemberDto = TeamMemberDto.builder()
                .userId(2L)
                .roles(List.of(TeamRole.DEVELOPER))
                .nickname("UpdatedNickname")
                .build();

        updateTeamMemberDto = UpdateTeamMemberDto.builder()
                .id(2L)
                .roles(List.of(TeamRole.DEVELOPER))
                .nickname("UpdatedNickname")
                .build();

        addTeamMemberDto = AddTeamMemberDto.builder()
                .userId(3L)
                .projectId(1L)
                .nickname("NewMember")
                .roles(List.of(TeamRole.DEVELOPER))
                .build();
    }

    @Nested
    @DisplayName("Добавление участника")
    class AddTeamMemberTests {

        @Test
        @DisplayName("Должен успешно добавить нового участника")
        void shouldAddTeamMember() {
            TeamMember owner = TeamMember.builder()
                    .id(1L)
                    .userId(1L)
                    .roles(List.of(TeamRole.OWNER))
                    .build();

            when(userContext.getUserId()).thenReturn(1L);
            when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(owner));
            when(teamMemberRepository.findByUserIdAndProjectId(3L, 1L)).thenReturn(Optional.empty());
            when(teamMemberMapper.toEntity(any(AddTeamMemberDto.class))).thenReturn(teamMember);
            when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(teamMember);
            when(teamMemberMapper.toDto(any(TeamMember.class))).thenReturn(teamMemberDto);

            TeamMemberDto result = teamMemberService.addTeamMember(addTeamMemberDto);

            assertEquals("UpdatedNickname", result.getNickname());
            verify(teamMemberRepository).save(any(TeamMember.class));
        }

        @Test
        @DisplayName("Должен выбросить исключение, если пользователь не владелец или TEAMLEAD")
        void shouldThrowExceptionIfNotOwnerOrTeamLead() {
            TeamMember memberWithoutPermission = TeamMember.builder()
                    .id(1L)
                    .userId(1L)
                    .roles(List.of(TeamRole.DEVELOPER))
                    .build();

            when(userContext.getUserId()).thenReturn(1L);
            when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(memberWithoutPermission));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                teamMemberService.addTeamMember(addTeamMemberDto);
            });

            assertEquals("Only the owner or TEAMLEAD can add new members to the project", exception.getMessage());
            verify(teamMemberRepository, never()).save(any(TeamMember.class));
        }
    }

    @Nested
    @DisplayName("Обновление участника")
    class UpdateTeamMemberTests {

        @Test
        @DisplayName("Должен успешно обновить никнейм участника")
        void shouldUpdateTeamMemberNickname() {
            when(userContext.getUserId()).thenReturn(2L);

            teamMember.setRoles(List.of(TeamRole.TEAMLEAD));

            when(teamMemberRepository.findById(2L)).thenReturn(Optional.of(teamMember));

            when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(teamMember);
            when(teamMemberMapper.toDto(any(TeamMember.class))).thenReturn(teamMemberDto);

            TeamMemberDto result = teamMemberService.updateTeamMember(2L, updateTeamMemberDto);

            assertEquals("UpdatedNickname", result.getNickname());

            verify(teamMemberRepository).save(any(TeamMember.class));
        }

        @Test
        @DisplayName("Должен выбросить исключение, если не TEAMLEAD пытается обновить роли")
        void shouldThrowExceptionIfNotTeamLeadUpdatingRoles() {
            TeamMember updater = TeamMember.builder()
                    .id(1L)
                    .userId(1L)
                    .roles(List.of(TeamRole.DEVELOPER))
                    .build();

            when(userContext.getUserId()).thenReturn(1L);
            when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(updater));
            when(teamMemberRepository.findById(updateTeamMemberDto.getId())).thenReturn(Optional.of(teamMember));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                teamMemberService.updateTeamMember(updateTeamMemberDto.getId(), updateTeamMemberDto);
            });

            assertEquals("Only TEAMLEAD can updateCampaign roles and permissions", exception.getMessage());
            verify(teamMemberRepository, never()).save(any(TeamMember.class));
        }
    }

    @Nested
    @DisplayName("Удаление участника")
    class RemoveTeamMemberTests {

        @Test
        @DisplayName("Должен удалить участника, если удаляет владелец")
        void shouldRemoveTeamMember() {
            Project project = new Project();
            project.setId(10L);
            Team team = new Team();
            team.setProject(project);

            TeamMember owner = TeamMember.builder()
                    .id(1L)
                    .team(team)
                    .userId(1L)
                    .roles(List.of(TeamRole.OWNER))
                    .build();

            when(userContext.getUserId()).thenReturn(1L);
            when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(owner));
            when(teamMemberRepository.findById(2L)).thenReturn(Optional.of(teamMember));

            teamMemberService.removeTeamMember(10L, 2L);

            verify(teamMemberRepository).deleteById(2L);
        }

        @Test
        @DisplayName("Должен выбросить исключение, если удаляет не владелец")
        void shouldThrowExceptionIfNotOwner() {
            Project project = new Project();
            project.setId(10L);
            Team team = new Team();
            team.setProject(project);

            TeamMember notOwner = TeamMember.builder()
                    .id(1L)
                    .team(team)
                    .userId(1L)
                    .roles(List.of(TeamRole.DEVELOPER))
                    .build();

            when(userContext.getUserId()).thenReturn(1L);
            when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(notOwner));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                teamMemberService.removeTeamMember(1L, 2L);
            });

            assertEquals("Only the owner can remove members from the project", exception.getMessage());
            verify(teamMemberRepository, never()).deleteById(2L);
        }
    }
}
