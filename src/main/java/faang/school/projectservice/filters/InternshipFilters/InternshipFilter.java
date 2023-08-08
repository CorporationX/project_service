package faang.school.projectservice.filters.InternshipFilters;

import faang.school.projectservice.dto.client.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public interface InternshipFilter {
    boolean isInternshipDtoValid(InternshipFilterDto internshipFilterDto);

    Stream<Internship> filterInternshipDto(Stream <Internship> internshipStream,
                                           InternshipFilterDto internshipFilterDto);


}
