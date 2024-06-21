package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentRestDto;
import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.mapper.MomentRestMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.moment.filter.MomentDateFilter;
import faang.school.projectservice.service.moment.filter.MomentFilter;
import faang.school.projectservice.service.moment.filter.MomentProjectFilter;
import faang.school.projectservice.validation.moment.MomentRestValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class MomentRestServiceTest {
    @Mock
    private MomentRestMapper momentMapper;
    @Mock
    private MomentRestValidator momentValidator;
    @Mock
    private MomentRepository momentRepository;
    @InjectMocks
    private MomentRestService momentService;

    @Test
    public void testCreate() {
        MomentRestDto momentDto = getMomentDto();

        momentService.create(momentDto);

        verify(momentValidator).momentHasProjectValidation(any(MomentRestDto.class));
        verify(momentValidator).projectNotCancelledValidator(anyList());
        verify(momentRepository).save(momentMapper.toEntity(momentDto));

        assertNotNull(momentDto);
    }

    @Test
    public void testUpdate() {
        Project project = getProject();
        MomentRestDto momentDto = getMomentDto();
        Moment newMoment = getNewMoment(project);
        Moment oldMoment = getOldMoment(project);

        when(momentRepository.findById(oldMoment.getId())).thenReturn(Optional.of(oldMoment));
        when(momentMapper.toEntity(momentDto)).thenReturn(newMoment);
        when(momentRepository.save(newMoment)).thenReturn(newMoment);
        when(momentMapper.toDto(newMoment)).thenReturn(momentDto);

        momentService.update(oldMoment.getId(), momentDto);

        verify(momentRepository).save(momentMapper.toEntity(momentDto));
        verify(momentMapper).toDto(newMoment);

        assertNotNull(newMoment);
        assertEquals(momentDto, momentMapper.toDto(newMoment));
    }

    @Test
    public void testUpdateException() {
        MomentRestDto momentDto = getMomentDto();

        assertThrows(EntityNotFoundException.class, () -> momentService.update(-1L, momentDto));
    }

    @Test
    public void testGetAllMomentsByDate() {
        MomentFilter momentDateFilter = new MomentDateFilter();
        List<Moment> moments = getMomentDateList();
        List<Moment> expectedList = getExpectedDateList();
        MomentFilterDto momentFilterDto = MomentFilterDto.builder()
                .startDate(LocalDateTime.of(2024, 6, 1, 0, 0))
                .endDate(LocalDateTime.now())
                .build();

        Stream<Moment> momentStream = momentDateFilter.apply(moments, momentFilterDto);

        assertEquals(expectedList, momentStream.toList());
    }

    @Test
    public void testGetAllMomentsByProjects() {
        MomentFilter momentProjectFilter = new MomentProjectFilter();
        List<Moment> moments = getMomentProjectList();
        List<Moment> expectedList = getExpectedMomentProjectList();
        MomentFilterDto momentFilterDto = MomentFilterDto.builder()
                .projects(List.of(1L, 2L))
                .build();

        Stream<Moment> momentStream = momentProjectFilter.apply(moments, momentFilterDto);
        assertEquals(expectedList, momentStream.toList());
    }

    @Test
    public void testGetAllMoments() {
        Project project = getProject();
        List<Moment> momentsList = new ArrayList<>(List.of(getOldMoment(project), getNewMoment(project)));

        List<MomentRestDto> expectedResult = momentMapper.toDtoList(momentsList);
        when(momentRepository.findAll()).thenReturn(momentsList);

        List<MomentRestDto> actualResult = momentService.getAllMoments();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testGetMomentById() {
        Project project = getProject();
        Moment moment = getOldMoment(project);
        MomentRestDto momentDto = momentMapper.toDto(moment);

        when(momentRepository.findById(moment.getId())).thenReturn(Optional.of(moment));

        MomentRestDto result = momentService.getMomentById(moment.getId());

        assertEquals(momentDto, result);
    }

    private static Project getProject() {
        return Project.builder()
                .id(1L)
                .name("Project name")
                .createdAt(LocalDateTime.now())
                .status(ProjectStatus.IN_PROGRESS)
                .visibility(ProjectVisibility.PUBLIC)
                .build();
    }

    private static Moment getOldMoment(Project project) {
        return Moment.builder()
                .id(1L)
                .name("First moment")
                .description("description")
                .projects(new ArrayList<>(List.of(project)))
                .userIds(new ArrayList<>(List.of(1L)))
                .build();
    }

    private static Moment getNewMoment(Project project) {
        return Moment.builder()
                .id(2L)
                .name("Changed moment")
                .description("Changed description")
                .projects(new ArrayList<>(List.of(project)))
                .userIds(new ArrayList<>(List.of(2L, 3L)))
                .build();
    }

    private static MomentRestDto getMomentDto() {
        return MomentRestDto.builder()
                .id(1L)
                .name("testMomentDto")
                .date(LocalDateTime.now())
                .projects(new ArrayList<>(List.of(1L)))
                .userIds(new ArrayList<>(List.of(1L, 2L)))
                .build();
    }

    private static List<Moment> getExpectedDateList() {
        return List.of(Moment.builder()
                        .id(2L)
                        .name("Moment2")
                        .date(LocalDateTime.of(2024, 6, 1, 12, 0))
                        .build(),
                Moment.builder()
                        .id(4L)
                        .name("Moment4")
                        .date(LocalDateTime.of(2024, 6, 5, 15, 30))
                        .build()
        );
    }

    private static List<Moment> getMomentDateList() {
        return List.of(Moment.builder()
                        .id(1L)
                        .name("Moment1")
                        .date(LocalDateTime.now().plusHours(1))
                        .build(),
                Moment.builder()
                        .id(2L)
                        .name("Moment2")
                        .date(LocalDateTime.of(2024, 6, 1, 12, 0))
                        .build(),
                Moment.builder()
                        .id(3L)
                        .name("Moment3")
                        .date(LocalDateTime.of(2024, 4, 1, 12, 0))
                        .build(),
                Moment.builder()
                        .id(4L)
                        .name("Moment4")
                        .date(LocalDateTime.of(2024, 6, 5, 15, 30))
                        .build()
        );
    }

    private static List<Moment> getExpectedMomentProjectList() {
        return List.of(Moment.builder()
                        .id(1L)
                        .name("Moment1")
                        .projects(List.of(
                                Project.builder().id(1L).build(),
                                Project.builder().id(2L).build()))
                        .build(),
                Moment.builder()
                        .id(3L)
                        .name("Moment3")
                        .projects(List.of(
                                Project.builder().id(1L).build(),
                                Project.builder().id(2L).build()))
                        .build()
        );
    }

    private static List<Moment> getMomentProjectList() {
        return new ArrayList<>(List.of(Moment.builder()
                        .id(1L)
                        .name("Moment1")
                        .projects(List.of(Project.builder().id(1L).build(),
                                Project.builder().id(2L).build()))
                        .build(),
                Moment.builder()
                        .id(2L)
                        .name("Moment2")
                        .projects(List.of(Project.builder().id(3L).build(),
                                Project.builder().id(2L).build()))
                        .build(),
                Moment.builder()
                        .id(3L)
                        .name("Moment3")
                        .projects(List.of(Project.builder().id(1L).build(),
                                Project.builder().id(2L).build()))
                        .build(),
                Moment.builder()
                        .id(4L)
                        .name("Moment4")
                        .projects(List.of(Project.builder().id(4L).build(),
                                Project.builder().id(5L).build()))
                        .build()
        ));
    }
}