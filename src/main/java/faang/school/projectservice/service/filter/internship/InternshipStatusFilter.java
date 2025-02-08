package faang.school.projectservice.service.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterRequest;
import faang.school.projectservice.model.Internship;

import java.util.stream.Stream;

public class InternshipStatusFilter implements InternshipFilter{
    @Override
    public Stream<Internship> filter(Stream<Internship> stream, InternshipFilterRequest request) {
        return request.status() == null
                ? stream
                : stream.filter(internship -> internship.getStatus().equals(request.status()));
    }
}
