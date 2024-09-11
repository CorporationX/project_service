package faang.school.projectservice.service.subproject.filters;

import faang.school.projectservice.dto.client.subproject.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@AllArgsConstructor
class SubProjectNameFilterTest {
    private SubProjectNameFilter filter;
    private SubProjectFilterDto filterDto;

    @BeforeEach
    void setUp() {
        filter = new SubProjectNameFilter();
        filterDto = new SubProjectFilterDto();
    }


    @Test
    void isApplicableTrue() {
        filterDto.setName("test");
        assertDoesNotThrow(() -> filter.isApplicable(filterDto));
    }

    @Test
    void isApplicableFalse() {
        assertFalse(filter.isApplicable(filterDto));
    }

    @Test
    void apply() {
        List<Project> projects = IntStream.range(0, 3)
                .mapToObj(i -> new Project())
                .toList();
        projects.get(0).setName("test1");
        projects.get(1).setName("testName");
        projects.get(2).setName("testName2");
        filterDto.setName("testName");


        List<Project> filteredProjects = filter.apply(projects.stream(), filterDto).toList();

        assertAll(
                () -> assertEquals(2, filteredProjects.size()),
                () -> assertEquals(filterDto.getName(), filteredProjects.get(0).getName())
        );
    }


//       requestFilterDto.setStatusPattern(RequestStatus.PENDING);
//    Stream<RecommendationRequest> recommendationRequests = getRecommendationRequestStream();
//
//    List<RecommendationRequest> resultRecommendationRequests = recommendationRequestStatusFilter.apply(recommendationRequests, requestFilterDto).toList();
//
//        Assertions.assertAll(
//                () -> assertEquals(resultRecommendationRequests.get(0).getStatus(), requestFilterDto.getStatusPattern()),
//            () -> assertEquals(1, resultRecommendationRequests.size())
//            );
}