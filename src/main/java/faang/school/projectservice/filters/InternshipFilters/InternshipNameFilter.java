package faang.school.projectservice.filters.InternshipFilters;

import faang.school.projectservice.dto.client.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public class InternshipNameFilter implements InternshipFilter {

    @Override
    public boolean isInternshipDtoValid(InternshipFilterDto internshipFilterDto) {
        return internshipFilterDto.getName() != null;
    }

    @Override
    public Stream<Internship> filterInternshipDto(Stream<Internship> internshipStream, InternshipFilterDto internshipFilterDto) {
        return internshipStream.filter(internship ->
                internship.getName().contains(internshipFilterDto.getName()));
    }
}
