package faang.school.projectservice.service.stage;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.filter.stage.StageFilter;
import faang.school.projectservice.filter.stage.StageStatusFilter;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.validator.stage.StageDtoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StageServiceTest {
    @InjectMocks
    StageService stageService;

    @Mock
    StageRepository stageRepository;

    @Mock
    StageDtoValidator stageDtoValidator;

    @Mock
    StageMapper stageMapper;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    List<StageFilter> stageFiltersMock;

    private Stage stage;
    private StageDto stageDto;
    private Long id;
    private StageRoles stageRoles;
    private List<StageRoles> stageRolesList;
    private List<Stage> stages;
    private List<StageDto> stageDtos;
    private StageFilterDto stageFilterDto;
    private Project project;
    private List<StageFilter> stageFilters;
    private StageFilter stageStatusFilter;

    @BeforeEach
    public void setUp() {
        id = 1L;

        stage = new Stage();
        stageDto = new StageDto();
        stageRoles = new StageRoles();
        stageFilterDto = new StageFilterDto();
        project = new Project();
        stageStatusFilter = Mockito.mock(StageStatusFilter.class);

        stages = new ArrayList<>();
        stageRolesList = new ArrayList<>();
        stageFilters = new ArrayList<>();
        stageDtos = new ArrayList<>();

        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreate() {
        stageDto.setStageId(id);

        doNothing().when(stageDtoValidator).validateProjectId(id);
        doNothing().when(stageDtoValidator).validateStageRolesCount(any());
        when(stageMapper.toEntity(any(), any())).thenReturn(stage);
        when(stageRepository.save(any())).thenReturn(stage);
        when(stageMapper.toDto(any())).thenReturn(stageDto);

        StageDto result = stageService.create(stageDto);

        verify(stageDtoValidator).validateProjectId(id);
        verify(stageDtoValidator).validateStageRolesCount(any());
        verify(stageMapper).toEntity(any(), any());
        verify(stageRepository).save(any());
        verify(stageMapper).toDto(any());
        assertEquals(stageDto, result);
    }

    @Test
    public void testGetStages() {
        project.setStages(stages);
        Stream<Stage> stageStream = stages.stream();
        stages.add(stage);
        stageDtos.add(stageDto);
        stageFilters.add(stageStatusFilter);

        when(projectRepository.getProjectById(id)).thenReturn(project);
        when(stageFiltersMock.stream()).thenReturn(stageFilters.stream());
        when(stageStatusFilter.isApplicable(any())).thenReturn(true);
        when(stageStatusFilter.apply(any(), any())).thenReturn(stageStream);
        when(stageMapper.toDto(any())).thenReturn(stageDto);

        List<StageDto> result = stageService.getStages(id, stageFilterDto);

        verify(projectRepository).getProjectById(id);
        verify(stageFiltersMock).stream();
        verify(stageStatusFilter).isApplicable(any());
        verify(stageStatusFilter).apply(any(), any());
        verify(stageMapper).toDto(any());
        assertEquals(stageDtos, result);
    }

    //public void
}
