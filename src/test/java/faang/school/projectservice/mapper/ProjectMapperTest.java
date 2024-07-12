package faang.school.projectservice.mapper;

import faang.school.projectservice.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.*;

class ProjectMapperTest {

    private final ProjectMapper mapper = Mappers.getMapper(ProjectMapper.class);

    @Test
    void toDto() {
        var project = TestDataFactory.createProject();
        var moment = TestDataFactory.createMoment();
        project.getMoments().add(moment);

        var projectDto = mapper.toDto(project);

        assertThat(projectDto.getId()).isEqualTo(project.getId());
        assertThat(projectDto.getName()).isEqualTo(project.getName());
        assertThat(projectDto.getDescription()).isEqualTo(project.getDescription());
        assertThat(projectDto.getCreatedAt()).isEqualTo(project.getCreatedAt());
        assertThat(projectDto.getUpdatedAt()).isEqualTo(project.getUpdatedAt());
        assertThat(projectDto.getStatus()).isEqualTo(project.getStatus());
    }

    @Test
    void toEntity() {
        var projectDto = TestDataFactory.createProjectDto();
        var momentDto = TestDataFactory.createMomentDto();
        projectDto.getMoments().add(momentDto);

        var project = mapper.toEntity(projectDto);

        assertThat(project.getId()).isEqualTo(projectDto.getId());
        assertThat(project.getName()).isEqualTo(projectDto.getName());
        assertThat(project.getDescription()).isEqualTo(projectDto.getDescription());
        assertThat(project.getCreatedAt()).isEqualTo(projectDto.getCreatedAt());
        assertThat(project.getUpdatedAt()).isEqualTo(projectDto.getUpdatedAt());
        assertThat(project.getStatus()).isEqualTo(projectDto.getStatus());
    }
}