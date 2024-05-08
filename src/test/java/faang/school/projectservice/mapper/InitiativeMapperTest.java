package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.initiative.InitiativeDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.initiative.Initiative;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InitiativeMapperTest {
    private InitiativeMapper mapper;
    private Initiative initiative;
    private InitiativeDto dto;
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
                .id(1L)
                .name("name")
                .description("desc")
                .status(InitiativeStatus.ACCEPTED)
                .stageIds(List.of(1L, 2L, 3L))
                .curatorId(5L)
                .projectId(4L)
                .build();

        mapper = Mappers.getMapper(InitiativeMapper.class);
    }

    @Test
    void toEntity() {
        Initiative actual = mapper.toEntity(dto, project, curator, stages);
        assertEquals(initiative, actual);
    }

    @Test
    void toDto() {
        InitiativeDto actual = mapper.toDto(initiative);
        assertEquals(dto, actual);
    }
}