package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    private ProjectRepository projectRepository;
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
                .projectIds(List.of(project.getId()))
                .build();

        Moment moment = Moment.builder()
                .name("First moment")
                .description("description")
                .date(LocalDateTime.now())
                .projects(List.of(project))
                .build();

        when(projectRepository.getProjectById(1L)).thenReturn(project);
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
                .projectIds(List.of(project.getId()))
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
        when(projectRepository.getProjectById(1L)).thenReturn(project);
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
                .projectIds(List.of(project.getId()))
                .build();

        assertThrows(
                DataValidationException.class,
                () -> momentService.update(momentId, momentDto)
        );
    }

    @Test
    public void testGetAllMoments() {
        Pageable pageable = PageRequest.of(0, 5);
        Moment moment1 = Moment.builder()
                .id(1L)
                .name("Moment 1")
                .description("description")
                .date(LocalDateTime.now())
                .build();

        Moment moment2 = Moment.builder()
                .id(2L)
                .name("Moment 2")
                .description("description")
                .date(LocalDateTime.now())
                .build();

        MomentDto momentDto1 = MomentDto.builder()
                .id(1L)
                .name("Moment 1")
                .description("description")
                .date(LocalDateTime.now())
                .build();

        MomentDto momentDto2 = MomentDto.builder()
                .id(2L)
                .name("Moment 2")
                .description("description")
                .date(LocalDateTime.now())
                .build();

        Page<Moment> momentPage = new PageImpl<>(List.of(moment1, moment2));
        Page<MomentDto> momentDtoPage = new PageImpl<>(List.of(momentDto1, momentDto2));
        when(momentRepository.findAll(pageable)).thenReturn(momentPage);
        when(momentMapper.toDto(moment1)).thenReturn(momentDto1);
        when(momentMapper.toDto(moment2)).thenReturn(momentDto2);
        Page<MomentDto> result = momentService.getAllMoments(pageable.getPageNumber(), pageable.getPageSize());
        verify(momentRepository).findAll(pageable);
        verify(momentMapper).toDto(moment1);
        verify(momentMapper).toDto(moment2);
        assertEquals(momentDtoPage, result);
    }

    @Test
    public void testGetByIdShouldReturnDataValidationException() {
        Long id = 1L;
        assertThrows(DataValidationException.class,
                () -> momentService.getById(id)
        );
    }

    @Test
    public void testGetById() {
        Long id = 1L;
        Moment moment = Moment.builder()
                .id(id)
                .name("Moment 1")
                .description("description")
                .date(LocalDateTime.now())
                .build();
        MomentDto momentDto = MomentDto.builder()
                .id(id)
                .name("Moment 1")
                .description("description")
                .date(LocalDateTime.now())
                .build();
        when(momentRepository.findById(id)).thenReturn(Optional.of(moment));
        when(momentMapper.toDto(moment)).thenReturn(momentDto);
        MomentDto result = momentService.getById(id);
        verify(momentRepository).findById(id);
        verify(momentMapper).toDto(moment);
        assertEquals(momentDto, result);
    }

}
