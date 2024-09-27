package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.validator.moment.MomentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MomentServiceImplTest {
    @InjectMocks
    private MomentServiceImpl momentService;

    @Mock
    private MomentValidator momentValidator;

    @Mock
    private MomentRepository momentRepository;

    @Mock
    private MomentMapper momentMapper;

    @Mock
    private List<MomentFilter> momentFilters;

    private Moment moment;
    private MomentDto momentDto;
    private MomentFilterDto filterDto;
    private Project project;
    private Team team;
    private TeamMember teamMember;
    private long id;

    @BeforeEach
    void setUp() {
        moment = new Moment();
        moment.setId(1L);

        momentDto = MomentDto.builder()
                .name("boba")
                .Id(1L)
                .build();

        filterDto = MomentFilterDto.builder().build();
        teamMember = TeamMember.builder().id(1L).userId(1L).build();
        team = Team.builder().teamMembers(List.of(teamMember)).build();
        project = Project.builder()
                .id(1L)
                .status(ProjectStatus.IN_PROGRESS)
                .teams(List.of(team))
                .moments(new ArrayList<>())
                .build();
        id = 1L;
    }

    @Test
    @DisplayName("Проверка создания момента и добавление полей к моменту")
    void giveValidWhenCreateMomentThenReturnMomentDto() {
        moment.setProjects(Collections.singletonList(project));

        when(momentMapper.toMoment(momentDto)).thenReturn(moment);
        when(momentMapper.toMomentDto(moment)).thenReturn(momentDto);
        when(momentRepository.save(any(Moment.class))).thenReturn(moment);

        when(momentValidator.validateProjectsByIdAndStatus(momentDto))
                .thenReturn(List.of(project));


        var result = momentService.createMoment(momentDto);

        assertEquals(momentDto.name(), result.name());
        Mockito.verify(momentRepository).save(any(Moment.class));
    }

    @Test
    @DisplayName("Проверка обновления момента по проекту и добавление полей к моменту")
    void updateMomentByProjects() {
        when(momentMapper.toMomentDto(moment)).thenReturn(momentDto);
        when(momentRepository.save(any(Moment.class))).thenReturn(moment);

        List<Project> projects = Collections.singletonList(project);

        when(momentValidator.validateProjectsByIdAndStatus(momentDto)).thenReturn(projects);
        when(momentValidator.validateExistingMoment(momentDto.Id())).thenReturn(moment);

        var result = momentService.updateMomentByProjects(momentDto);

        assertEquals(momentDto.Id(), result.Id());
        Mockito.verify(momentRepository).save(any(Moment.class));
    }

    @Test
    @DisplayName("Проверка обновления момента по user ID и добавление полей к моменту")
    void updateMomentByUser() {
        momentDto = MomentDto.builder()
                .name("boba")
                .Id(1L)
                .projectIds(List.of(1L))
                .build();

        when(momentMapper.toMomentDto(moment)).thenReturn(momentDto);
        when(momentRepository.save(any(Moment.class))).thenReturn(moment);

        List<Project> projects = Collections.singletonList(project);

        when(momentValidator.validateProjectsByUserIdAndStatus(id)).thenReturn(projects);
        when(momentValidator.validateExistingMoment(momentDto.Id())).thenReturn(moment);

        var result = momentService.updateMomentByUser(id, momentDto);

        assertEquals(momentDto.projectIds().size(), result.projectIds().size());
        Mockito.verify(momentRepository).save(any(Moment.class));

    }

    @Test
    @DisplayName("Проверка с переданным фильтром")
    void givenValidWhenGetMomentsByFiltersThenReturnListMomentDto() {
        long projectId = 1L;
        MomentFilter filter = mock(MomentFilter.class);

        when(momentRepository.findAllByProjectId(projectId)).thenReturn(List.of(moment));
        when(momentMapper.toMomentDto(moment)).thenReturn(momentDto);
        when(momentFilters.stream()).thenReturn(Stream.of(filter));
        when(filter.isApplicable(filterDto)).thenReturn(true);
        when(filter.apply(any(Stream.class), eq(filterDto))).thenReturn(Stream.of(moment));

        List<MomentDto> result = momentService.getMomentsByFilters(projectId, filterDto);

        assertEquals(List.of(momentDto), result);
    }

    @Test
    @DisplayName("Проверка с пустым фильтром")
    void givenNoApplicableFiltersWhenGetMomentsByFiltersThenReturnEmptyListDto() {
        long projectId = 1L;
        MomentFilter filter = mock(MomentFilter.class);

        when(momentRepository.findAllByProjectId(projectId)).thenReturn(List.of(moment));
        when(momentMapper.toMomentDto(moment)).thenReturn(momentDto);
        when(momentFilters.stream()).thenReturn(Stream.of(filter));
        when(filter.isApplicable(filterDto)).thenReturn(false);

        List<MomentDto> result = momentService.getMomentsByFilters(projectId, filterDto);

        assertEquals(List.of(momentDto), result);
    }

    @Test
    @DisplayName("Проверка с пустым листом moment")
    void givenEmptyListMomentWhenGetMomentsByFiltersThenReturnEmptyListDto() {
        long projectId = 1L;

        when(momentRepository.findAllByProjectId(projectId)).thenReturn(List.of(moment));
        when(momentMapper.toMomentDto(moment)).thenReturn(momentDto);
        when(momentFilters.stream()).thenReturn(Stream.of());

        List<MomentDto> result = momentService.getMomentsByFilters(projectId, filterDto);

        assertEquals(List.of(momentDto), result);
    }

    @Test
    @DisplayName("Проверка на получения всех существующих моментов")
    void givenValidWhenGetAllMomentsThenReturnListMomentDto() {
        MomentDto momentDto = MomentDto.builder().build();

        when(momentRepository.findAll()).thenReturn(Collections.singletonList(moment));
        when(momentMapper.toMomentDto(Collections.singletonList(moment)))
                .thenReturn(Collections.singletonList(momentDto));

        var result = momentService.getAllMoments();

        assertEquals(1, result.size());
        assertEquals(momentDto, result.get(0));
    }

    @Test
    @DisplayName("Проверка на получения всех не существующих моментов")
    void givenNotValidWhenGetAllMomentsThenReturnEmptyList() {
        when(momentRepository.findAll()).thenReturn(Collections.emptyList());
        when(momentMapper.toMomentDto(Collections.emptyList()))
                .thenReturn(Collections.emptyList());

        var result = momentService.getAllMoments();

        assertEquals(0, result.size());
        assertEquals(Collections.emptyList(), result);
    }

    @Test
    @DisplayName("Проверка на получения существующего момента по его ID")
    void getMomentById() {
        when(momentValidator.validateExistingMoment(id)).thenReturn(moment);
        when(momentMapper.toMomentDto(moment)).thenReturn(momentDto);

        var result = momentService.getMomentById(id);

        assertEquals(momentDto.Id(), result.Id());
    }
}