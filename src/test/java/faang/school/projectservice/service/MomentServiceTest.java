package faang.school.projectservice.service;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.validator.MomentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MomentServiceTest {
    @Mock
    private MomentRepository momentRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TeamMemberJpaRepository teamMemberJpaRepository;
    @Mock
    private MomentMapper mapper;
    @Mock
    private MomentValidator validator;
    @InjectMocks
    private MomentService service;
    private MomentDto momentDto;
    private Moment moment;

    @BeforeEach
    void setUp() {
        momentDto = MomentDto.builder()
                .name("Name")
                .description("Description")
                .date(LocalDateTime.of(2023, 1, 1, 0, 0))
                .projectIds(new ArrayList<>(List.of(1L)))
                .teamMemberIds(new ArrayList<>(List.of(1L)))
                .imageId("imageId")
                .build();

        moment = Moment.builder()
                .id(1L)
                .name("Name")
                .description("Description")
                .date(LocalDateTime.of(2023, 1, 1, 0, 0))
                .projects(new ArrayList<>(List.of(Project.builder().id(1L).build())))
                .teamMembers(new ArrayList<>(List.of(TeamMember.builder().id(1L).build())))
                .imageId("imageId")
                .build();

        when(mapper.toEntity(momentDto)).thenReturn(moment);
        when(teamRepository.findById(Mockito.any())).thenReturn(Optional.empty());
        when(momentRepository.save(Mockito.any())).thenReturn(moment);
        when(momentRepository.findById(Mockito.any())).thenReturn(Optional.of(moment));
    }

    @Test
    void create_shouldInvokeValidatorValidateMomentProjectsMethod() {
        service.create(momentDto);
        verify(validator).validateMomentProjects(momentDto);
    }

    @Test
    void create_shouldInvokeRepositorySaveMethod() {
        service.create(momentDto);
        verify(momentRepository).save(mapper.toEntity(momentDto));
    }

    @Test
    void create_shouldInvokeMapperBothMethods() {
        service.create(momentDto);
        verify(mapper).toEntity(momentDto);
        verify(mapper).toDto(moment);
    }

    @Test
    void update_shouldInvokeValidatorValidateMomentProjectsMethod() {
        service.update(1L, momentDto);
        verify(validator).validateMomentProjects(momentDto);
    }

    @Test
    void update_shouldThrowEntityNotFoundException() {
        when(momentRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> service.update(1L, momentDto),
                "Moment with id = 1 does not exist");
    }

    @Test
    void update_shouldUpdateMomentFields() {
        momentDto.setName("New Name");
        momentDto.setDescription("New Description");
        momentDto.setDate(momentDto.getDate().plusDays(1));

        service.update(1L, momentDto);

        assertAll(() -> {
            assertEquals("New Name", moment.getName());
            assertEquals("New Description", moment.getDescription());
            assertEquals(LocalDateTime.of(2023, 1, 2, 0, 0), moment.getDate());
        });
    }

    @Test
    void update_shouldUpdateMomentProjects() {
        momentDto.setProjectIds(List.of(1L, 2L));

        Project mockProject1 = mock(Project.class);
        Project mockProject2 = mock(Project.class);
        Team mockTeam = mock(Team.class);
        TeamMember mockTeamMember = mock(TeamMember.class);
        mockProject1.setId(1L);
        mockProject2.setId(2L);
        mockTeamMember.setId(1L);

        when(mockProject1.getTeams()).thenReturn(List.of(mockTeam));
        when(mockProject2.getTeams()).thenReturn(List.of(mockTeam));
        when(mockTeam.getTeamMembers()).thenReturn(List.of(mockTeamMember));

        when(projectRepository.findAllByIds(List.of(1L,  2L)))
                .thenReturn(List.of(mockProject1, mockProject2));

        System.out.println(moment.getProjects());

        service.update(1L, momentDto);

        System.out.println(moment.getProjects());

        verify(projectRepository).findAllByIds(momentDto.getProjectIds());

        assertAll(() -> {
            assertEquals(2, moment.getProjects().size());
//            assertTrue(moment.getProjects().contains(Project.builder().id(2L).build()));
        });
    }
}