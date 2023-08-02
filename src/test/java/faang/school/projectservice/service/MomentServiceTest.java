package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.MomentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {
    @Mock
    private MomentRepository momentRepository;
    @Mock
    private MomentMapper momentMapper;
    @InjectMocks
    private MomentService momentService;

    @Test
    public void testCreateMomentCompletedForSubProject() {
        TeamMember teamMember2 = TeamMember.builder()
                .userId(2L)
                .build();
        TeamMember teamMember1 = TeamMember.builder()
                .userId(1L)
                .build();
        Team team2 = Team.builder()
                .teamMembers(List.of(teamMember2))
                .build();
        Team team1 = Team.builder()
                .teamMembers(List.of(teamMember1))
                .build();
        Project subSubProject = Project.builder()
                .teams(List.of(team2))
                .build();
        Project subProject = Project.builder()
                .children(List.of(subSubProject))
                .teams(List.of(team1))
                .name("Subproject")
                .build();
        Moment moment = Moment.builder()
                .name("Subproject")
                .description("All subprojects completed")
                .userIds(List.of(1L, 2L))
                .build();

        when(momentRepository.save(moment)).thenReturn(moment);

        Moment createdMoment = momentService.createMomentCompletedForSubProject(subProject);

        assertNotNull(createdMoment);
        assertEquals(moment, createdMoment);
        assertEquals(List.of(1L, 2L), createdMoment.getUserIds());
        verify(momentRepository, times(1)).save(moment);
    }

    @Test
    public void testCreateShouldReturnDataValidationException() {
        Project project = Project.builder()
                .id(1L)
                .name("Project 1")
                .status(ProjectStatus.COMPLETED)
                .build();

        MomentDto momentDto = MomentDto.builder()
                .name("First moment")
                .description("description")
                .date(LocalDateTime.now())
                .projects(List.of(project))
                .build();

        Moment moment = Moment.builder()
                .name("First moment")
                .description("description")
                .date(LocalDateTime.now())
                .projects(List.of(project))
                .build();

        when(momentMapper.toEntity(momentDto)).thenReturn(moment);
        assertThrows(
                DataValidationException.class,
                () -> momentService.create(momentDto)
        );
    }

    @Test
    public void testCreate() {
        Project project = Project.builder()
                .id(1L)
                .name("Project 1")
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        MomentDto momentDto = MomentDto.builder()
                .name("First moment")
                .description("description")
                .date(LocalDateTime.now())
                .projects(List.of(project))
                .build();

        Moment moment = Moment.builder()
                .name("First moment")
                .description("description")
                .date(LocalDateTime.now())
                .projects(List.of(project))
                .build();

        when(momentMapper.toEntity(momentDto)).thenReturn(moment);
        moment.setId(1L);
        when(momentRepository.save(moment)).thenReturn(moment);
        momentDto.setId(1L);
        when(momentMapper.toDto(moment)).thenReturn(momentDto);
        MomentDto createdMoment = momentService.create(momentDto);
        verify(momentRepository).save(moment);
        assertEquals(momentDto, createdMoment);
    }

    @Test
    public void testUpdateShouldReturnDataValidationException() {
        Long momentId = 1L;
        Project project = Project.builder()
                .id(momentId)
                .name("Project 1")
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        MomentDto momentDto = MomentDto.builder()
                .id(momentId)
                .name("First moment")
                .description("description")
                .date(LocalDateTime.now())
                .projects(List.of(project))
                .build();

        assertThrows(
                DataValidationException.class,
                () -> momentService.update(momentId, momentDto)
        );
    }
}
