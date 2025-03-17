package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InternshipStatusFilterTest {
    private final InternshipStatusFilter internshipStatusFilter = new InternshipStatusFilter();

    @Test
    public void testIsApplicableTrue() {
        boolean actual = internshipStatusFilter.isApplicable(new InternshipFilterDto(InternshipStatus.IN_PROGRESS,null));
        assertTrue(actual);
    }
    @Test
    public void testIsApplicableFalse() {
        boolean actual = internshipStatusFilter.isApplicable(new InternshipFilterDto(null,null));
        assertFalse(actual);
    }

    @Test
    public void testApply() {
        Stream<Internship> internships = Stream.of(
                Internship.builder().status(InternshipStatus.IN_PROGRESS).build(),
                Internship.builder().status(InternshipStatus.COMPLETED).build());

        List<Internship> list = internshipStatusFilter
                .apply(internships,new InternshipFilterDto(InternshipStatus.IN_PROGRESS,null)).toList();
        assertEquals(1, list.size());
        assertEquals(InternshipStatus.IN_PROGRESS, list.get(0).getStatus());
    }

    @Test
    public void testApply2() {
        Stream<Internship> internships = Stream.of(
                Internship.builder().status(InternshipStatus.IN_PROGRESS).build(),
                Internship.builder().status(InternshipStatus.IN_PROGRESS).build());

        List<Internship> list = internshipStatusFilter
                .apply(internships,new InternshipFilterDto(InternshipStatus.IN_PROGRESS,null)).toList();
        assertEquals(2, list.size());
        assertEquals(InternshipStatus.IN_PROGRESS, list.get(0).getStatus());
        assertEquals(InternshipStatus.IN_PROGRESS, list.get(1).getStatus());
    }

    @Test
    public void testApply3() {
        Stream<Internship> internships = Stream.of(
                Internship.builder().status(InternshipStatus.IN_PROGRESS).build(),
                Internship.builder().status(InternshipStatus.IN_PROGRESS).build());

        List<Internship> list = internshipStatusFilter
                .apply(internships,new InternshipFilterDto(InternshipStatus.COMPLETED,null)).toList();
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());

    }
}
