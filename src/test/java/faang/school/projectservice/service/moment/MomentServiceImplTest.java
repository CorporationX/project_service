package faang.school.projectservice.service.moment;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.moment.CreateMomentDto;
import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.UpdateMomentDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
public class MomentServiceImplTest {
    @Mock
    private MomentRepository momentRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Spy
    private MomentMapper momentMapper;
    @InjectMocks
    private MomentServiceImpl momentService;

    private static final long CURRENT_USER_ID = 1;
    private static final long MOMENT_ID_ONE = 1L;
    private static final long MOMENT_ID_TWO = 2L;
    private static final long MOMENT_ID_THREE = 3L;
    private static final List<Long> PROJECT_IDS = List.of(1L, 2L, 3L);
    private static final List<Long> MEMBER_IDS = List.of(1L, 2L, 3L);

    @Test
    void createMoment_whenProjectNotFound_shouldThrowException() {
        CreateMomentDto createMomentDto = new CreateMomentDto(
                "Test Moment",
                "Test Description",
                PROJECT_IDS,
                LocalDateTime.now()
        );

        List<Project> foundProjects = List.of(
                createProject(MOMENT_ID_ONE, "Project 1", ProjectStatus.CREATED),
                createProject(MOMENT_ID_TWO, "Project 2", ProjectStatus.IN_PROGRESS)
        );

        when(userContext.getUserId()).thenReturn(CURRENT_USER_ID);
        when(projectRepository.findAllById(createMomentDto.projectIds())).thenReturn(foundProjects);

        assertThrows(
                IllegalArgumentException.class,
                () -> momentService.createMoment(createMomentDto)
        );

        verify(momentRepository, never()).save(any());
    }

    @Test
    void createMoment_whenProjectIsCompleted_shouldThrowException() {
        CreateMomentDto createMomentDto = new CreateMomentDto(
                "Test Moment",
                "Test Description",
                PROJECT_IDS,
                LocalDateTime.now()
        );

        List<Project> foundProjects = List.of(
                createProject(MOMENT_ID_ONE, "Project 1", ProjectStatus.CREATED),
                createProject(MOMENT_ID_TWO, "Project 2", ProjectStatus.COMPLETED),
                createProject(MOMENT_ID_THREE, "Project 3", ProjectStatus.CREATED)
        );

        when(userContext.getUserId()).thenReturn(CURRENT_USER_ID);
        when(projectRepository.findAllById(createMomentDto.projectIds())).thenReturn(foundProjects);

        assertThrows(
                IllegalArgumentException.class,
                () -> momentService.createMoment(createMomentDto)
        );

        verify(momentRepository, never()).save(any());
    }

    @Test
    void createMoment_whenProjectIsCancelled_shouldThrowException() {
        CreateMomentDto createMomentDto = new CreateMomentDto(
                "Test Moment",
                "Test Description",
                PROJECT_IDS,
                LocalDateTime.now()
        );

        List<Project> foundProjects = List.of(
                createProject(MOMENT_ID_ONE, "Project 1", ProjectStatus.CREATED),
                createProject(MOMENT_ID_TWO, "Project 2", ProjectStatus.CANCELLED),
                createProject(MOMENT_ID_THREE, "Project 3", ProjectStatus.CREATED)
        );

        when(userContext.getUserId()).thenReturn(CURRENT_USER_ID);
        when(projectRepository.findAllById(createMomentDto.projectIds())).thenReturn(foundProjects);

        assertThrows(
                IllegalArgumentException.class,
                () -> momentService.createMoment(createMomentDto)
        );

        verify(momentRepository, never()).save(any());
    }

