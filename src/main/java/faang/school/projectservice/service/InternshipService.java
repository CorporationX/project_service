package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.InternshipCreateRequest;
import faang.school.projectservice.dto.internship.InternshipFilterRequest;
import faang.school.projectservice.dto.internship.InternshipResponse;
import faang.school.projectservice.dto.internship.InternshipUpdateRequest;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.filter.internship.InternshipFilter;
import faang.school.projectservice.validation.InternshipValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final InternshipValidationService internshipValidationService;
    private final List<InternshipFilter> internshipFilters;

    public void createInternship(InternshipCreateRequest dto) {
        internshipValidationService.validateRequest(dto);
        internshipRepository.save(internshipMapper.toEntity(dto));
    }

    public void updateInternship(InternshipUpdateRequest dto) {
        internshipValidationService.validateRequest(dto);
        Internship internship = internshipRepository.getById(dto.getId());
        if (dto.getEndDate().isBefore(LocalDateTime.now())) {
            List<TeamMember> teamInterns = internship.getInterns();
            teamInterns.stream()
                    .forEach(intern -> intern.setRoles(List.of(dto.getRole())));
        } else {
            internshipMapper.update(dto, internship);
        }
        internshipRepository.save(internship);
    }

    public List<InternshipResponse> getInternshipsByFilter(@Valid InternshipFilterRequest filterRequest) {
        Stream<Internship> internships = internshipRepository.findAll().stream();

        for (InternshipFilter filter : internshipFilters) {
            internships = filter.filter(internships, filterRequest);
        }

        return internships.map(internshipMapper::toDto).toList();
    }

    public List<InternshipResponse> getAllInternships() {
        return internshipRepository.findAll().stream()
                .map(internship -> internshipMapper.toDto(internship))
                .toList();
    }

    public InternshipResponse getInternshipById(long internshipId) {
        return internshipMapper.toDto(internshipRepository.findById(internshipId)
                .orElseThrow(() -> new NoSuchElementException("Element with id" + internshipId + "dose not exist")));
    }

    public void removeInternFromInternship(List<Long> internIds, long internshipId) {
        removeOrFinishAheadInternshipData(internIds, internshipId, false);
    }

    public void finishTheInternshipAheadOfScheduleFor(List<Long> internIds, long internshipId) {
        removeOrFinishAheadInternshipData(internIds, internshipId, true);
    }

    private void removeOrFinishAheadInternshipData(List<Long> internIds,
                                                   long internshipId,
                                                   boolean finishInternshipAhead) {
        Internship internship = internshipRepository.getById(internshipId);
        if (finishInternshipAhead) {
            internship.getInterns().stream()
                    .forEach(intern -> intern.setRoles(List.of(internship.getRole())));
        }
        List<TeamMember> updatedInternsList = internship.getInterns().stream()
                .filter(intern -> !internIds.contains(intern.getId()))
                .toList();
        internship.setInterns(updatedInternsList);
        internshipRepository.save(internship);
    }
}