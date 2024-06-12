package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.moment.filter.MomentFilter;
import faang.school.projectservice.validation.moment.MomentValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class MomentServiceTest {
    @Mock
    private MomentMapper momentMapper;
    @Mock
    private MomentValidator momentValidator;
    @Mock
    private MomentFilter momentFilter;
    @Mock
    private MomentRepository momentRepository;
    @InjectMocks
    private MomentService momentService;

    @Test
    public void testCreate() {
        MomentDto momentDto = getMomentDto();

        momentService.create(momentDto);

        verify(momentValidator).momentHasProjectValidation(any(MomentDto.class));
        verify(momentValidator).projectNotCancelledValidator(anyList());
        verify(momentRepository).save(momentMapper.toEntity(momentDto));

        assertNotNull(momentDto);
    }

    @Test
    public void testUpdate() {
        Project project = getProject();
        MomentDto momentDto = getMomentDto();
        Moment newMoment = getNewMoment(project);
        Moment oldMoment = getOldMoment(project);

        when(momentRepository.findById(oldMoment.getId())).thenReturn(Optional.of(oldMoment));
        when(momentMapper.toEntity(momentDto)).thenReturn(newMoment);
        when(momentRepository.save(newMoment)).thenReturn(newMoment);
        when(momentMapper.toDto(newMoment)).thenReturn(momentDto);

        momentService.update(oldMoment.getId(), momentDto);

        verify(momentRepository).save(momentMapper.toEntity(momentDto));
        verify(momentMapper).toDto(newMoment);

        assertEquals(momentDto, momentMapper.toDto(newMoment));
    }

    @Test
    public void testUpdateException() {
        MomentDto momentDto = getMomentDto();

        assertThrows(EntityNotFoundException.class, () -> momentService.update(-1L, momentDto));
    }


    @Test
    void testGetAllMomentsByFilters() {
        //TODO
    }

    @Test
    public void testGetAllMoments() {
        Project project = getProject();
        List<Moment> momentsList = List.of(getOldMoment(project), getNewMoment(project));

        List<MomentDto> expectedResult = momentMapper.toDtoList(momentsList);
        when(momentRepository.findAll()).thenReturn(momentsList);

        List<MomentDto> actualResult = momentService.getAllMoments();
        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testGetMomentById() {
        Project project = getProject();
        Moment moment = getOldMoment(project);
        MomentDto momentDto = momentMapper.toDto(moment);

        when(momentRepository.findById(moment.getId())).thenReturn(Optional.of(moment));

        MomentDto result = momentService.getMomentById(moment.getId());

        verify(momentRepository, times(1)).findById(moment.getId());
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
                .projects(List.of(project))
                .userIds(List.of(1L))
                .build();
    }

    private static Moment getNewMoment(Project project) {
        return Moment.builder()
                .id(2L)
                .name("Changed moment")
                .description("Changed description")
                .projects(List.of(project))
                .userIds(List.of(2L, 3L))
                .build();
    }

    private static MomentDto getMomentDto() {
        return MomentDto.builder()
                .id(1L)
                .name("testMomentDto")
                .date(LocalDateTime.now())
                .projectIds(List.of(1L))
                .userIds(List.of(1L, 2L))
                .build();
    }
}