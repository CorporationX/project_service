package faang.school.projectservice.filter.internship;

import faang.school.projectservice.model.Internship;

public interface InternshipFilter {
    boolean isApplicable(InternshipFilterDto filters);
    boolean apply(Internship event, InternshipFilterDto filters);
}