    @Test
    void createMoment_shouldSaveAndReturnDto_whenCreationIsSuccessful() {
        LocalDateTime testDate = LocalDateTime.now();

        CreateMomentDto createMomentDto = new CreateMomentDto(
                "Test Moment",
                "Test Description",
                PROJECT_IDS,
                testDate
        );

        List<Project> foundProjects = List.of(
                createProject(MOMENT_ID_ONE, "Project 1", ProjectStatus.CREATED),
                createProject(MOMENT_ID_TWO, "Project 2", ProjectStatus.IN_PROGRESS),
                createProject(MOMENT_ID_THREE, "Project 3", ProjectStatus.CREATED)
        );

        Moment moment = new Moment();
        MomentDto expectedMomentDto = new MomentDto(
                MOMENT_ID_ONE,
                "Test moment",
                "Test description",
                testDate,
                testDate,
                testDate,
                List.of(),
                List.of()
        );

        when(userContext.getUserId()).thenReturn(CURRENT_USER_ID);
        when(projectRepository.findAllById(createMomentDto.projectIds())).thenReturn(foundProjects);
        when(momentMapper.toMoment(createMomentDto)).thenReturn(moment);
        when(momentRepository.save(moment)).thenReturn(moment);
        when(momentMapper.toMomentDto(moment)).thenReturn(expectedMomentDto);

        MomentDto actualMomentDto = momentService.createMoment(createMomentDto);

        assertEquals(expectedMomentDto, actualMomentDto);
        assertEquals(foundProjects, moment.getProjects());
        assertEquals(CURRENT_USER_ID, moment.getCreatedBy());
        assertEquals(CURRENT_USER_ID, moment.getUpdatedBy());
        assertEquals(testDate, moment.getDate());

        verify(momentRepository).save(moment);
        verify(momentMapper).toMoment(createMomentDto);
        verify(momentMapper).toMomentDto(moment);
    }

    @Test
    void updateMoment_whenMomentNotFound_shouldThrowException() {
        Long momentId = MOMENT_ID_ONE;

        UpdateMomentDto updateMomentDto = new UpdateMomentDto(
                "Test Moment",
                "Test Description",
                PROJECT_IDS,
                LocalDateTime.now(),
                MEMBER_IDS
        );

        when(momentRepository.findById(momentId)).thenReturn(Optional.empty());

        assertThrows(
                EntityNotFoundException.class,
                () -> momentService.updateMoment(momentId, updateMomentDto)
        );

        verify(momentRepository, never()).save(any());
    }

    @Test
    void updateMoment_whenProjectNotFound_shouldThrowException() {
        Long momentId = MOMENT_ID_ONE;
        Moment moment = new Moment();

        UpdateMomentDto updateMomentDto = new UpdateMomentDto(
                "Test Moment",
                "Test Description",
                PROJECT_IDS,
                LocalDateTime.now(),
                MEMBER_IDS
        );

        when(momentRepository.findById(momentId)).thenReturn(Optional.of(moment));
        when(projectRepository.findAllById(PROJECT_IDS)).thenReturn(List.of(
                new Project() {{
                    setId(1L);
                }}
        ));

        assertThrows(
                IllegalArgumentException.class,
                () -> momentService.updateMoment(momentId, updateMomentDto)
        );

        verify(momentRepository, never()).save(any());
    }

    @Test
    void updateMoment_whenProjectIsCompleted_shouldThrowException() {
        Long momentId = MOMENT_ID_ONE;
        Moment moment = new Moment();

        UpdateMomentDto updateMomentDto = new UpdateMomentDto(
                "Test Moment",
                "Test Description",
                PROJECT_IDS,
                LocalDateTime.now(),
                MEMBER_IDS
        );

        List<Project> foundProjects = List.of(
                createProject(MOMENT_ID_ONE, "Project 1", ProjectStatus.CREATED),
                createProject(MOMENT_ID_TWO, "Project 2", ProjectStatus.COMPLETED),
                createProject(MOMENT_ID_THREE, "Project 3", ProjectStatus.CREATED)
        );

        when(momentRepository.findById(momentId)).thenReturn(Optional.of(moment));
        when(projectRepository.findAllById(PROJECT_IDS)).thenReturn(foundProjects);

        assertThrows(
                IllegalArgumentException.class,
                () -> momentService.updateMoment(momentId, updateMomentDto)
        );

        verify(momentRepository, never()).save(any());
    }

