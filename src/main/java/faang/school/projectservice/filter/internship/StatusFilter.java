package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StatusFilter implements InternshipFilter {

    @Override
    public boolean isAcceptable(InternshipFilterDto internshipFilterDto) {
        return internshipFilterDto.getStatus() != null;
    }

    @Override
    public Stream<Internship> applyFilter(Stream<Internship> internships, InternshipFilterDto internshipFilterDto) {
        try {
            InternshipStatus filterStatus = InternshipStatus.valueOf(internshipFilterDto.getStatus());
            return internships.filter(internship -> internship.getStatus().equals(filterStatus));
        } catch (IllegalArgumentException e) {
            throw new DataValidationException("Incorrect filter value");
        }
    }
}
