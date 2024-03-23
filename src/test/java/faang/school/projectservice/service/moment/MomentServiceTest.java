package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.mapper.moment.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.filters.MomentFilter;
import faang.school.projectservice.validator.moment.ValidatorMoment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {

    private MomentMapperImpl momentMapper;
    private ValidatorMoment validatorMoment;
    private MomentRepository momentRepository;
    private ProjectRepository projectRepository;
    private MomentFilter momentFilter;
    @Captor
    private ArgumentCaptor<Moment> captor;
    private MomentService momentService;

    private MomentDto momentDto;
    private Moment moment;
    private Moment moment1;
    private Moment moment2;
    private Project project;
    private Project project2;
    private Project project3;
    private Project project1;
    private MomentFilterDto filtersDto;
    Long projectId = 1L;
    Long momentId = 1L;


    @BeforeEach
    void init() {
        project = Project.builder()
                .id(1L)
                .build();
        project1 = Project.builder()
                .id(2L)
                .moments(Arrays.asList(moment, moment1))
                .build();
        project2 = Project.builder()
                .id(1L)
                .build();
        project3 = Project.builder()
                .id(2L)
                .build();
        filtersDto = MomentFilterDto.builder()
                .month(1)
                .projects(Arrays.asList(project3, project2))
                .build();
        momentDto = MomentDto.builder()
                .id(10L)
                .name("testMomentDto")
                .projectIds(Arrays.asList(1L, 2L))
                .userIds(Arrays.asList(7L, 8L))
                .build();
        moment = Moment.builder()
                .id(9L)
                .name("testMoment")
                .projects(Arrays.asList(project, project1))
                .userIds(Arrays.asList(7L, 8L))
                .build();
        moment1 = Moment.builder()
                .id(10L)
                .name("testMoment")
                .projects(Arrays.asList(project, project1))
                .userIds(Arrays.asList(7L, 8L))
                .build();
        moment2 = Moment.builder()
                .id(1L)
                .name("testMoment")
                .projects(Arrays.asList(project, project1))
                .userIds(Arrays.asList(7L, 8L))
                .build();
        project.setMoments(List.of(moment1, moment));

        Stream<Moment> momentStream = Stream.of(moment1, moment);

        List<Project> newProjects = Arrays.asList(project2, project3);
        List<Long> newProjectIds = Arrays.asList(11L, 12L, 13L);
        List<Long> oldProjectIds = Arrays.asList(111L, 2L, 1L);
        List<Long> oldUserIds = Arrays.asList(15L, 20L, 30L);
        List<Long> newUserIds = Arrays.asList(14L, 21L, 31L);

        momentRepository = Mockito.mock(MomentRepository.class);
        projectRepository = Mockito.mock(ProjectRepository.class);
        validatorMoment = Mockito.mock(ValidatorMoment.class);
        momentMapper = Mockito.spy(MomentMapperImpl.class);
        momentFilter = Mockito.mock(MomentFilter.class);
        List<MomentFilter> filtersList = List.of(momentFilter);
        momentService = new MomentService(momentRepository, projectRepository, validatorMoment, momentMapper, filtersList);
    }

    @Test
    public void testCreateMoment() {

        momentService.createMoment(momentDto);

        verify(validatorMoment).ValidatorMomentName(any(MomentDto.class));
        verify(validatorMoment).ValidatorOpenProject(any(MomentDto.class));
        verify(validatorMoment).ValidatorMomentProject(any(MomentDto.class));
        verify(momentMapper).toEntity(momentDto);
        verify(momentRepository).save(any(Moment.class));
    }


    @Test
    public void testUpdatingUsersAndProjectsAtMoments() {
        when(momentRepository.findById(momentDto.getId())).thenReturn(Optional.ofNullable(moment));
        when(projectRepository.findAllByIds(any())).thenReturn(List.of(project2, project3));
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        momentService.updateMoment(momentDto);

        verify(momentMapper, times(1)).toDto(moment);
        verify(projectRepository, times(1)).findAllByIds(any());
        verify(momentRepository, times(1)).save(captor.capture());
        Moment momentCaptor = captor.getValue();
        assertEquals(momentCaptor.getProjects().stream().map(Project::getId).toList(), momentDto.getProjectIds());
        assertEquals(momentDto.getUserIds(), momentCaptor.getUserIds());
        assertEquals(moment.getProjects().stream().map(Project::getId).toList(), momentDto.getProjectIds());
    }

    @Test
    public void TestGetAllMomentsByDateAndProject() {
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(momentFilter.isApplicable(filtersDto)).thenReturn(true);
        when(momentFilter.apply(any(Stream.class), any(MomentFilterDto.class))).thenReturn(Stream.of(moment1, moment));

        momentService.getAllMomentsByDateAndProject(projectId, filtersDto);

        verify(projectRepository, times(1)).getProjectById(projectId);
        verify(momentFilter, times(1)).isApplicable(filtersDto);
        verify(momentFilter, times(1)).apply(any(Stream.class), any(MomentFilterDto.class));
    }

    @Test
    public void TestGetAllMoments() {
        List<Moment> listMoments = Arrays.asList(moment, moment1);
        when(momentRepository.findAll()).thenReturn(listMoments);
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        momentService.getAllMoments();

        verify(momentRepository, times(1)).findAll();
        verify(momentMapper, times(1)).toDto(moment);
    }

    @Test
    public void TestGetMomentById() {
        when(momentRepository.findById(momentId)).thenReturn(Optional.of(moment));
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        momentService.getMomentById(momentId);

        verify(momentRepository, times(1)).findById(anyLong());
        verify(momentMapper, times(1)).toDto(moment);
    }
}