    @Test
    void updateMoment_whenProjectIsCancelled_shouldThrowException() {
        Long momentId = MOMENT_ID_ONE;
        Moment moment = new Moment();

        UpdateMomentDto updateMomentDto = new UpdateMomentDto(
                "Test Moment",
                "Test Description",
                PROJECT_IDS,
                LocalDateTime.now(),
                MEMBER_IDS
        );

        List<Project> foundProjects = List.of(
                createProject(MOMENT_ID_ONE, "Project 1", ProjectStatus.CREATED),
                createProject(MOMENT_ID_TWO, "Project 2", ProjectStatus.CANCELLED),
                createProject(MOMENT_ID_THREE, "Project 3", ProjectStatus.CREATED)
        );

        when(momentRepository.findById(momentId)).thenReturn(Optional.of(moment));
        when(projectRepository.findAllById(PROJECT_IDS)).thenReturn(foundProjects);

        assertThrows(
                IllegalArgumentException.class,
                () -> momentService.updateMoment(momentId, updateMomentDto)
        );

        verify(momentRepository, never()).save(any());
    }

    @Test
    void updateMoment_whenMemberIdsNull_shouldUpdateMomentAndProjectsOnly() {
        Long momentId = MOMENT_ID_ONE;
        Moment moment = new Moment();
        moment.setProjects(List.of());
        moment.setUserIds(List.of());

        UpdateMomentDto updateMomentDto = new UpdateMomentDto(
                "Test Moment",
                "Test Description",
                PROJECT_IDS,
                LocalDateTime.now(),
                null
        );

        List<Project> foundProjects = List.of(
                createProject(MOMENT_ID_ONE, "Project 1", ProjectStatus.CREATED),
                createProject(MOMENT_ID_TWO, "Project 2", ProjectStatus.CREATED),
                createProject(MOMENT_ID_THREE, "Project 3", ProjectStatus.CREATED)
        );

        when(momentRepository.findById(momentId)).thenReturn(Optional.of(moment));
        when(projectRepository.findAllById(PROJECT_IDS)).thenReturn(foundProjects);
        when(userContext.getUserId()).thenReturn(CURRENT_USER_ID);

        momentService.updateMoment(momentId, updateMomentDto);

        verify(momentRepository).save(moment);
        assertEquals(3, moment.getProjects().size());
        assertEquals(0, moment.getUserIds().size());
    }

    @Test
    void updateMoment_shouldUpdateMomentProjectsAndMembersSuccessfully() {
        Long momentId = MOMENT_ID_ONE;

        Moment moment = new Moment();
        moment.setProjects(List.of());
        moment.setUserIds(List.of(CURRENT_USER_ID));

        UpdateMomentDto updateMomentDto = new UpdateMomentDto(
                "Updated Moment",
                "Updated Description",
                PROJECT_IDS,
                LocalDateTime.now(),
                MEMBER_IDS
        );

        List<Project> foundProjects = List.of(
                createProject(MOMENT_ID_ONE, "Project 1", ProjectStatus.CREATED),
                createProject(MOMENT_ID_TWO, "Project 2", ProjectStatus.CREATED),
                createProject(MOMENT_ID_THREE, "Project 3", ProjectStatus.CREATED)
        );

        when(momentRepository.findById(momentId)).thenReturn(Optional.of(moment));
        when(projectRepository.findAllById(PROJECT_IDS)).thenReturn(foundProjects);
        when(teamMemberRepository.findAllById(MEMBER_IDS))
                .thenReturn(MEMBER_IDS.stream().map(id -> {
                    TeamMember tm = new TeamMember();
                    tm.setId(id);
                    return tm;
                }).toList());
        when(userContext.getUserId()).thenReturn(CURRENT_USER_ID);

        momentService.updateMoment(momentId, updateMomentDto);

        verify(momentRepository).save(moment);
        assertEquals(3, moment.getProjects().size());
        assertTrue(moment.getUserIds().containsAll(MEMBER_IDS));
        assertEquals("Updated Moment", moment.getName());
        assertEquals("Updated Description", moment.getDescription());
        assertEquals(CURRENT_USER_ID, moment.getUpdatedBy());
    }

