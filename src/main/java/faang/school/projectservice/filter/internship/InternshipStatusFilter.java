package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.filter.internship.InternshipFilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

class InternshipStatusFilter implements Filter<InternshipFilterDto, Internship> {

    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public Stream<Internship> applyFilter(Stream<Internship> internships, InternshipFilterDto filters) {
        return internships.filter(internship -> internship.getStatus() == filters.getStatus());
    }
}