package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MomentProjectsFilterTest {
    private MomentProjectsFilter momentProjectsFilter;
    List<Moment> moments;

    @BeforeEach
    void init() {
        momentProjectsFilter = new MomentProjectsFilter();
        moments = List.of(Moment.builder()
                        .id(1L)
                        .name("Moment 1")
                        .projects(List.of(
                                Project.builder()
                                        .id(1L)
                                        .build(),
                                Project.builder()
                                        .id(2L)
                                        .build()))
                        .build(),
                Moment.builder()
                        .id(2L)
                        .name("Moment 2")
                        .projects(List.of(
                                Project.builder()
                                        .id(3L)
                                        .build(),
                                Project.builder()
                                        .id(2L)
                                        .build()))
                        .build(),
                Moment.builder()
                        .id(3L)
                        .name("Moment 3")
                        .projects(List.of(
                                Project.builder()
                                        .id(1L)
                                        .build(),
                                Project.builder()
                                        .id(2L)
                                        .build()))
                        .build(),
                Moment.builder()
                        .id(4L)
                        .name("Moment 4")
                        .projects(List.of(
                                Project.builder()
                                        .id(6L)
                                        .build(),
                                Project.builder()
                                        .id(7L)
                                        .build()))
                        .build()
        );
    }

    @Test
    public void testIsApplicableProjectsPatternShouldReturnTrue() {
        MomentFilterDto momentFilterDto = MomentFilterDto.builder()
                .projectsPattern(List.of(1L,2L))
                .build();
        assertTrue(momentProjectsFilter.isApplicable(momentFilterDto));
    }

    @Test
    public void testIsApplicableProjectsPatternShouldReturnFalse() {
        MomentFilterDto momentFilterDto = MomentFilterDto.builder().build();
        assertFalse(momentProjectsFilter.isApplicable(momentFilterDto));
    }

    @Test
    public void testApplyProjectsPatternShouldReturnFilteredList() {
        MomentFilterDto momentFilterDto = MomentFilterDto.builder()
                .projectsPattern(List.of(1L,2L))
                .build();

        List<Moment> expectedList = List.of(Moment.builder()
                        .id(1L)
                        .name("Moment 1")
                        .projects(List.of(
                                Project.builder()
                                        .id(1L)
                                        .build(),
                                Project.builder()
                                        .id(2L)
                                        .build()))
                        .build(),
                Moment.builder()
                        .id(3L)
                        .name("Moment 3")
                        .projects(List.of(
                                Project.builder()
                                        .id(1L)
                                        .build(),
                                Project.builder()
                                        .id(2L)
                                        .build()))
                        .build()
        );

        Stream<Moment> momentStream = momentProjectsFilter.apply(moments.stream(), momentFilterDto);
        assertEquals(expectedList, momentStream.toList());
    }
}
