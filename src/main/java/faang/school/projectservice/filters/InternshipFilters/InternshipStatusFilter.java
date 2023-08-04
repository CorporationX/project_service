package faang.school.projectservice.filters.InternshipFilters;

import faang.school.projectservice.dto.client.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public class InternshipStatusFilter implements InternshipFilter {

    @Override
    public boolean isInternshipDtoValid(InternshipFilterDto internshipFilterDto) {
        return internshipFilterDto.getStatus() != null;
    }

    @Override
    public Stream<Internship> filterInternshipDto(Stream<Internship> internshipStream, InternshipFilterDto internshipFilterDto) {
        return internshipStream.filter(internship ->
                internship.getStatus().equals(internshipFilterDto.getStatus()));
    }
}
