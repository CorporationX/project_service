package faang.school.projectservice.filter.moment;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MomentFilterTest {

    private List<MomentFilter> filters;
    private List<Moment> moments;

    @BeforeEach
    void setUp() {
        filters = List.of(
                new MomentMonthFilter(),
                new MomentProjectsFilter()
        );

        List<Project> projects = List.of(
                Project.builder().id(1L).build(),
                Project.builder().id(2L).build(),
                Project.builder().id(3L).build()
        );

        moments = new ArrayList<>();
        moments.add(Moment.builder()
                .date(LocalDateTime.of(2023, 1, 1, 0, 0))
                .projects(projects)
                .build());

        moments.add(Moment.builder()
                .date(LocalDateTime.of(2023, 2, 1, 0, 0))
                .projects(projects)
                .build());

        moments.add(Moment.builder()
                .date(LocalDateTime.of(2023, 1, 1, 0, 0))
                .projects(new ArrayList<>())
                .build());
    }

    @Test
    void isApplicable_shouldReturnMonthFilter() {
        MomentFilterDto momentFilterDto = MomentFilterDto.builder()
                .month(2)
                .build();

        filters.stream()
                .filter(f -> f.isApplicable(momentFilterDto))
                .findFirst()
                .ifPresentOrElse(
                        filter -> assertTrue(filter instanceof MomentMonthFilter),
                        Assertions::fail
                );
    }

    @Test
    void isApplicable_shouldReturnProjectsFilter() {
        MomentFilterDto momentFilterDto = MomentFilterDto.builder()
                .projectIds(List.of(1L, 2L))
                .build();

        filters.stream()
                .filter(f -> f.isApplicable(momentFilterDto))
                .findFirst()
                .ifPresentOrElse(
                        filter -> assertTrue(filter instanceof MomentProjectsFilter),
                        Assertions::fail
                );
    }

    @Test
    void apply_shouldReturnOnlyOneMomentAfterFiltration() {
        MomentFilterDto momentFilterDto = MomentFilterDto.builder()
                .month(1)
                .projectIds(List.of(1L, 2L, 3L))
                .build();

        filters.stream()
                .filter(f -> f.isApplicable(momentFilterDto))
                .forEach(filter -> filter.apply(moments, momentFilterDto));

        assertEquals(1, moments.size());
    }
}