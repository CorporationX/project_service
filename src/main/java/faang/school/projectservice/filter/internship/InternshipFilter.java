package faang.school.projectservice.filter.internship;

import faang.school.projectservice.model.Internship;

public interface InternshipFilter {
    boolean isApplicable(InternshipFilterDto filters);

//    Stream<Event> apply(Stream<Event> eventStream, EventFilterDto filters);
    boolean apply(Internship event, InternshipFilterDto filters);
}
