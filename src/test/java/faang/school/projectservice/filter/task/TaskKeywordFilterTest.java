package faang.school.projectservice.filter.task;

import faang.school.projectservice.dto.task.TaskFilterDto;
import faang.school.projectservice.model.Task;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskKeywordFilterTest {

    private final TaskKeywordFilter filter = new TaskKeywordFilter();
    private final String firstKeyword = "test";

    @Mock
    private Root<Task> root;

    @Mock
    private CriteriaQuery<Task> query;

    @Mock
    private CriteriaBuilder builder;

    @Mock
    private Path<String> namePath;

    @Mock
    private Path<String> descriptionPath;

    @Mock
    private Expression<String> lowerName;

    @Mock
    private Expression<String> lowerDescription;

    @Mock
    private Predicate namePredicate;

    @Mock
    private Predicate descriptionPredicate;

    @Mock
    private Predicate orPredicate;

    @Test
    public void testPositiveApplicable() {
        boolean isApplicable = filter.isApplicable(createFilterDto(firstKeyword));

        assertTrue(isApplicable);
    }

    @Test
    public void testPositiveApplicableWhenFilterNull() {
        boolean isApplicable = filter.isApplicable(createFilterDto(null));

        assertFalse(isApplicable);
    }

    @Test
    public void testPositiveApplicableWhenFilterEmpty() {
        boolean isApplicable = filter.isApplicable(createFilterDto(""));

        assertFalse(isApplicable);
    }

    @Test
    public void testPositiveApplicableWhenFilterBlank() {
        boolean isApplicable = filter.isApplicable(createFilterDto("      "));

        assertFalse(isApplicable);
    }

    @Test
    public void testPositiveApply() {
        setFilterBehavior();
        TaskFilterDto filterDto = createFilterDto(firstKeyword);
        String expectedPattern = "%test%";
        when(builder.like(lowerName, expectedPattern, '\\')).thenReturn(namePredicate);
        when(builder.like(lowerDescription, expectedPattern, '\\')).thenReturn(descriptionPredicate);
        when(builder.or(namePredicate, descriptionPredicate)).thenReturn(orPredicate);

        Predicate result = filter.apply(filterDto).toPredicate(root, query, builder);

        assertEquals(result, orPredicate);
    }

    @Test
    public void testPositiveApplyWithSpecialCharacters() {
        setFilterBehavior();
        TaskFilterDto filterDto = createFilterDto("100%_test\\");
        String expectedPattern = "%100\\%\\_test\\\\%";

        Specification<Task> spec = filter.apply(filterDto);
        spec.toPredicate(root, query, builder);

        verify(builder).like(lowerName, expectedPattern, '\\');
        verify(builder).like(lowerDescription, expectedPattern, '\\');
        verify(builder).or(any(), any());
    }

    @Test
    public void testPositiveApplyWhenDifferentCase() {
        setFilterBehavior();
        TaskFilterDto filterDto = createFilterDto(firstKeyword.toUpperCase());

        filter.apply(filterDto).toPredicate(root, query, builder);

        verify(builder).lower(namePath);
        verify(builder).lower(descriptionPath);
    }

    private TaskFilterDto createFilterDto(String keyword) {
        return TaskFilterDto.builder()
                .keyword(keyword)
                .build();
    }

    private void setFilterBehavior() {
        when(root.<String>get("name")).thenReturn(namePath);
        when(root.<String>get("description")).thenReturn(descriptionPath);
        when(builder.lower(namePath)).thenReturn(lowerName);
        when(builder.lower(descriptionPath)).thenReturn(lowerDescription);
    }
}
