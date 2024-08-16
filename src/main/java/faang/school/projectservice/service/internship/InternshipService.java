package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.internship.InternshipValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InternshipValidator internshipValidator;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> internshipFilters;

    @Transactional
    public InternshipDto createInternship(InternshipDto internshipDto) {
        internshipValidator.validateInternship(internshipDto);
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setStatus(InternshipStatus.IN_PROGRESS);
        internshipRepository.save(internship);
        return internshipMapper.toDto(internship);
    }

    @Transactional
    public InternshipDto updateInternship(Long id, InternshipDto internshipDto) {
        Internship internship = internshipRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Internship not found"));
        internshipValidator.validateInternship(internshipDto);
        if (internship.getStatus() == InternshipStatus.IN_PROGRESS) {
            internship.setStatus(InternshipStatus.valueOf(internshipDto.getStatus()));
        }
        if (internship.getStatus() == InternshipStatus.COMPLETED) {
            handleCompletedInternship(internship);
        }
        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    @Transactional(readOnly = true)
    public List<InternshipDto> getInternshipByFilter(InternshipFilterDto internshipFilterDto) {
        List<Internship> internships = internshipRepository.findAll();
        return internshipFilters.stream()
                .filter(filter -> filter.isApplicable(internshipFilterDto))
                .reduce(internships.stream(), (stream, filter) -> filter.apply(stream, internshipFilterDto), Stream::concat)
                .map(internshipMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InternshipDto> getAllInternships() {
        return internshipRepository.findAll().stream()
                .map(internshipMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public InternshipDto getInternshipById(Long id) {
        return internshipMapper.toDto(internshipRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Internship not found")));
    }

    private void handleCompletedInternship(Internship internship) {
        Project project = internship.getProject();
        boolean allTasksCompleted = project.getTasks().stream().allMatch(task -> task.getStatus() == TaskStatus.DONE);
        if (allTasksCompleted) {
            promoteInterns(internship);
        } else {
            removeInternsFromInternship(internship);
        }
    }

    private void promoteInterns(Internship internship) {
        internship.getInterns().forEach(intern -> {
            if (intern.getRoles().contains(TeamRole.INTERN)) {
                intern.getRoles().remove(TeamRole.INTERN);
                intern.getRoles().add(TeamRole.DEVELOPER);
                teamMemberRepository.save(intern);
            }
        });
    }

    private void removeInternsFromInternship(Internship internship) {
        List<TeamMember> internsToRemove = new ArrayList<>(internship.getInterns());
        internsToRemove.forEach(intern -> {
            internship.getInterns().remove(intern);
            teamMemberRepository.save(intern);
        });
        internshipRepository.save(internship);
    }
}
