package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.exeption.DataValidationException;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberMapper teamMemberMapper;
    private final ProjectRepository projectRepository;
    private final List<InternshipFilter> filters;

    @Transactional
    public InternshipDto createInternship(InternshipDto internshipDto) {
        checkExistenceInterns(internshipDto);
        checkInternshipDtoDate(internshipDto);
        Internship createdInternship = internshipMapper.toEntity(internshipDto);
        //   createdInternship.setProject(projectRepository.getProjectById(internshipDto.getProjectId()));
        //  createdInternship.setMentorId(teamMemberRepository.findById(internshipDto.getMentorId()));
        //  createdInternship.setInterns(getInterns(internshipDto));
        createdInternship.getInterns().forEach(intern -> changeRole(intern, INTERN));
        createdInternship.setStatus(IN_PROGRESS);
        return internshipMapper.toInternshipDto(internshipRepository.save(createdInternship));
    }

    public InternshipDto addNewInterns(InternshipDto internshipDto, TeamMemberDto teamMemberDto) {
        Internship internship = internshipRepository.findById(internshipDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Internship not found with id: " + internshipDto.getId()));
        if (LocalDateTime.now().isAfter(internship.getStartDate()))
            throw new IllegalArgumentException("The internship has already started");
        TeamMember teamMember = teamMemberMapper.toEntity(teamMemberDto);
        internship.getInterns().add(teamMember);
        return internshipMapper.toInternshipDto(internshipRepository.save(internship));
    }

    public InternshipDto finishInterPrematurely(InternshipDto internshipDto, TeamMemberDto teamMemberDto) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberDto.getId());
        changeRole(teamMember, DEVELOPER);
        return updateInterns(internshipDto, teamMember);
    }

    public InternshipDto removeInterPrematurely(InternshipDto internshipDto, TeamMemberDto teamMemberDto) {
        Internship internship = internshipRepository.findById(internshipDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Internship not found with id: " + internshipDto.getId()));
        TeamMember intern = searchInternInInternship(internshipDto, teamMemberDto);
        removeRole(intern);
        internship.getInterns().remove(intern);
        return internshipMapper.toInternshipDto(internshipRepository.save(internship));

    }

    public InternshipDto updateInternship(InternshipDto updatedInternshipDto) {
        Internship existingInternship = internshipRepository.findById(updatedInternshipDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Internship not found with id: " + updatedInternshipDto.getId()));
        if (existingInternship.equals(internshipMapper.toEntity(updatedInternshipDto)))
            throw new DataValidationException("There are no fields to update");
        Internship internship = updateInternshipFields(existingInternship, updatedInternshipDto);
        return internshipMapper.toInternshipDto(internshipRepository.save(internship));
    }


    public InternshipDto updateInternshipAfterEndDate(long idInternshipDto) {
        Internship internship = internshipRepository.getReferenceById(idInternshipDto);
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


    public List<InternshipDto> getInternshipByFilter(InternshipFilterDto filter) {
        Stream<Internship> internshipStream = internshipRepository.findAll().stream();

        return filters.stream()
                .filter(fil -> fil.isApplicable(filter))
                .flatMap(fil -> fil.apply(internshipStream, filter))
                .map(internshipMapper::toInternshipDto)
                .distinct()
                .toList();
    }

    private Internship updateInternshipFields(Internship existingInternship, InternshipDto updatedInternshipDto) {
        if (updatedInternshipDto.getMentorId() != null)
            existingInternship.setMentorId(teamMemberRepository.findById(updatedInternshipDto.getMentorId()));
        if (updatedInternshipDto.getInterns() != null)
            existingInternship.setInterns(updatedInternshipDto.getInterns());
        if (updatedInternshipDto.getStartDate() != null && updatedInternshipDto.getEndDate() != null) {
            checkInternshipDtoDate(updatedInternshipDto);
            existingInternship.setStartDate(updatedInternshipDto.getStartDate());
            existingInternship.setStartDate(updatedInternshipDto.getStartDate());
        }
        return existingInternship;
    }

    private TeamMember searchInternInInternship(InternshipDto internshipDto, TeamMemberDto teamMemberDto) {
        Internship internship = internshipRepository.getReferenceById(internshipDto.getId());
        List<TeamMember> interns = internship.getInterns();

        return interns.stream()
                .filter(teamMember -> teamMember.getId().equals(teamMemberDto.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Intern not found in the internship"));
    }

    private void removeRole(TeamMember intern) {
        List<TeamRole> roles = intern.getRoles();
        roles.remove(INTERN);
        intern.setRoles(roles);
    }

    private InternshipDto updateInterns(InternshipDto internshipDto, TeamMember teamMember) {
        Internship internship = internshipRepository.findById(internshipDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Internship not found with id: " + internshipDto.getId()));
        List<TeamMember> interns = internship.getInterns();
        interns.stream()
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

//    private boolean checkAllTasksTodo(TeamMember intern) {
//        return intern.getStages().stream()
//                .flatMap(stage -> stage.getTasks().stream())
//                .allMatch(task -> task.getStatus().equals(TODO));
//    }

    private void changeRole(TeamMember teamMember, TeamRole role) {
        List<TeamRole> roles = teamMember.getRoles();
        if (roles == null)
            roles = new ArrayList<>();
        roles.remove(INTERN);
        roles.add(role);
        teamMember.setRoles(roles);
    }


    private void checkExistenceInterns(InternshipDto internshipDto) {
        List<TeamMember> interns = internshipDto.getInterns();
        if (interns == null || interns.isEmpty())
            throw new IllegalArgumentException("Interns list cannot be empty");
    }

    private void checkInternshipDtoDate(InternshipDto internshipDto) {
        if (internshipDto.getStartDate() == null || internshipDto.getEndDate() == null)
            throw new NullPointerException("Invalid dates");
        if (internshipDto.getStartDate().isAfter(internshipDto.getEndDate()))
            throw new IllegalArgumentException("Incorrect dates have been entered");
        Duration duration = Duration.between(internshipDto.getStartDate(), internshipDto.getEndDate());
        if (duration.toDays() > 91)
            throw new DataValidationException("Internship duration cannot exceed 91 days");
    }
}