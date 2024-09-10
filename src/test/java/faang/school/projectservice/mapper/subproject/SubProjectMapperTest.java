package faang.school.projectservice.mapper.subproject;

import faang.school.projectservice.dto.client.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.client.subproject.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class SubProjectMapperTest {
    ProjectDto projectDto = new ProjectDto();

    @Spy
    SubProjectMapperImpl subProjectMapper = new SubProjectMapperImpl();

    @Test
    void mapToSubDto() {
        projectDto.setId(1L);
        projectDto.setName("name");
        projectDto.setParentProject(new Project());
        projectDto.setVisibility(ProjectVisibility.PUBLIC);
        projectDto.setStatus(ProjectStatus.ON_HOLD);

        CreateSubProjectDto createSubProjectDto = subProjectMapper.mapToSubDto(projectDto);

        assertAll(
                () -> assertEquals(projectDto.getId(), createSubProjectDto.getId()),
                () -> assertEquals(projectDto.getName(), createSubProjectDto.getName()),
                () -> assertEquals(projectDto.getParentProject().getId(), createSubProjectDto.getParentProject().getId()),
                () -> assertEquals(projectDto.getVisibility(), createSubProjectDto.getVisibility()),
                () -> assertEquals(ProjectStatus.CREATED, createSubProjectDto.getStatus())
        );
    }

    @Test
    void mapToEntity() {
        CreateSubProjectDto subProjectDto = new CreateSubProjectDto();
        subProjectDto.setId(1L);
        subProjectDto.setName("name");
        subProjectDto.setParentProject(new Project());
        subProjectDto.setVisibility(ProjectVisibility.PUBLIC);
        subProjectDto.setStatus(ProjectStatus.ON_HOLD);

    }
}