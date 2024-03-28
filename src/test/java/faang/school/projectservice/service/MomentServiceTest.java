package faang.school.projectservice.service;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.MomentDateFilter;
import faang.school.projectservice.filter.MomentFilter;
import faang.school.projectservice.filter.MomentProjectIdsFilter;
import faang.school.projectservice.mapper.MomentMapperImpl;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.MomentFilterValidator;
import faang.school.projectservice.validator.MomentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {
    @InjectMocks
    private MomentService momentService;

    @Mock
    private MomentRepository momentRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MomentValidator momentValidator;

    @Mock
    private MomentFilterValidator momentFilterValidator;

    @Spy
    private MomentMapperImpl momentMapper;

    private final List<MomentFilter> momentFilters = new ArrayList<>();

    @Captor
    private ArgumentCaptor<Moment> momentArgumentCaptor;

    @BeforeEach
    void setUp() {
        momentFilters.add(new MomentDateFilter());
        momentFilters.add(new MomentProjectIdsFilter());
        momentService = new MomentService(momentRepository, projectRepository, momentMapper, momentValidator,
                momentFilterValidator, momentFilters);
    }

    @Test
    public void testCreate() {
        String name = "Name";
        String description = "Description";
        LocalDateTime date = LocalDateTime.now();
        List<Long> projectIds = List.of(1L, 2L, 3L);
        List<Long> userIds = List.of(1L, 2L, 3L);
        MomentDto momentDto = MomentDto.builder()
                .name(name)
                .description(description)
                .date(date)
                .projectIds(projectIds)
                .userIds(userIds)
                .build();
        for (Long projectId : projectIds) {
            when(projectRepository.getProjectById(projectId)).thenReturn(Project.builder().id(projectId).build());
        }
        momentService.create(momentDto);
        verify(momentRepository, times(1)).save(momentArgumentCaptor.capture());
        Moment capturedMoment = momentArgumentCaptor.getValue();
        assertEquals(momentDto.getName(), capturedMoment.getName());
        assertEquals(momentDto.getDescription(), capturedMoment.getDescription());
        assertEquals(momentDto.getDate(), capturedMoment.getDate());
        assertEquals(momentDto.getProjectIds(), capturedMoment.getProjects().stream().map(Project::getId).toList());
        assertEquals(momentDto.getUserIds(), capturedMoment.getUserIds());
    }

    @Test
    public void testGetByInvalidId() {
        long momentId = 1L;
        Optional<Moment> optionalMoment = Optional.empty();
        when(momentRepository.findById(momentId)).thenReturn(optionalMoment);
        assertThrows(DataValidationException.class, () -> momentService.get(momentId));
    }

    @Test
    public void testGetByValidId() {
        long momentId = 1L;
        Optional<Moment> optionalMoment = Optional.of(new Moment());
        when(momentRepository.findById(momentId)).thenReturn(optionalMoment);
        assertDoesNotThrow(() -> momentService.get(momentId));
    }

    @Test
    public void testGetAll(){
        momentService.getAll();
        verify(momentRepository, times(1)).findAll();
    }

    @Test
    public void testDateFilter() {
        LocalDateTime goodDate = LocalDateTime.now();
        LocalDateTime badDate = goodDate.plusDays(2);
        LocalDateTime startDate = goodDate.minusDays(1);
        LocalDateTime endDate = goodDate.plusDays(1);
        MomentFilterDto momentFilterDto = MomentFilterDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
        Moment goodMoment = Moment.builder().date(goodDate).build();
        Moment badMoment = Moment.builder().date(badDate).build();
        when(momentRepository.findAll()).thenReturn(List.of(goodMoment, badMoment));
        List<MomentDto> resultList = momentService.filter(momentFilterDto);
        assertEquals(resultList.get(0).getDate(), goodDate);
        assertEquals(1, resultList.size());
    }

    @Test
    public void testProjectIdsFilter() {
        List<Long> goodIds = List.of(1L, 2L, 3L);
        List<Long> badIds = List.of(2L, 3L, 4L);
        List<Long> filterIds = List.of(1L, 2L);
        MomentFilterDto momentFilterDto = MomentFilterDto.builder()
                .projectIds(filterIds)
                .build();
        Moment goodMoment = Moment.builder()
                .projects(goodIds.stream()
                        .map(id -> Project.builder()
                                .id(id)
                                .build())
                        .toList())
                .build();
        Moment badMoment = Moment.builder()
                .projects(badIds.stream()
                        .map(id -> Project.builder()
                                .id(id)
                                .build())
                        .toList())
                .build();
        when(momentRepository.findAll()).thenReturn(List.of(goodMoment, badMoment));
        List<MomentDto> resultList = momentService.filter(momentFilterDto);
        assertEquals(resultList.get(0).getProjectIds(), goodIds);
        assertEquals(resultList.size(), 1);
    }

    @Test
    public void testUpdate(){
        String name = "Name";
        String description = "Description";
        LocalDateTime date = LocalDateTime.now();
        List<Long> projectIds = List.of(1L, 2L, 3L);
        List<Long> userIds = List.of(1L, 2L, 3L);
        MomentDto momentDto = MomentDto.builder()
                .name(name)
                .description(description)
                .date(date)
                .projectIds(projectIds)
                .userIds(userIds)
                .build();
        for (Long projectId : projectIds) {
            when(projectRepository.getProjectById(projectId)).thenReturn(Project.builder().id(projectId).build());
        }
        momentService.create(momentDto);
        verify(momentRepository, times(1)).save(momentArgumentCaptor.capture());
        Moment capturedMoment = momentArgumentCaptor.getValue();
        assertEquals(momentDto.getName(), capturedMoment.getName());
        assertEquals(momentDto.getDescription(), capturedMoment.getDescription());
        assertEquals(momentDto.getDate(), capturedMoment.getDate());
        assertEquals(momentDto.getProjectIds(), capturedMoment.getProjects().stream().map(Project::getId).toList());
        assertEquals(momentDto.getUserIds(), capturedMoment.getUserIds());
    }

}
