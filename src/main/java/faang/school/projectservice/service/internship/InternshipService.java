package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.filter.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.mapper.teammember.TeamMemberMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import faang.school.projectservice.validator.intership.InternshipValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final InternshipValidator internshipValidator;
    private final List<Filter<InternshipFilterDto, Internship>> internshipFilters;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;
    private final TeamMemberMapper teamMemberMapper;

    public InternshipDto create(InternshipDto internshipDto) {

        internshipValidator.validateInternship(internshipDto);
        Project project = projectService.getProjectById(internshipDto.getProjectId());
        TeamMemberDto mentor = teamMemberService.getTeamMemberById(internshipDto.getMentorId().getId());
        internshipValidator.validateInternshipProjectAndMentorExist(project, teamMemberMapper.toEntity(mentor));

        Internship internshipToSave = internshipMapper.toEntity(internshipDto);
        internshipToSave.setStatus(InternshipStatus.IN_PROGRESS);
        internshipToSave.getInterns()
                .forEach(intern -> intern.setRoles(List.of(TeamRole.INTERN)));
        Internship internshipResult = internshipRepository.save(internshipToSave);
        return internshipMapper.toDto(internshipResult);
    }

    public InternshipDto update(InternshipDto internshipDto) {
        Internship internship = internshipRepository.findById(internshipDto.getId()).orElseThrow();

        if (internship.getEndDate().isBefore(LocalDateTime.now())) {
            internship.setStatus(InternshipStatus.COMPLETED);

            internship.getInterns().removeIf(member ->
                    member.getStages().stream()
                            .flatMap(stage -> stage.getTasks().stream())
                            .anyMatch(task -> task.getStatus() != TaskStatus.DONE));

            internship.getInterns().forEach(member ->
                    member.setRoles(List.of(TeamRole.DEVELOPER)));
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
                .toList();
    }

    public List<InternshipDto> getAllInternship() {
        return internshipRepository.findAll().stream()
                .map(internshipMapper::toDto)
                .toList();
    }

    public InternshipDto getInternshipById(long id) {
        Optional<Internship> internshipToFind = internshipRepository.findById(id);
        if (internshipToFind.isPresent()) {
            return internshipMapper.toDto(internshipToFind.get());
        } else {
            log.error("Cannot find internship with id {}", id);
            throw new EntityNotFoundException("Internship doesn't exist!");
        }
    }
}
