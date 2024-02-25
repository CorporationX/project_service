package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.mapper.internship.TeamMemberMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static faang.school.projectservice.model.InternshipStatus.COMPLETED;
import static faang.school.projectservice.model.InternshipStatus.IN_PROGRESS;
import static faang.school.projectservice.model.TaskStatus.DONE;
import static faang.school.projectservice.model.TeamRole.DEVELOPER;
import static faang.school.projectservice.model.TeamRole.INTERN;

@Service
@Slf4j
@RequiredArgsConstructor
public class InternshipServiceImpl implements InternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberMapper teamMemberMapper;
    private final List<InternshipFilter> filters;

    @Transactional
    public InternshipDto createInternship(InternshipDto internshipDto) {
        checkId(internshipDto.getProjectId());
        checkId(internshipDto.getMentorId());
        if (!teamMemberRepository.existsById(internshipDto.getMentorId()))
            throw new DataValidationException("There is no mentor with this id in the team member");

        checkInternsIsNotEmpty(internshipDto);
        checkInternshipDtoDate(internshipDto.getStartDate(), internshipDto.getEndDate());
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.getInterns().forEach(intern -> changeRole(intern, INTERN));
        internship.setStatus(IN_PROGRESS);
        return internshipMapper.toInternshipDto(internshipRepository.save(internship));
    }

    @Transactional
    public InternshipDto addNewIntern(long internshipId, long teamMemberId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new IllegalArgumentException("Internship not found with id: " + internshipId));
        if (LocalDateTime.now().isAfter(internship.getStartDate()))
            throw new IllegalArgumentException("The internship has already started");
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId);
        internship.getInterns().add(teamMember);
        return internshipMapper.toInternshipDto(internshipRepository.save(internship));
    }

    @Transactional
    public InternshipDto finishInternPrematurely(long internshipId, long teamMemberId) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId);
        changeRole(teamMember, DEVELOPER);
        return updateInterns(internshipId, teamMember);
    }

    @Transactional
    public InternshipDto removeInternPrematurely(long internshipId, long internId) {
        Internship internship = internshipRepository.findById(internId)
                .orElseThrow(() -> new IllegalArgumentException("Internship not found with id: " + internshipId));
        TeamMember intern = searchInternInInternship(internship, internId);
        removeRole(intern, INTERN);
        internship.getInterns().remove(intern);
        return internshipMapper.toInternshipDto(internshipRepository.save(internship));
    }

    @Transactional
    public InternshipDto updateInternship(InternshipDto updatedInternshipDto) {
        Internship internship = internshipRepository.findById(updatedInternshipDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Internship not found with id: " + updatedInternshipDto.getId()));
        if (internship.equals(internshipMapper.toEntity(updatedInternshipDto)))
            throw new DataValidationException("There are no fields to update");
        internship = updateInternshipFields(internship, updatedInternshipDto);
        return internshipMapper.toInternshipDto(internshipRepository.save(internship));
    }

    @Transactional
    public InternshipDto updateInternshipAfterEndDate(long internshipId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new IllegalArgumentException("Internship not found with id: " + internshipId));
        if (LocalDateTime.now().isBefore(internship.getEndDate()))
            throw new IllegalArgumentException("The internship is not over");
        internship.getInterns()
                .removeIf(intern -> {
                    if (checkAllTasksDone(intern)) {
                        changeRole(intern, DEVELOPER);
                        return false;
                    }
                    return true;
                });
        internship.setStatus(COMPLETED);
        return internshipMapper.toInternshipDto(internshipRepository.save(internship));
    }


    @Transactional
    public List<InternshipDto> getInternshipByStatus(InternshipFilterDto status) {
        Stream<Internship> internshipStream = internshipRepository.findAll().stream();
        return filters.stream()
                .filter(internshipFilter -> internshipFilter.isApplicable(status))
                .flatMap(internshipFilter -> internshipFilter.apply(internshipStream, status))
                .map(internshipMapper::toInternshipDto)
                .toList();
    }

    public List<InternshipDto> getInternshipByRole(InternshipFilterDto id, TeamRole role) {
        Stream<Internship> internshipStream = internshipRepository.findAll().stream();
        return filters.stream()
                .filter(internshipFilter -> internshipFilter.isApplicable(id))
                .flatMap(internshipFilter -> internshipFilter.apply(internshipStream, id))
                .filter(internship -> internship.getInterns().stream().anyMatch(intern -> intern.getRoles().contains(role)))
                .map(internshipMapper::toInternshipDto)
                .toList();
    }

    public List<InternshipDto> getAllInternship() {
        return internshipRepository.findAll().stream()
                .map(internshipMapper::toInternshipDto)
                .toList();
    }

    public InternshipDto getById(long id) {
        return internshipMapper.toInternshipDto(internshipRepository.getReferenceById(id));
    }

    private Internship updateInternshipFields(Internship internship, InternshipDto internshipDto) {
        if (internshipDto.getMentorId() != null)
            internship.setMentorId(teamMemberRepository.findById(internshipDto.getMentorId()));
        if (internshipDto.getInterns() != null)
            internship.setInterns(internshipDto.getInterns());
        if (internshipDto.getStartDate() != null) {
            checkInternshipDtoDate(internshipDto.getStartDate(), internship.getEndDate());
            internship.setStartDate(internshipDto.getStartDate());
        }
        if (internshipDto.getEndDate() != null) {
            checkInternshipDtoDate(internship.getStartDate(), internshipDto.getEndDate());
            internship.setEndDate(internshipDto.getEndDate());
        }
        return internship;
    }

    private TeamMember searchInternInInternship(Internship internship, long teamMemberId) {
        return internship.getInterns().stream()
                .filter(teamMember -> teamMember.getId().equals(teamMemberId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Intern not found in the internship"));
    }

    private void removeRole(TeamMember intern, TeamRole role) {
        if (!intern.getRoles().isEmpty())
            intern.getRoles().remove(role);
    }

    private InternshipDto updateInterns(long internshipId, TeamMember teamMember) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new IllegalArgumentException("Internship not found with id: " + internshipId));
        internship.getInterns().stream()
                .filter(intern -> intern.getId().equals(teamMember.getId()))
                .findFirst()
                .ifPresent(intern -> {
                    intern.setRoles(teamMember.getRoles());
                });
        return internshipMapper.toInternshipDto(internshipRepository.save(internship));
    }

    private boolean checkAllTasksDone(TeamMember intern) {
        return intern.getStages().stream()
                .flatMap(stage -> stage.getTasks().stream())
                .allMatch(task -> task.getStatus().equals(DONE));
    }

    private void changeRole(TeamMember teamMember, TeamRole role) {
        List<TeamRole> roles = teamMember.getRoles();
        if (roles == null)
            roles = new ArrayList<>();
        roles.remove(INTERN);
        roles.add(role);
        teamMember.setRoles(roles);
    }

    private void checkId(Long id) {
        if (id == null || id < 1)
            throw new IllegalArgumentException("Invalid id");
    }

    private void checkInternsIsNotEmpty(InternshipDto internshipDto) {
        List<TeamMember> interns = internshipDto.getInterns();
        if (interns == null || interns.isEmpty())
            throw new IllegalArgumentException("Interns list cannot be empty");
    }

    private void checkInternshipDtoDate(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null)
            throw new DataValidationException("Invalid dates");
        if (startDate.isAfter(endDate))
            throw new IllegalArgumentException("Incorrect dates have been entered");
        Duration duration = Duration.between(startDate, endDate);
        if (duration.toDays() > 91)
            throw new DataValidationException("Internship duration cannot exceed 91 days");
    }
}