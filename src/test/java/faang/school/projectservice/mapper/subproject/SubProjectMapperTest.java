package faang.school.projectservice.mapper.subproject;

import faang.school.projectservice.model.dto.CreateSubProjectDto;
import faang.school.projectservice.model.dto.ProjectDto;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.model.enums.ProjectStatus;
import faang.school.projectservice.model.enums.ProjectVisibility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SubProjectMapperTest {
    final ProjectDto projectDto = new ProjectDto();

    @Spy
    private SubProjectMapperImpl subProjectMapper = new SubProjectMapperImpl();

    @Test
    void mapToSubDto() {
        projectDto.setId(1L);
        projectDto.setName("name");
        projectDto.setParentProjectId(new Project().getId());
        projectDto.setVisibility(ProjectVisibility.PUBLIC);
        projectDto.setStatus(ProjectStatus.ON_HOLD);

        CreateSubProjectDto createSubProjectDto = subProjectMapper.mapToSubDto(projectDto);
        System.out.println(projectDto.getStatus());
        assertAll(
                () -> assertEquals(projectDto.getId(), createSubProjectDto.getId()),
                () -> assertEquals(projectDto.getName(), createSubProjectDto.getName()),
                () -> assertEquals(projectDto.getParentProjectId(), createSubProjectDto.getParentProjectId()),
                () -> assertEquals(projectDto.getVisibility(), createSubProjectDto.getVisibility()),
                () -> assertEquals(ProjectStatus.ON_HOLD, createSubProjectDto.getStatus())
        );
    }

    @Test
    void mapToEntity() {
        CreateSubProjectDto subProjectDto = new CreateSubProjectDto();
        subProjectDto.setId(1L);
        subProjectDto.setName("name");
        subProjectDto.setParentProjectId(new Project().getId());
        subProjectDto.setVisibility(ProjectVisibility.PUBLIC);
        subProjectDto.setStatus(ProjectStatus.ON_HOLD);

    }
}