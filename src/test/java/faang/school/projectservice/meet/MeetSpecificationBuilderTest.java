package faang.school.projectservice.meet;

import faang.school.projectservice.model.dto.MeetFilterDto;
import faang.school.projectservice.model.entity.Meet;
import faang.school.projectservice.specification.MeetSpecificationBuilder;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MeetSpecificationBuilderTest {

    @Test
    void buildSpecification_WithTitleFilter() {
        MeetFilterDto filterDto = new MeetFilterDto();
        filterDto.setTitle("Team Meeting");

        Root<Meet> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Predicate predicate = mock(Predicate.class);

        when(criteriaBuilder.like(any(), anyString())).thenReturn(predicate);
        when(root.get("title")).thenReturn(Mockito.mock(Path.class));

        Specification<Meet> specification = MeetSpecificationBuilder.buildSpecification(filterDto);

        Predicate resultPredicate = specification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, times(1)).like(any(), eq("%Team Meeting%"));
        assertNotNull(resultPredicate);
    }

    @Test
    void buildSpecification_WithDateRange() {
        MeetFilterDto filterDto = new MeetFilterDto();
        LocalDateTime startDateAfter = LocalDateTime.of(2024, 9, 1, 0, 0);
        filterDto.setStartDateAfter(startDateAfter);
        LocalDateTime endDateBefore = LocalDateTime.of(2024, 10, 1, 0, 0);
        filterDto.setEndDateBefore(endDateBefore);

        Root<Meet> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Predicate predicate = mock(Predicate.class);

        when(criteriaBuilder.greaterThanOrEqualTo(any(), any(LocalDateTime.class))).thenReturn(predicate);
        when(criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), endDateBefore)).thenReturn(predicate);
        when(root.get("title")).thenReturn(Mockito.mock(Path.class));
        when(root.get("startDate")).thenReturn(Mockito.mock(Path.class));
        when(root.get("endDate")).thenReturn(Mockito.mock(Path.class));

        Specification<Meet> specification = MeetSpecificationBuilder.buildSpecification(filterDto);

        Predicate resultPredicate = specification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, times(1)).greaterThanOrEqualTo(any(), eq(startDateAfter));
        verify(criteriaBuilder, times(1)).lessThanOrEqualTo(any(), eq(endDateBefore));
        assertNotNull(resultPredicate);
    }

    @Test
    void buildSpecification_WithMultipleFilters() {
        MeetFilterDto filterDto = new MeetFilterDto();
        filterDto.setTitle("Sync");
        LocalDateTime startDateAfter = LocalDateTime.of(2024, 9, 1, 0, 0);
        filterDto.setStartDateAfter(startDateAfter);
        LocalDateTime endDateBefore = LocalDateTime.of(2024, 10, 1, 0, 0);
        filterDto.setEndDateBefore(endDateBefore);

        Root<Meet> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder criteriaBuilder = mock(CriteriaBuilder.class);
        Predicate predicate = mock(Predicate.class);

        when(criteriaBuilder.like(any(), anyString())).thenReturn(predicate);
        when(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDateAfter)).thenReturn(predicate);
        when(criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), endDateBefore)).thenReturn(predicate);
        when(root.get("title")).thenReturn(Mockito.mock(Path.class));
        when(root.get("startDate")).thenReturn(Mockito.mock(Path.class));
        when(root.get("endDate")).thenReturn(Mockito.mock(Path.class));

        Specification<Meet> specification = MeetSpecificationBuilder.buildSpecification(filterDto);

        Predicate resultPredicate = specification.toPredicate(root, query, criteriaBuilder);

        verify(criteriaBuilder, times(1)).like(any(), eq("%Sync%"));
        verify(criteriaBuilder, times(1)).greaterThanOrEqualTo(any(), eq(startDateAfter));
        verify(criteriaBuilder, times(1)).lessThanOrEqualTo(any(), eq(endDateBefore));
        assertNotNull(resultPredicate);
    }
}
