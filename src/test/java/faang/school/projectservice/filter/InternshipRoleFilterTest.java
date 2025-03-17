package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamRole;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InternshipRoleFilterTest {
    private final InternshipRoleFilter internshipRoleFilter = new InternshipRoleFilter();

    @Test
    public void testIsApplicableTrue() {
        boolean result = internshipRoleFilter.isApplicable(new InternshipFilterDto(null, TeamRole.INTERN));
        assertTrue(result);
    }
    @Test
    public void testIsApplicableFalse() {
        boolean result = internshipRoleFilter.isApplicable(new InternshipFilterDto(null, null));
        assertFalse(result);
    }
    @Test
    public void testApply() {
        Stream<Internship> internshipStream = Stream.of(
                Internship.builder().role(TeamRole.ANALYST).build(),
                Internship.builder().role(TeamRole.INTERN).build());
        List<Internship> internshipList = internshipRoleFilter.apply(internshipStream,
                new InternshipFilterDto(null, TeamRole.INTERN)).toList();

        assertEquals(1, internshipList.size());
        assertEquals(TeamRole.INTERN, internshipList.get(0).getRole());
    }

    @Test
    public void testApply2() {
        Stream<Internship> internshipStream = Stream.of(
                Internship.builder().role(TeamRole.ANALYST).build(),
                Internship.builder().role(TeamRole.ANALYST).build());
        List<Internship> internshipList = internshipRoleFilter.apply(internshipStream,
                new InternshipFilterDto(null, TeamRole.ANALYST)).toList();

        assertEquals(2, internshipList.size());
        assertEquals(TeamRole.ANALYST, internshipList.get(0).getRole());
        assertEquals(TeamRole.ANALYST, internshipList.get(1).getRole());
    }

    @Test
    public void testApply3() {
        Stream<Internship> internshipStream = Stream.of(
                Internship.builder().role(TeamRole.ANALYST).build(),
                Internship.builder().role(TeamRole.INTERN).build());
        List<Internship> internshipList = internshipRoleFilter.apply(internshipStream,
                new InternshipFilterDto(null, TeamRole.DEVELOPER)).toList();

        assertEquals(0, internshipList.size());
    }
}
