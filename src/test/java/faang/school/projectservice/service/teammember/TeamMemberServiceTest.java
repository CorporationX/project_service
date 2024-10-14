package faang.school.projectservice.service.teammember;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.teammember.AddTeamMemberDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.mapper.teammember.TeamMemberMapper;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
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

    @InjectMocks
    private TeamMemberService teamMemberService;


    private TeamMember teamMember;
    private final Long teamMemberId = 3L;
    private TeamMemberDto teamMemberDto;
    private AddTeamMemberDto addTeamMemberDto;
    private long userIdWithPermission = 1L;
    @BeforeEach
    public void setup() {
        teamMember = TeamMember.builder()
                .id(teamMemberId)
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

        addTeamMemberDto = AddTeamMemberDto.builder()
                .userId(2L)
                .roles(List.of(TeamRole.DEVELOPER))
                .nickname("NewMember")
                .build();
    }

    @Nested
    @DisplayName("Добавление участника в команду")
    class AddTeamMemberTests {

        @Test
        @DisplayName("Должен успешно добавить участника")
        void shouldAddTeamMember() {
            TeamMember owner = TeamMember.builder()
                    .userId(1L)
                    .roles(List.of(TeamRole.OWNER))
                    .build();

            when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(owner));
            when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(teamMember);
            when(teamMemberMapper.toDto(any(TeamMember.class))).thenReturn(teamMemberDto);
            when(userContext.getUserId()).thenReturn(userIdWithPermission);
            TeamMemberDto result = teamMemberService.addTeamMember(addTeamMemberDto);

            assertEquals("UpdatedNickname", result.getNickname());
            verify(teamMemberRepository).save(any(TeamMember.class));
        }

        @Test
        @DisplayName("Должен выбросить исключение, если пользователь не владелец или TEAMLEAD")
        void shouldThrowExceptionIfNotOwnerOrTeamLead() {
            TeamMember userWithoutAccess = TeamMember.builder()
                    .userId(1L)
                    .roles(List.of(TeamRole.DEVELOPER))
                    .build();

            when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(userWithoutAccess));
            when(userContext.getUserId()).thenReturn(userIdWithPermission);
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
            when(teamMemberRepository.findById(2L)).thenReturn(Optional.of(teamMember));
            when(teamMemberRepository.save(any(TeamMember.class))).thenReturn(teamMember);
            when(teamMemberMapper.toDto(any(TeamMember.class))).thenReturn(teamMemberDto);
            when(userContext.getUserId()).thenReturn(2L);

            TeamMemberDto result = teamMemberService.updateTeamMember(teamMemberDto);

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

            TeamMember memberToUpdate = TeamMember.builder()
                    .id(2L)
                    .userId(2L)
                    .roles(List.of(TeamRole.DEVELOPER))
                    .build();

            when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(updater));
            when(teamMemberRepository.findById(2L)).thenReturn(Optional.of(memberToUpdate));
            when(userContext.getUserId()).thenReturn(1L);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                teamMemberService.updateTeamMember(teamMemberDto);
            });

            assertEquals("Only TEAMLEAD can update roles and permissions", exception.getMessage());
            verify(teamMemberRepository, never()).save(any(TeamMember.class));
        }
    }

    @Nested
    @DisplayName("Удаление участника")
    class RemoveTeamMemberTests {

        @Test
        @DisplayName("Должен удалить участника, если удаляет владелец")
        void shouldRemoveTeamMember() {
            TeamMember owner = TeamMember.builder()
                    .id(1L)
                    .userId(1L)
                    .roles(List.of(TeamRole.OWNER))
                    .build();

            TeamMember memberToDelete = TeamMember.builder()
                .id(2L)
                .userId(2L)
                .build();

            when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(owner));
            when(teamMemberRepository.findById(2L)).thenReturn(Optional.of(memberToDelete));

            teamMemberService.removeTeamMember(1L, 2L);

            verify(teamMemberRepository).deleteById(2L);
        }

        @Test
        @DisplayName("Должен выбросить исключение, если удаляет не владелец")
        void shouldThrowExceptionIfNotOwner() {
            TeamMember notOwner = TeamMember.builder()
                    .userId(1L)
                    .roles(List.of(TeamRole.DEVELOPER))
                    .build();

            when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(notOwner));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                teamMemberService.removeTeamMember(1L, 2L);
            });

            assertEquals("Only the owner can remove members from the project", exception.getMessage());
            verify(teamMemberRepository, never()).deleteById(2L);
        }
    }

    @Nested
    @DisplayName("Получение участника по ID")
    class GetTeamMemberByIdTests {

        @Test
        @DisplayName("Должен вернуть участника по его ID")
        void shouldReturnTeamMemberById() {
            when(teamMemberRepository.findById(2L)).thenReturn(Optional.of(teamMember));
            when(teamMemberMapper.toDto(any(TeamMember.class))).thenReturn(teamMemberDto);

            TeamMemberDto result = teamMemberService.getTeamMemberById(2L);

            assertEquals(2L, result.getUserId());
            verify(teamMemberRepository).findById(2L);
        }

        @Test
        @DisplayName("Должен выбросить исключение, если участник не найден")
        void shouldThrowExceptionIfTeamMemberNotFound() {
            when(teamMemberRepository.findById(2L)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
                teamMemberService.getTeamMemberById(2L);
            });

            assertEquals("Team member with id 2 does not exist!", exception.getMessage());
        }
    }
}
