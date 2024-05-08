package faang.school.projectservice.service.initiative;

import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.mapper.InitiativeMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.InitiativeRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validation.InitiativeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InitiativeServiceImplTest {
    @Mock
    private InitiativeMapper mapper;
    @Mock
    private InitiativeValidator validator;
    @Mock
    private InitiativeRepository initiativeRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private StageRepository stageRepository;
    @InjectMocks
    private InitiativeServiceImpl service;

    private InitiativeDto dto;
    private Initiative initiative;
    private List<Stage> stages;
    private Project project;
    private TeamMember curator;

    @BeforeEach
    void init() {
        stages = List.of(
                Stage.builder().stageId(1L).build(),
                Stage.builder().stageId(2L).build(),
                Stage.builder().stageId(3L).build()
        );

        project = Project.builder().id(4L).build();

        curator = TeamMember.builder().userId(5L).build();

        initiative = Initiative.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .status(InitiativeStatus.ACCEPTED)
                .stages(stages)
                .curator(curator)
                .project(project)
                .build();

        dto = InitiativeDto.builder()
                .id(0L)
                .name("name")
                .description("desc")
                .curatorId(5L)
                .projectId(4L)
                .status(InitiativeStatus.IN_PROGRESS)
                .stageIds(List.of(1L, 2L, 3L))
                .build();
    }

    @Test
    void create() {
        when(projectRepository.getProjectById(project.getId())).thenReturn(project);
        when(teamMemberRepository.findById(curator.getUserId())).thenReturn(curator);
        when(stageRepository.findAll()).thenReturn(stages);
        when(mapper.toEntity(dto, project, curator, stages)).thenReturn(initiative);
        when(initiativeRepository.save(initiative)).thenReturn(initiative);
        when(mapper.toDto(any())).thenReturn(dto);

        InitiativeDto actual = service.create(dto);
        assertEquals(dto, actual);

        InOrder inOrder = inOrder(validator, projectRepository, teamMemberRepository,
                stageRepository, mapper, initiativeRepository);
        inOrder.verify(validator, times(1)).validate(dto);
        inOrder.verify(projectRepository, times(1)).getProjectById(dto.getProjectId());
        inOrder.verify(teamMemberRepository, times(1)).findById(dto.getCuratorId());
        inOrder.verify(stageRepository, times(1)).findAll();
        inOrder.verify(mapper, times(1)).toEntity(dto, project, curator, stages);
        inOrder.verify(initiativeRepository, times(1)).save(initiative);
        inOrder.verify(mapper, times(1)).toDto(initiative);

    }
}