    @Test
    void getAllMoments_pageableProvided_shouldReturnMappedPage() {
        Moment moment1 = new Moment();
        moment1.setId(MOMENT_ID_ONE);
        moment1.setDate(LocalDateTime.now());
        moment1.setName("Test Moment 1");
        moment1.setDescription("Test description 1");
        moment1.setDate(LocalDateTime.now());
        moment1.setCreatedAt(LocalDateTime.now());
        moment1.setUpdatedAt(LocalDateTime.now());
        moment1.setProjects(List.of());

        Moment moment2 = new Moment();
        moment2.setId(MOMENT_ID_TWO);
        moment2.setDate(LocalDateTime.now());
        moment2.setName("Test Moment 2");
        moment2.setDescription("Test description 2");
        moment2.setDate(LocalDateTime.now());

        Page<Moment> momentPage = new PageImpl<>(List.of(moment1, moment2));

        when(momentRepository.findAll(PageRequest.of(0, 10))).thenReturn(momentPage);
        when(momentMapper.toMomentDto(moment1)).thenReturn(new MomentDto(
                moment1.getId(),
                moment1.getName(),
                moment1.getDescription(),
                moment1.getDate(),
                moment1.getCreatedAt(),
                moment1.getUpdatedAt(),
                List.of(),
                List.of()
        ));
        when(momentMapper.toMomentDto(moment2)).thenReturn(new MomentDto(
                moment2.getId(),
                moment2.getName(),
                moment2.getDescription(),
                moment2.getDate(),
                moment2.getCreatedAt(),
                moment2.getUpdatedAt(),
                List.of(),
                List.of()
        ));

        Page<MomentDto> result =
                momentService.getAllMoments(PageRequest.of(0, 10));

        assertEquals(2, result.getContent().size());
        assertEquals("Test Moment 1", result.getContent().get(0).name());
        assertEquals("Test Moment 2", result.getContent().get(1).name());
    }

    @Test
    void getMomentById_whenMomentExists_shouldReturnMappedDto() {
        Long momentId = MOMENT_ID_ONE;
        LocalDateTime now = LocalDateTime.now();

        Project project1 = createProject(MOMENT_ID_ONE, "Project 1", ProjectStatus.CREATED);
        Project project2 = createProject(MOMENT_ID_TWO, "Project 2", ProjectStatus.IN_PROGRESS);

        Moment moment = new Moment();
        moment.setId(momentId);
        moment.setName("Moment Test");
        moment.setDescription("Description Test");
        moment.setDate(now);
        moment.setCreatedAt(now);
        moment.setUpdatedAt(now);
        moment.setProjects(List.of(project1, project2));
        moment.setUserIds(List.of(1L, 2L));

        MomentDto expectedDto = new MomentDto(
                momentId,
                "Moment Test",
                "Description Test",
                now,
                now,
                now,
                List.of(1L, 2L),
                List.of(1L, 2L)
        );

        when(momentRepository.findById(momentId)).thenReturn(Optional.of(moment));
        when(momentMapper.toMomentDto(moment)).thenReturn(expectedDto);

        MomentDto actualDto = momentService.getMomentById(momentId);

        verify(momentRepository).findById(momentId);
        verify(momentMapper).toMomentDto(moment);

        assertEquals(expectedDto, actualDto);
        assertEquals(2, actualDto.projectIds().size());
        assertEquals(2, actualDto.memberIds().size());
        assertTrue(actualDto.projectIds().containsAll(List.of(1L, 2L)));
    }

