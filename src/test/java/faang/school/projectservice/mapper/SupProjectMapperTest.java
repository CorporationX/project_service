package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SupProjectMapperTest {
    private SubProjectMapper subProjectMapper = Mappers.getMapper(SubProjectMapper.class);
    private Long projectId = 2L;
    private Long parentProjectId = 1L;
    private String name = "SubProject name";
    private String description = "SubProject description";
    private Project project = Project.builder()
            .name(name)
            .description(description)
            .build();
    private CreateSubProjectDto subProjectDto = CreateSubProjectDto.builder()
            .parentProjectId(parentProjectId)
            .name(name)
            .description(description)
            .build();

    @Test
    public void toEntity() {
        Project result = subProjectMapper.toEntity(subProjectDto);

        assertEquals(project, result);
    }
}
