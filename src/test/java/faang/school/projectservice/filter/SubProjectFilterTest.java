package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static faang.school.projectservice.model.ProjectStatus.COMPLETED;
import static faang.school.projectservice.model.ProjectStatus.CREATED;
import static faang.school.projectservice.model.ProjectStatus.IN_PROGRESS;
import static faang.school.projectservice.model.ProjectStatus.ON_HOLD;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubProjectFilterTest {

    private List<SubProjectFilter> filters;

    private SubProjectFilterDto subProjectFilterDto;

    @BeforeEach
    void setUp() {
        filters = new ArrayList<>();
        filters.add(new SubProjectNameFilter());
        filters.add(new SubProjectStatusFilter());
    }

    @Test
    void testSubProjectFilters() {
        subProjectFilterDto = SubProjectFilterDto.builder()
                .name("name")
                .status(IN_PROGRESS)
                .build();

        List<ProjectDto> dtos = new ArrayList<>();
        dtos.add(ProjectDto.builder().name("name").status(IN_PROGRESS).build());
        dtos.add(ProjectDto.builder().name("any name").status(IN_PROGRESS).build());
        dtos.add(ProjectDto.builder().name("non name").status(COMPLETED).build());
        dtos.add(ProjectDto.builder().name("qwe").status(ON_HOLD).build());
        dtos.add(ProjectDto.builder().name("123").status(CREATED).build());

        filters.stream()
                .filter(filter -> filter.isApplicable(subProjectFilterDto))
                .forEach(filter -> filter.apply(dtos, subProjectFilterDto));

        assertEquals(2, dtos.size());
    }
}