    @Test
    void getMomentById_whenMomentNotFound_shouldThrowException() {
        Long momentId = MOMENT_ID_ONE;
        when(momentRepository.findById(momentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> momentService.getMomentById(momentId));
        verify(momentRepository).findById(momentId);
        verify(momentMapper, never()).toMomentDto(any());
    }

    @Test
    void getMomentsByProject_whenProjectNotFound_shouldThrowException() {
        when(projectRepository.findById(MOMENT_ID_ONE)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> momentService.getMomentsByProject(
                        MOMENT_ID_ONE,
                        null,
                        null,
                        PageRequest.of(0, 10)));
    }

    @Test
    void getMomentsByProject_whenMonthInvalid_shouldThrowException() {
        Project project = createProject(MOMENT_ID_ONE, "Project 1", ProjectStatus.CREATED);
        when(projectRepository.findById(MOMENT_ID_ONE)).thenReturn(Optional.of(project));

        assertThrows(IllegalArgumentException.class,
                () -> momentService.getMomentsByProject(
                        MOMENT_ID_ONE,
                        13,
                        null,
                        PageRequest.of(0, 10)));
        assertThrows(IllegalArgumentException.class,
                () -> momentService.getMomentsByProject(
                        MOMENT_ID_ONE,
                        0,
                        null,
                        PageRequest.of(0, 10)));
    }

    @Test
    void getMomentsByProject_whenPartnerNotFound_shouldThrowException() {
        Project project = createProject(MOMENT_ID_ONE, "Project 1", ProjectStatus.CREATED);
        when(projectRepository.findById(MOMENT_ID_ONE)).thenReturn(Optional.of(project));
        when(projectRepository.findAllById(List.of(2L, 3L)))
                .thenReturn(List.of(createProject(2L, "P2", ProjectStatus.CREATED)));

        assertThrows(IllegalArgumentException.class,
                () -> momentService.getMomentsByProject(
                        MOMENT_ID_ONE,
                        null,
                        List.of(2L, 3L),
                        PageRequest.of(0, 10)));
    }

    @Test
    void getMomentsByProject_shouldFilterByMonth() {
        Project project = createProject(MOMENT_ID_ONE, "Project 1", ProjectStatus.CREATED);
        when(projectRepository.findById(MOMENT_ID_ONE)).thenReturn(Optional.of(project));

        Moment m1 = new Moment();
        m1.setId(1L);
        m1.setDate(LocalDateTime.of(2025, 5, 10, 0, 0));
        Moment m2 = new Moment();
        m2.setId(2L);
        m2.setDate(LocalDateTime.of(2025, 6, 10, 0, 0));

        when(momentRepository.findAllByProjectId(MOMENT_ID_ONE)).thenReturn(List.of(m1, m2));
        when(momentMapper.toMomentDto(any())).thenAnswer(inv -> {
            Moment mm = inv.getArgument(0);
            return new MomentDto(mm.getId(),
                    null,
                    null,
                    mm.getDate(),
                    null,
                    null,
                    List.of(),
                    List.of());
        });

        Page<MomentDto> result = momentService.getMomentsByProject(
                MOMENT_ID_ONE,
                5,
                null,
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).id());
    }

    @Test
    void getMomentsByProject_shouldFilterByPartnerProjects() {
        Project project = createProject(MOMENT_ID_ONE, "Project 1", ProjectStatus.CREATED);
        Project partner = createProject(2L, "Partner", ProjectStatus.CREATED);
        when(projectRepository.findById(MOMENT_ID_ONE)).thenReturn(Optional.of(project));
        when(projectRepository.findAllById(List.of(2L))).thenReturn(List.of(partner));

        Moment m1 = new Moment();
        m1.setId(1L);
        m1.setProjects(List.of(partner));
        Moment m2 = new Moment();
        m2.setId(2L);
        m2.setProjects(List.of(project));

        when(momentRepository.findAllByProjectId(MOMENT_ID_ONE)).thenReturn(List.of(m1, m2));
        when(momentMapper.toMomentDto(any())).thenAnswer(inv -> {
            Moment mm = inv.getArgument(0);
            return new MomentDto(
                    mm.getId(),
                    null,
                    null,
                    mm.getDate(),
                    null,
                    null,
                    List.of(),
                    List.of());
        });

        Page<MomentDto> result = momentService.getMomentsByProject(
                MOMENT_ID_ONE,
                null,
                List.of(2L),
                PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).id());
    }

    @Test
    void getMomentsByProject_shouldReturnAll_whenNoFilters() {
        Project project = createProject(MOMENT_ID_ONE, "Project 1", ProjectStatus.CREATED);
        when(projectRepository.findById(MOMENT_ID_ONE)).thenReturn(Optional.of(project));

        Moment m1 = new Moment();
        m1.setId(1L);
        Moment m2 = new Moment();
        m2.setId(2L);

        when(momentRepository.findAllByProjectId(MOMENT_ID_ONE)).thenReturn(List.of(m1, m2));
        when(momentMapper.toMomentDto(any())).thenAnswer(inv -> {
            Moment mm = inv.getArgument(0);
            return new MomentDto(
                    mm.getId(),
                    null,
                    null,
                    mm.getDate(),
                    null,
                    null,
                    List.of(),
                    List.of());
        });

        Page<MomentDto> result = momentService.getMomentsByProject(
                MOMENT_ID_ONE,
                null,
                null,
                PageRequest.of(0, 10)
        );

        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().stream().map(MomentDto::id).toList().containsAll(List.of(1L, 2L)));
    }

    private Project createProject(Long id, String name, ProjectStatus status) {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        project.setStatus(status);

        return project;
    }
}
