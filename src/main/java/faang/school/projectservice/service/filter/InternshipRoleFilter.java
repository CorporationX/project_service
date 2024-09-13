package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.internship.InternshipFilter;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public class InternshipRoleFilter implements InternshipFilter {

    /*
    Пока не понял, что за фильтр по роли...
     */


    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return false;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internships, InternshipFilterDto filters) {
        return Stream.empty();
    }
}
