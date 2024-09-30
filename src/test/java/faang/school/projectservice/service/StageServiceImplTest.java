package faang.school.projectservice.service;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageRolesDto;
import faang.school.projectservice.dto.TeamMemberDto;
import faang.school.projectservice.dto.filter.StageFilterDto;
import faang.school.projectservice.filter.StageFilter;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.jpa.StageJpaRepository;
import faang.school.projectservice.mapper.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.filter.ExecutorsRoleFilter;
import faang.school.projectservice.filter.StageTaskFilter;
import faang.school.projectservice.validator.StageServiceValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class StageServiceImplTest {
    private StageJpaRepository stageRepository;
    private ProjectJpaRepository projectRepository;
    private StageMapper mapper;
    private StageServiceValidator validator;
    private StageServiceImpl service;
    private List<StageFilter> filters;

    private StageDto stageDto;


    @BeforeEach
    public void init() {
        List<StageRolesDto> rolesDtos = List.of(
                new StageRolesDto(1L, 2, TeamRole.DESIGNER),
                new StageRolesDto(2L, 3, TeamRole.DESIGNER)
        );
        stageDto = new StageDto(1L, "1", 1L, rolesDtos);

        stageRepository = Mockito.mock(StageJpaRepository.class);
        projectRepository = Mockito.mock(ProjectJpaRepository.class);
        mapper = Mockito.mock(StageMapper.class);
        validator = Mockito.mock(StageServiceValidator.class);

        ExecutorsRoleFilter roleFilterMock = Mockito.mock(ExecutorsRoleFilter.class);
        StageTaskFilter taskFilterMock = Mockito.mock(StageTaskFilter.class);
        filters = List.of(
                roleFilterMock, taskFilterMock
        );

        service = new StageServiceImpl(stageRepository, projectRepository, mapper, validator, filters);

        Mockito.lenient().when(projectRepository.existsById(1L))
                .thenReturn(true);
    }

    private void initRolesAndMembers() {
        List<TeamMemberDto> teamMemberDtos = List.of(
                new TeamMemberDto(1L, List.of(TeamRole.DESIGNER, TeamRole.ANALYST)),
                new TeamMemberDto(2L, List.of(TeamRole.DESIGNER)),
                new TeamMemberDto(3L, List.of(TeamRole.ANALYST))
        );
        stageDto.setExecutorsDtos(teamMemberDtos);


    }

    @Test
    void create() {
        Mockito.when(mapper.toStage(stageDto))
                .thenReturn(null);

        service.create(stageDto);

        Mockito.verify(mapper, Mockito.times(1))
                .toStage(stageDto);
        Mockito.verify(stageRepository, Mockito.times(1))
                .save(null);
    }

    @Test
    void testGetStageById() {
        Long stageId = stageDto.getStageId();

        Mockito.when(stageRepository.getById(stageId))
                .thenReturn(null);

        service.getStageById(stageId);

        Mockito.verify(mapper, Mockito.times(1))
                .toDto(null);
        Mockito.verify(stageRepository, Mockito.times(1))
                .getById(stageId);
    }

    @Test
    void testDeleteStage() {
        service.deleteStage(stageDto);

        Mockito.verify(validator, Mockito.times(1))
                .validateExecutorsStageRoles(stageDto);
        Mockito.verify(stageRepository, Mockito.times(1))
                .deleteById(stageDto.getStageId());
    }

    @Test
    void getFilteredStages_role() {
        Long projectId = 1L;
        Project project = Mockito.mock(Project.class);
        StageFilterDto filterDto = new StageFilterDto(TeamRole.DEVELOPER, null, null);
        Stage stage = new Stage();
        List<Stage> stages = Mockito.mock(List.class);
        Stream<Stage> stageStream = Stream.of(stage, stage, stage);


        Mockito.when(projectRepository.getReferenceById(projectId))
                .thenReturn(project);
        Mockito.when(project.getStages())
                .thenReturn(stages);
        Mockito.when(filters.get(0).isApplicable(filterDto))
                .thenReturn(true);
        Mockito.when(filters.get(1).isApplicable(filterDto))
                .thenReturn(false);
        Mockito.when(filters.get(0).apply(stageStream, filterDto))
                .thenReturn(stageStream);
        Mockito.when(stages.stream())
                .thenReturn(stageStream);

        service.getFilteredStages(projectId, filterDto);

        Mockito.verify(validator, Mockito.times(1))
                .validateProjectNotCanceled(project.getStatus());
        Mockito.verify(projectRepository, Mockito.times(1))
                .getReferenceById(projectId);
        Mockito.verify(filters.get(0), Mockito.times(1))
                .isApplicable(filterDto);
        Mockito.verify(filters.get(1), Mockito.times(1))
                .isApplicable(filterDto);
        Mockito.verify(filters.get(0), Mockito.times(1))
                .apply(stageStream, filterDto);
        Mockito.verify(filters.get(1), Mockito.never())
                .apply(stageStream, filterDto);
        Mockito.verify(mapper, Mockito.times(3))
                .toDto(stage);
    }

    @Test
    void updateStage() {
        initRolesAndMembers();
        service.updateStage(stageDto);

        Mockito.verify(validator, Mockito.times(1))
                .validateExecutorsStageRoles(stageDto);
    }

    @Test
    void getNumberOfRolesInvolved_whenOk() {
        initRolesAndMembers();

        Map<TeamRole, Long> actualMap = Map.of(
                TeamRole.DESIGNER, 2L,
                TeamRole.ANALYST, 2L
        );

        Assertions.assertEquals(service.getNumberOfRolesInvolved(stageDto), actualMap);
    }

    @Test
    void sentInvitationToNeededExecutors_whenOk() {
        initRolesAndMembers();
        Map<TeamRole, Long> actualMap = new java.util.HashMap<>(Map.of(
                TeamRole.DESIGNER, 1L,
                TeamRole.ANALYST, 1L
        ));

        service.sentInvitationToNeededExecutors(actualMap, stageDto);

        Mockito.verify(validator, Mockito.times(1))
                .validateCount(1L);
        Mockito.verify(validator, Mockito.times(1))
                .validateCount(2L);
    }
}
