package faang.school.projectservice.service;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

import static org.hibernate.Hibernate.map;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> internshipFilters;

    public InternshipDto getInternshipById(Long internshipId) {
        return internshipRepository.findById(internshipId)
                .map(internshipMapper::toInternshipDto)
                .orElseThrow(() -> new NotFoundException("Internship not found"));

    }

    public List<InternshipDto> getAllInternships() {
        return internshipRepository.findAll().stream()
                .map(internshipMapper::toInternshipDto)
                .toList();
    }

    public List<InternshipDto> getInternshipsFiltered(InternshipFilterDto filterDto) {
        Stream<Internship> internshipsStream = internshipRepository.findAll().stream();
        for (InternshipFilter internshipFilter : internshipFilters) {
            if (internshipFilter.isApplicable(filterDto)) {
                internshipsStream = internshipFilter.apply(internshipsStream, filterDto);
            }
        }
        return internshipsStream.map(internshipMapper::toInternshipDto).toList();
    }

}
