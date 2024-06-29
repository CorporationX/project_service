package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.filter.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MomentProjectFilterTest {

    @Test
    public void testGetAllMomentsByProjects() {
        MomentFilter momentProjectFilter = new MomentProjectFilter();
        List<Moment> moments = getMomentProjectList();
        List<Moment> expectedList = getExpectedMomentProjectList();
        MomentFilterDto momentFilterDto = MomentFilterDto.builder()
                .projects(List.of(1L, 2L))
                .build();

        Stream<Moment> momentStream = momentProjectFilter.apply(moments, momentFilterDto);
        assertEquals(expectedList, momentStream.toList());
    }

    private static List<Moment> getExpectedMomentProjectList() {
        return List.of(Moment.builder()
                        .id(1L)
                        .name("Moment1")
                        .projects(List.of(
                                Project.builder().id(1L).build(),
                                Project.builder().id(2L).build()))
                        .build(),
                Moment.builder()
                        .id(3L)
                        .name("Moment3")
                        .projects(List.of(
                                Project.builder().id(1L).build(),
                                Project.builder().id(2L).build()))
                        .build()
        );
    }

    private static List<Moment> getMomentProjectList() {
        return new ArrayList<>(List.of(Moment.builder()
                        .id(1L)
                        .name("Moment1")
                        .projects(List.of(Project.builder().id(1L).build(),
                                Project.builder().id(2L).build()))
                        .build(),
                Moment.builder()
                        .id(2L)
                        .name("Moment2")
                        .projects(List.of(Project.builder().id(3L).build(),
                                Project.builder().id(2L).build()))
                        .build(),
                Moment.builder()
                        .id(3L)
                        .name("Moment3")
                        .projects(List.of(Project.builder().id(1L).build(),
                                Project.builder().id(2L).build()))
                        .build(),
                Moment.builder()
                        .id(4L)
                        .name("Moment4")
                        .projects(List.of(Project.builder().id(4L).build(),
                                Project.builder().id(5L).build()))
                        .build()
        ));
    }
}