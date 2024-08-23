package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.exception.DataValidationExceptions;
import faang.school.projectservice.exception.NotFoundEntityException;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.service.moment.filter.MomentFilter;
import faang.school.projectservice.service.moment.filter.impl.MomentDateFilter;
import faang.school.projectservice.service.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MomentServiceTest {

    @Mock
    private MomentRepository momentRepository;

    @Mock
    private MomentMapper momentMapper;

    @Mock
    private ProjectService projectService;

    @Mock
    private MomentDateFilter filter;
    private List<MomentFilter> momentFilters;

    @InjectMocks
    private MomentService momentService;

    private Moment moment;
    private MomentDto momentDto;
    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        moment = new Moment();
        moment.setId(1L);
        moment.setDate(LocalDateTime.now());

        momentDto = new MomentDto();
        momentDto.setId(1L);
        momentDto.setDate(LocalDateTime.now());

        projectDto = new ProjectDto();
        projectDto.setId(1L);
        projectDto.setStatus(ProjectStatus.IN_PROGRESS);

        momentFilters = List.of(filter);
        momentService = new MomentService(momentRepository,
                momentMapper, momentFilters, projectService);
    }

    @Test
    void testGetMomentById_Success() {
        when(momentRepository.findById(1L)).thenReturn(Optional.of(moment));
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        MomentDto result = momentService.getMomentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(momentRepository, times(1)).findById(1L);
        verify(momentMapper, times(1)).toDto(moment);
    }

    @Test
    void testGetMomentById_NotFound() {
        when(momentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundEntityException.class, () -> momentService.getMomentById(1L));
        verify(momentRepository, times(1)).findById(1L);
        verify(momentMapper, times(0)).toDto(any());
    }

    @Test
    void testGetListMomentForFilter_Success() {
        var listMoments = List.of(moment);
        when(momentRepository.findAllByProjectId(1L)).thenReturn(listMoments);
        when(momentMapper.toDto(moment)).thenReturn(momentDto);
        MomentFilterDto filters = MomentFilterDto.builder().date(LocalDateTime.now().minusMonths(7)).build();
        when(filter.isApplicable(filters)).thenReturn(true);
        when(filter.apply(listMoments, filters)).thenReturn(listMoments.stream());

        List<MomentDto> result = momentService.getListMomentForFilter(1L, filters);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(momentRepository, times(1)).findAllByProjectId(1L);
        verify(momentMapper, times(1)).toDto(moment);
    }

    @Test
    void testCreateMoment_Success() {
        when(projectService.getProjectDtoById(1L)).thenReturn(projectDto);
        when(momentMapper.toEntity(momentDto)).thenReturn(moment);
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        MomentDto result = momentService.createMoment(1L, momentDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(projectService, times(1)).getProjectDtoById(1L);
        verify(momentMapper, times(1)).toEntity(momentDto);
        verify(momentMapper, times(1)).toDto(moment);
    }

    @Test
    void testCreateMoment_ProjectCancelled() {
        projectDto.setStatus(ProjectStatus.CANCELLED);
        when(projectService.getProjectDtoById(1L)).thenReturn(projectDto);

        assertThrows(DataValidationExceptions.class, () -> momentService.createMoment(1L, momentDto));
        verify(projectService, times(1)).getProjectDtoById(1L);
        verify(momentMapper, times(0)).toEntity(any());
        verify(momentMapper, times(0)).toDto(any());
    }

    @Test
    void testUpdateMoment_Success() {
        momentDto.setProjectsId(new ArrayList<>(List.of(1L)));
        moment.setProjects(new ArrayList<>(List.of(Project.builder().id(1L).build())));
        when(projectService.getProjectDtoById(1L)).thenReturn(projectDto);
        when(momentMapper.toEntity(momentDto)).thenReturn(moment);
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        MomentDto result = momentService.updateMoment(1L, momentDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(projectService, times(1)).getProjectDtoById(1L);
        verify(momentMapper, times(1)).toEntity(momentDto);
        verify(momentMapper, times(1)).toDto(moment);
    }
}