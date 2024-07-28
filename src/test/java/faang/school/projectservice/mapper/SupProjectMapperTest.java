package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.UpdateSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
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

    @Test
    public void updateEntity() {
        project.setStatus(ProjectStatus.IN_PROGRESS);
        project.setVisibility(ProjectVisibility.PRIVATE);
        UpdateSubProjectDto updateDto = UpdateSubProjectDto.builder()
                .status(ProjectStatus.COMPLETED)
                .build();

        subProjectMapper.updateEntity(updateDto, project);

        assertEquals(ProjectStatus.COMPLETED, project.getStatus());
        assertEquals(ProjectVisibility.PRIVATE, project.getVisibility());
    }
}
