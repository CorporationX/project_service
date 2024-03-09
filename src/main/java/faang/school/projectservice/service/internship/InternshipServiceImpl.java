package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.exeptions.DataValidationException;
import faang.school.projectservice.exeptions.EntityNotFoundException;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.mapper.internship.TeamMemberMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validation.InternshipValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    private final InternshipValidator internshipValidator;


    @Transactional
    public InternshipDto createInternship(InternshipDto internshipDto) {
        checkId(internshipDto.getProjectId());
        checkId(internshipDto.getMentorId());
        if (!teamMemberRepository.existsById(internshipDto.getMentorId())) {
            log.error("There is no mentor with this id in the team member");
            throw new DataValidationException("There is no mentor with this id in the team member");
        }
        checkInternsIsNotEmpty(internshipDto);
        internshipValidator.checkInternshipDtoDate(internshipDto.getStartDate(), internshipDto.getEndDate());
        Internship internship = internshipMapper.toEntity(internshipDto);
        List<TeamMember> interns = internshipDto.getInternsId().stream()
                .map(teamMemberRepository::findById)
                .peek(teamMember -> changeRole(teamMember, INTERN))
                .toList();
        internship.setInterns(interns);
        internshipDto.getInternsId().forEach(id -> changeRole(teamMemberRepository.findById(id), INTERN));
        internship.setStatus(IN_PROGRESS);
        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    @Transactional
    public InternshipDto addNewIntern(long internshipId, long teamMemberId) {
        Internship internship = getById(internshipId);
        if (LocalDateTime.now().isAfter(internship.getStartDate())) {
            log.error("The internship has already started");
            throw new DataValidationException("The internship has already started");
        }
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId);
        internship.getInterns().add(teamMember);
        return internshipMapper.toDto(internship);
    }

    @Transactional
    public InternshipDto finishInternPrematurely(long internshipId, long teamMemberId) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberId);
        changeRole(teamMember, DEVELOPER);
        Internship internship = updateInterns(internshipId, teamMember);
        return internshipMapper.toDto(internship);
    }

    @Transactional
    public InternshipDto removeInternPrematurely(long internshipId, long internId) {
        Internship internship = getById(internshipId);
        TeamMember intern = searchInternInInternship(internship, internId);
        removeRole(intern, INTERN);
        internship.getInterns().remove(intern);
        return internshipMapper.toDto(internship);
    }

    @Transactional
    public InternshipDto updateInternship(InternshipDto updatedInternshipDto) {
        Internship internship = getById(updatedInternshipDto.getId());
        if (internship.equals(internshipMapper.toEntity(updatedInternshipDto)))
            throw new DataValidationException("There are no fields to update");
        internship = updateInternshipFields(internship, updatedInternshipDto);
        return internshipMapper.toDto(internship);
    }

    @Transactional
    public InternshipDto updateInternshipAfterEndDate(long internshipId) {
        Internship internship = getById(internshipId);
        if (LocalDateTime.now().isBefore(internship.getEndDate()))
            throw new DataValidationException("The internship is not over");
        internship.getInterns()
                .removeIf(intern -> {
                    if (checkAllTasksDone(intern)) {
                        changeRole(intern, DEVELOPER);
                        return false;
                    }
                    return true;
                });
        internship.setStatus(COMPLETED);
        return internshipMapper.toDto(internship);
    }


    @Transactional
    public List<InternshipDto> getInternshipByStatus(InternshipFilterDto status) {
        Stream<Internship> internshipStream = internshipRepository.findAll().stream();
        return filters.stream()
                .filter(internshipFilter -> internshipFilter.isApplicable(status))
                .flatMap(internshipFilter -> internshipFilter.apply(internshipStream, status))
                .map(internshipMapper::toDto)
                .toList();
    }

    public List<InternshipDto> getInternshipByRole(InternshipFilterDto internshipFilterDto, TeamRole role) {
        Stream<Internship> internshipStream = internshipRepository.findAll().stream();
        return filters.stream()
                .filter(internshipFilter -> internshipFilter.isApplicable(internshipFilterDto))
                .flatMap(internshipFilter -> internshipFilter.apply(internshipStream, internshipFilterDto))
                //.filter(internship -> internship.getInterns().stream().anyMatch(intern -> intern.getRoles().contains(role)))
                .map(internshipMapper::toDto)
                .toList();
    }

    public List<InternshipDto> getAllInternship() {
        return internshipRepository.findAll().stream()
                .map(internshipMapper::toDto)
                .toList();
    }

    public InternshipDto getDtoById(long id) {
        return internshipMapper.toDto(internshipRepository.getReferenceById(id));
    }

    private Internship getById(long id) {
        return internshipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Internship not found with id: " + id));
    }

    private Internship updateInternshipFields(Internship internship, InternshipDto internshipDto) {
        if (internshipDto.getMentorId() != null)
            internship.setMentorId(teamMemberRepository.findById(internshipDto.getMentorId()));
        if (internshipDto.getInternsId() != null)
            internship.setInterns(internshipDto.getInternsId().stream()
                    .map(teamMemberRepository::findById)
                    .toList());
        if (internshipDto.getStartDate() != null) {
            internshipValidator.checkInternshipDtoDate(internshipDto.getStartDate(), internship.getEndDate());
            internship.setStartDate(internshipDto.getStartDate());
        }
        if (internshipDto.getEndDate() != null) {
            internshipValidator.checkInternshipDtoDate(internship.getStartDate(), internshipDto.getEndDate());
            internship.setEndDate(internshipDto.getEndDate());
        }
        return internship;
    }

    private TeamMember searchInternInInternship(Internship internship, long teamMemberId) {
        return internship.getInterns().stream()
                .filter(teamMember -> teamMember.getId().equals(teamMemberId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Intern not found in the internship"));
    }

    private void removeRole(TeamMember intern, TeamRole role) {
        if (!intern.getRoles().isEmpty())
            intern.getRoles().remove(role);
    }

    private Internship updateInterns(long internshipId, TeamMember teamMember) {
        Internship internship = getById(internshipId);
        internship.getInterns().stream()
                .filter(intern -> intern.getId().equals(teamMember.getId()))
                .findFirst()
                .ifPresent(intern -> {
                    intern.setRoles(teamMember.getRoles());
                });
        return internship;
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
        List<TeamMember> interns = internshipDto.getInternsId().stream()
                .map(teamMemberRepository::findById)
                .toList();
        if (interns.isEmpty())
            throw new IllegalArgumentException("Interns list cannot be empty");
    }
}