package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.filter.FilterDto;
import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

class InternshipRoleFilter implements Filter<InternshipFilterDto, Internship> {

    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getRole() != null;
    }

    @Override
    public Stream<Internship> applyFilter(Stream<Internship> internships, InternshipFilterDto filters) {
        return internships.filter(internship -> internship.getInterns()
                .stream()
                .flatMap(intern -> intern.getRoles().stream())
                .allMatch(role -> role == filters.getRole()));
    }
}
