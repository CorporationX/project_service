package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.exception.ConstraintViolation;
import faang.school.projectservice.exception.MessageError;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.filter.internship.InternshipFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipUpdateLogicService internshipNewLogicService;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> internshipFilters;


    @Transactional
    public InternshipDto create(InternshipDto internshipDto) {
        Internship internship = internshipMapper.toEntity(internshipDto);
        internshipRepository.save(internship);
        return internshipMapper.toDto(internship);
    }

    @Transactional
    public InternshipDto update(InternshipDto internshipDto) {
        return internshipNewLogicService.updateLogic(internshipDto);
    }

    public List<InternshipDto> getFilteredInternships(InternshipFilterDto filters) {
        List<Internship> internships = internshipRepository.findAll();

        List<InternshipFilter> applicableFilters = internshipFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .toList();

        return internships.stream()
                .filter(internship -> applicableFilters.stream()
                        .allMatch(internshipFilter -> internshipFilter.apply(internship, filters)))
                .map(internshipMapper::toDto)
                .toList();
    }

    public List<InternshipDto> getAllInternships() {
        return internshipMapper.toDto(internshipRepository.findAll());
    }

    public InternshipDto findById(Long id) {
        Internship internship = internshipRepository.findById(id)
                .orElseThrow(() -> new ConstraintViolation(MessageError.INTERNSHIP_NOT_FOUND_EXCEPTION));
        return internshipMapper.toDto(internship);
    }
}
