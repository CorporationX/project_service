package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static faang.school.projectservice.model.ProjectStatus.CREATED;
import static faang.school.projectservice.model.ProjectVisibility.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ProjectMapperTest {
    @Spy
    private ProjectMapperImpl projectMapper;
    private Project project;
    private ProjectDto projectDto;

    @BeforeEach
    void setUp() {
        project = Project.builder()
                .status(CREATED)
                .visibility(PUBLIC)
                .build();
        projectDto = ProjectDto
                .builder()
                .status(CREATED)
                .visibility(PUBLIC)
                .build();
    }

    @Test
    void testToDto() {
        assertEquals(projectDto, projectMapper.toDto(project));
    }

    @Test
    void testToEntity() {
        assertEquals(project, projectMapper.toEntity(projectDto));
    }
}