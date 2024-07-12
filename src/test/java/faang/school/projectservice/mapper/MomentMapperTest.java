package faang.school.projectservice.mapper;

import faang.school.projectservice.TestDataFactory;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

class MomentMapperTest {

    private final MomentMapper mapper = Mappers.getMapper(MomentMapper.class);

    @Test
    void toDto() {
        var moment = TestDataFactory.createMoment();
        Project project = TestDataFactory.createProject();
        moment.getProjects().add(project);

        var momentDto = mapper.toDto(moment);

        assertThat(momentDto.getId()).isEqualTo(moment.getId());
        assertThat(momentDto.getName()).isEqualTo(moment.getName());
        assertThat(momentDto.getDescription()).isEqualTo(moment.getDescription());
        assertThat(momentDto.getDate()).isEqualTo(moment.getDate());
        assertThat(momentDto.getCreatedAt()).isEqualTo(moment.getCreatedAt());
        assertThat(momentDto.getUpdatedAt()).isEqualTo(moment.getUpdatedAt());
        assertThat(momentDto.getCreatedBy()).isEqualTo(moment.getCreatedBy());
        assertThat(momentDto.getUpdatedBy()).isEqualTo(moment.getUpdatedBy());

        assertThat(momentDto.getProjects()).isNotEmpty();
        assertThat(momentDto.getProjects().size()).isEqualTo(moment.getProjects().size());
        for (int i = 0; i < moment.getProjects().size(); i++) {
            assertThat(momentDto.getProjects().get(i).getId()).isEqualTo(moment.getProjects().get(i).getId());
        }
        IntStream.range(0, moment.getProjects().size()).forEach(i -> {
            assertThat(momentDto.getProjects().get(i).getId()).isEqualTo(moment.getProjects().get(i).getId());
            assertThat(momentDto.getProjects().get(i).getName()).isEqualTo(moment.getProjects().get(i).getName());
            assertThat(momentDto.getProjects().get(i).getDescription()).isEqualTo(moment.getProjects().get(i).getDescription());
        });

        assertThat(momentDto.getUserIds()).isNotEmpty();
        assertThat(momentDto.getUserIds().size()).isEqualTo(moment.getUserIds().size());

        assertThat(momentDto.getUserIds()).containsExactlyInAnyOrderElementsOf(moment.getUserIds());
    }

    @Test
    void toEntity() {
        var momentDto = TestDataFactory.createMomentDto();
        var projectDto = TestDataFactory.createProjectDto();
        momentDto.getProjects().add(projectDto);

        var moment = mapper.toEntity(momentDto);

        assertThat(moment.getId()).isEqualTo(momentDto.getId());
        assertThat(moment.getName()).isEqualTo(momentDto.getName());
        assertThat(moment.getDescription()).isEqualTo(momentDto.getDescription());
        assertThat(moment.getDate()).isEqualTo(momentDto.getDate());
        assertThat(moment.getCreatedAt()).isEqualTo(momentDto.getCreatedAt());
        assertThat(moment.getUpdatedAt()).isEqualTo(momentDto.getUpdatedAt());
        assertThat(moment.getCreatedBy()).isEqualTo(momentDto.getCreatedBy());
        assertThat(moment.getUpdatedBy()).isEqualTo(momentDto.getUpdatedBy());

        assertThat(moment.getProjects()).isNotEmpty();
        assertThat(moment.getProjects().size()).isEqualTo(momentDto.getProjects().size());
        for (int i = 0; i < momentDto.getProjects().size(); i++) {
            assertThat(moment.getProjects().get(i).getId()).isEqualTo(momentDto.getProjects().get(i).getId());
        }
        IntStream.range(0, momentDto.getProjects().size()).forEach(i -> {
            assertThat(moment.getProjects().get(i).getId()).isEqualTo(momentDto.getProjects().get(i).getId());
            assertThat(moment.getProjects().get(i).getName()).isEqualTo(momentDto.getProjects().get(i).getName());
            assertThat(moment.getProjects().get(i).getDescription()).isEqualTo(momentDto.getProjects().get(i).getDescription());
        });

        assertThat(moment.getUserIds()).isNotEmpty();
        assertThat(moment.getUserIds().size()).isEqualTo(momentDto.getUserIds().size());

        assertThat(moment.getUserIds()).containsExactlyInAnyOrderElementsOf(momentDto.getUserIds());
    }
}