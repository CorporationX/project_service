package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectStatusFilterDto;
import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.projectstatusfilter.ProjectStatusFilterImpl;
import faang.school.projectservice.filter.projectstatusfilter.ProjectStatusFilter;
import faang.school.projectservice.mapper.StageMapperImpl;
import faang.school.projectservice.mapper.StageRolesMapperImpl;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;
import faang.school.projectservice.repository.StageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class StageServiceTest {

    @InjectMocks
    private StageService stageService;

    @Mock
    private StageRepository stageRepository;

    @Spy
    private StageRolesMapperImpl stageRolesMapper;

    @Spy
    private StageMapperImpl stageMapper;

    private Stage stage1;
    private Stage stage2;
    private Stage stage3;

    private StageDto stageDto1;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(stageMapper, "stageRolesMapper", stageRolesMapper);
        stage1 = Stage
                .builder()
                .stageId(1L)
                .stageName("stage")
                .project(Project.builder().id(1L).status(ProjectStatus.CREATED).build())
                .stageRoles(List.of(StageRoles
                        .builder()
                        .id(1L)
                        .teamRole(TeamRole.ANALYST)
                        .count(1)
                        .build()))
                .build();
        stage2 = Stage
                .builder()
                .stageId(1L)
                .stageName("stage")
                .project(Project.builder().id(1L).status(ProjectStatus.CANCELLED).build())
                .stageRoles(List.of(StageRoles
                        .builder()
                        .id(1L)
                        .teamRole(TeamRole.ANALYST)
                        .count(1)
                        .build()))
                .build();
        stage3 = Stage
                .builder()
                .stageId(1L)
                .stageName("stage")
                .project(Project.builder().id(1L).status(ProjectStatus.CREATED).build())
                .stageRoles(List.of(StageRoles
                        .builder()
                        .id(1L)
                        .teamRole(TeamRole.ANALYST)
                        .count(1)
                        .build()))
                .build();
        stageDto1 = stageMapper.toDto(stage1);
    }

    @Test
    void testCreateStage() {
        stageService.createStage(stageDto1);
        Mockito.verify(stageRepository, Mockito.times(1)).save(stage1);
    }

    @Test
    void testCreateStageNegativeProjectByCancelled() {
        stageDto1.getProject().setStatus(ProjectStatus.CANCELLED);
        assertThrows(DataValidationException.class, () -> stageService.createStage(stageDto1));
    }

    @Test
    void testGetStagesByProjectStatus() {
        Mockito.when(stageRepository.findAll()).thenReturn(List.of(stage1, stage2, stage3));
        List<ProjectStatusFilter> projectStatusFilters = List.of(
                new ProjectStatusFilterImpl()
        );
        ProjectStatusFilterDto filter = ProjectStatusFilterDto.builder()
                .status(ProjectStatus.CREATED)
                .build();
        stageService = new StageService(stageRepository, stageMapper, projectStatusFilters);

        List<StageDto> actual = stageService.getStagesByProjectStatus(filter);
        List<StageDto> expected = Stream.of(stage1, stage3)
                .map(stageMapper::toDto)
                .toList();

        assertEquals(expected, actual);
    }
}