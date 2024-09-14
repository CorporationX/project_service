package faang.school.projectservice.service;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.validator.intership.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final InternshipValidator internshipValidator;
    private final List<InternshipFilter> internshipFilters;

    public InternshipDto create(InternshipDto internshipDto) {
        internshipValidator.validateInternshipHaveProjectAndInterns(internshipDto);
        internshipValidator.validateInternshipDuration(internshipDto);
        internshipValidator.validateInternshipGotMentor(internshipDto);
        Internship internshipToSave = internshipMapper.toEntity(internshipDto);
        internshipToSave.setStatus(InternshipStatus.IN_PROGRESS);
        internshipToSave.getInterns()
                .forEach(intern -> intern.setRoles(List.of(TeamRole.INTERN)));
        Internship internshipResult = internshipRepository.save(internshipToSave);
        return internshipMapper.toDto(internshipResult);
    }

    public InternshipDto update(InternshipDto internshipDto) {
        Internship internship = internshipValidator.validateInternshipExists(internshipDto.getId());
        if (internship.getEndDate().isBefore(LocalDateTime.now())) {
            internship.setStatus(InternshipStatus.COMPLETED);
            List<TeamMember> membersPassed = internship.getInterns().stream()
                    .filter(teamMember -> teamMember.getStages().stream()
                            .flatMap(stage -> stage.getTasks().stream())
                            .allMatch(task -> task.getStatus() == TaskStatus.DONE))
                    .toList();
            membersPassed.forEach(member -> member.setRoles(List.of(TeamRole.DEVELOPER)));
            internship.setInterns(membersPassed);
        }
        Internship updatedInternship = internshipRepository.save(internship);
        return internshipMapper.toDto(updatedInternship);
    }

    public List<InternshipDto> getFilteredInternship(InternshipFilterDto filterDto) {
        Stream<Internship> internships = internshipRepository.findAll().stream();
        return internshipFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(internships,
                        (stream, filter) -> filter.applyFilter(stream, filterDto),
                        (v1, v2) -> v1)
                .map(internshipMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<InternshipDto> getAllInternship() {
        return internshipRepository.findAll().stream()
                .map(internshipMapper::toDto)
                .toList();
    }

    public InternshipDto getInternship(long id) {
        return internshipMapper.toDto(internshipValidator.validateInternshipExists(id));
    }
}
