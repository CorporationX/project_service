package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class StatusFilterTest {
    private static final InternshipStatus STATUS = InternshipStatus.COMPLETED;

    private final StatusFilter statusFilter = new StatusFilter();

    @Test
    void testIsApplicableIsTrue() {
        InternshipFilterDto filter = InternshipFilterDto.builder()
                .status(STATUS)
                .build();

        assertTrue(statusFilter.isApplicable(filter));
    }

    @Test
    void testIsApplicableIsFalse() {
        InternshipFilterDto filter = InternshipFilterDto.builder().build();

        assertFalse(statusFilter.isApplicable(filter));
    }

    @Test
    void testApplyIsSuccess() {
        InternshipFilterDto filter = InternshipFilterDto.builder()
                .status(STATUS)
                .build();

        Internship internship = Internship.builder()
                .status(STATUS)
                .build();

        Stream<Internship> internshipStream = List.of(internship).stream();
        Stream<Internship> result = statusFilter.apply(internshipStream, filter);

        assertFalse(result.toList().isEmpty());
    }

    @Test
    void testApplyIsNotSuccess() {
        InternshipFilterDto filter = InternshipFilterDto.builder()
                .status(STATUS)
                .build();

        Internship internship = Internship.builder()
                .status(InternshipStatus.IN_PROGRESS)
                .build();

        Stream<Internship> internshipStream = List.of(internship).stream();
        Stream<Internship> result = statusFilter.apply(internshipStream, filter);

        assertTrue(result.toList().isEmpty());
    }
}