package faang.school.projectservice.service.moment.filters;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectPartnerFilterTest {
    private MomentFilterDto momentFilterDto;
    private Moment moment3;
    private Moment moment4;
    private Project project;
    private Project project1;

    @BeforeEach
    void init() {
        momentFilterDto = MomentFilterDto.builder()
                .projects(Arrays.asList())
                .build();
        moment3 = Moment.builder()
                .projects(Arrays.asList(project, project1))
                .build();
        moment4 = Moment.builder()
                .projects(Arrays.asList(project, project1))
                .build();
        project = Project.builder()
                .id(1L)
                .moments(Arrays.asList(moment3, moment4))
                .build();
        project1 = Project.builder()
                .id(2L)
                .moments(Arrays.asList(moment3, moment4))
                .build();
    }

    @Test
    public void isApplicableTestTrue() {
        ProjectPartnerFilter projectPartnerFilter = new ProjectPartnerFilter();
        assertTrue(projectPartnerFilter.isApplicable(momentFilterDto));
    }

    @Test
    public void isApplicableTestFalse() {
        momentFilterDto.setProjects(null);
        ProjectPartnerFilter projectPartnerFilter = new ProjectPartnerFilter();
        assertFalse(projectPartnerFilter.isApplicable(momentFilterDto));
    }

    @Test
    public void applyTest() {
        Stream<Moment> momentStream = Stream.of(moment3, moment4);
        ProjectPartnerFilter projectPartnerFilter = new ProjectPartnerFilter();

        List<Moment> returnedList = projectPartnerFilter.apply(momentStream, momentFilterDto).toList();

        assertEquals(returnedList.size(), 2);
        assertEquals(returnedList.get(0).getProjects(), moment3.getProjects());
        assertFalse(returnedList.contains(project1));
    }
}
