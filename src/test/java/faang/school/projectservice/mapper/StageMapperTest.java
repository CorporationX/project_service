package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.mapper.stage.StageMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class StageMapperTest {
    StageMapper stageMapper = Mappers.getMapper(StageMapper.class);

    @Test
    public void shouldConvertDtoToEntity () {
        StageDto dto = StageDto.builder()
                .id(1L)
                .name("Development")
                .projectId(1L)
                .build();

        Stage entity = stageMapper.toEntity(dto);

        assertNotNull(entity);
        assertEquals(dto.getId(), entity.getStageId());
        assertEquals(dto.getName(), entity.getStageName());
        assertEquals(dto.getProjectId(), entity.getProject().getId());
    }

    @Test
    public void shouldConvertEntityToDto () {
        Stage entity = Stage.builder()
                .stageName("Planning")
                .stageId(2L)
                .project(Project.builder().id(4L).name("Transactions").build())
                .build();

        StageDto dto = stageMapper.toDto(entity);

        assertNotNull(dto);
        assertEquals(entity.getStageId(), dto.getId());
        assertEquals(entity.getStageName(), dto.getName());
        assertEquals(entity.getProject().getId(), dto.getProjectId());
    }
}
