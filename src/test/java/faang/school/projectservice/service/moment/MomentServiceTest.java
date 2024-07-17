package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.exceptions.MomentValidationExceptions;
import faang.school.projectservice.exceptions.NotFoundElementInDataBaseException;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.moment.filter.MomentFilter;
import faang.school.projectservice.service.moment.filter.impl.MomentDateFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {

    private MomentRepository momentRepository;

    private ProjectRepository projectRepository;

    private MomentMapper momentMapper;

    private MomentService momentService;

    private List<MomentFilter> filters;

    @BeforeEach
    void setUp() {
        MomentFilter momentDateFilter = Mockito.mock(MomentDateFilter.class);
        filters = Arrays.asList(momentDateFilter);
        momentMapper = Mockito.mock(MomentMapper.class);
        projectRepository = Mockito.mock(ProjectRepository.class);
        momentRepository = Mockito.mock(MomentRepository.class);

        momentService = new MomentService(momentRepository, projectRepository, momentMapper, filters);
    }

    @Test
    void testGetMomentById_WhenMomentNotFound_ShouldThrowNotFoundElementInDataBase() {
        Long momentId = 1L;
        lenient().when(momentRepository.findById(momentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundElementInDataBaseException.class, () ->
                momentService.getMomentById(momentId));
        verify(momentRepository).findById(momentId);
    }

    @Test
    void testGetMomentById_WhenMomentFound_ShouldReturnMomentDto() {
        Long momentId = 1L;
        Moment moment = new Moment();
        MomentDto momentDto = new MomentDto();
        when(momentRepository.findById(momentId)).thenReturn(Optional.of(moment));
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        MomentDto result = momentService.getMomentById(momentId);

        assertNotNull(result);
        assertEquals(momentDto, result);
        verify(momentRepository).findById(momentId);
        verify(momentMapper).toDto(moment);
    }

    @Test
    void testGetListMomentForFilter_ShouldReturnFilteredMoments() {
        Long projectId = 1L;
        MomentFilterDto filter = new MomentFilterDto();
        Moment moment = new Moment();
        List<Moment> moments = List.of(moment);
        MomentDto momentDto = new MomentDto();

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(momentRepository.findAllByProjectId(projectId)).thenReturn(moments);
        when(filters.get(0).isApplicable(filter)).thenReturn(true);
        when(filters.get(0).apply(moments, filter)).thenReturn(Stream.of(moment));
        when(momentMapper.toDto(moment)).thenReturn(momentDto);


        List<MomentDto> result = momentService.getListMomentForFilter(projectId, filter);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(momentDto, result.get(0));
        verify(momentRepository).findAllByProjectId(projectId);
        verify(filters.get(0)).apply(moments, filter);
        verify(momentMapper).toDto(moment);
    }

    @Test
    void testGetAllMomentProject_ShouldReturnAllMomentsForProject() {
        Long projectId = 1L;
        Project project = new Project();
        Moment moment = new Moment();
        project.setMoments(List.of(moment));
        MomentDto momentDto = new MomentDto();

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        List<MomentDto> result = momentService.getAllMomentProject(projectId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(projectRepository).getProjectById(projectId);
        verify(momentMapper).toDto(moment);
    }

    @Test
    void testCreateMoment_ShouldCreateAndReturnMomentDto() {
        Long projectId = 1L;
        MomentDto momentDto = new MomentDto();
        Moment moment = new Moment();
        Project project = new Project();

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(momentMapper.toEntity(momentDto)).thenReturn(moment);
        when(momentRepository.save(moment)).thenReturn(moment);
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        MomentDto result = momentService.createMoment(projectId, momentDto);

        assertNotNull(result);
        assertEquals(momentDto, result);
        verify(projectRepository).getProjectById(projectId);
        verify(momentMapper).toEntity(momentDto);
        verify(momentRepository).save(moment);
        verify(momentMapper).toDto(moment);
    }

    @Test
    void testCreateMoment_ShouldReturnException() {
        Long projectId = 1L;
        MomentDto momentDto = new MomentDto();
        Project project = new Project();
        project.setStatus(ProjectStatus.CANCELLED);
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);

        assertThrows(MomentValidationExceptions.class, () ->
                momentService.createMoment(projectId, momentDto));
        verify(projectRepository).getProjectById(projectId);
    }

    @Test
    void testUpdateMoment_ShouldUpdateAndReturnMomentDto() {
        Long projectId = 1L;
        MomentDto momentDto = new MomentDto();
        List<Long> projectIds = new ArrayList<>();
        projectIds.add(projectId);
        momentDto.setProjectsId(projectIds);
        Moment moment = new Moment();
        Project project = new Project();
        project.setId(projectId);
        List<Project> projects = new ArrayList<>();
        projects.add(project);
        moment.setProjects(projects);

        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(projectRepository.getProjectById(projectId)).thenReturn(project);
        when(momentMapper.toEntity(momentDto)).thenReturn(moment);
        when(momentRepository.save(moment)).thenReturn(moment);
        when(momentMapper.toDto(moment)).thenReturn(momentDto);

        MomentDto result = momentService.updateMoment(projectId, momentDto);

        assertNotNull(result);
        assertEquals(momentDto, result);
        verify(projectRepository).getProjectById(projectId);
        verify(momentMapper).toEntity(momentDto);
        verify(momentRepository).save(moment);
        verify(momentMapper).toDto(moment);
    }

    @Test
    void testValidateProjectId_ShouldThrowNotFoundElementInDataBaseForNonExistingProject() {
        Long projectId = 1L;

        assertThrows(NotFoundElementInDataBaseException.class, () ->
                momentService.getAllMomentProject(projectId));
    }
}