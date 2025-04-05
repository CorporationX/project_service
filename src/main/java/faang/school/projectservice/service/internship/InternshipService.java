package faang.school.projectservice.service.internship;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipParticipantUpdateDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;
    private final InternshipMapper internshipMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberService teamMemberService;
    private final UserContext userContext;

    public String PROJECT_NOT_FOUND = "Project not found";
    public String MENTOR_NOT_FOUND = "Mentor not found";
    public String MENTOR_IS_INTERN = "Mentor cannot be an intern";
    public String START_DATE_AFTER_END_DATE = "Start date cannot be after end date";
    public String INTERNSHIP_TO_LONG = "Internship cannot last more than 3 months";
    public String MENTOR_AND_INTERNS_NOT_IN_SAME_TEAM = "Mentor and interns must be in the same team";
    public String INTERN_ID_NOT_PROVIDED = "Intern ID is required";
    public String INTERN_NOT_FOUND = "Intern not found";
    public String COMPLETED_INTERNSHIP = "Cannot update completed internship";
    public String PRESENT_UNFINISHED_TASKS = "Intern cannot change role while having unfinished tasks";
    public String TRYING_TO_ADD_NEW_INTERN = "Trying to add new intern to existing internship";
    public String PROJECT_NOT_ACTIVE = "Project is not active";


    public Internship createInternship(InternshipDto internshipDto) {
        validateInternshipCreateDto(internshipDto);
        log.info("Creating internship: {}", internshipDto);
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setCreatedBy(userContext.getUserId());
        log.info("Internship entity: {}", internship);

        return internshipRepository.save(internship);
    }

    public Internship getById(Long id) {
        return internshipRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Internship not found"));
    }

    public List<Internship> getAll(InternshipStatus status) {
        return internshipRepository.findAll(status);
    }

    public Internship updateInternship(Long id, InternshipUpdateDto internshipUpdateDto) {
        Internship internship = getById(id);
        validateInternshipUpdate(internshipUpdateDto);

        return internshipRepository.save(internship);
    }

    private void validateProjectExist(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(PROJECT_NOT_FOUND));

        if (project.getStatus() == ProjectStatus.CANCELLED
        || project.getStatus() == ProjectStatus.COMPLETED
        || project.getStatus() == ProjectStatus.ON_HOLD) {
            throw new IllegalArgumentException(PROJECT_NOT_ACTIVE);
        }
    }

    private void validateMentorNotIntern(Long mentorId, List<Long> internIds) {
        if (internIds.contains(mentorId)) {
            throw new IllegalArgumentException(MENTOR_IS_INTERN);
        }
    }

    private void validateStartDateAfterEndDate(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(START_DATE_AFTER_END_DATE);
        }
    }

    private void validateInternshipToLong(LocalDateTime startDate, LocalDateTime endDate) {
        if (endDate.isAfter(startDate.plusMonths(3))) {
            throw new IllegalArgumentException(INTERNSHIP_TO_LONG);
        }
    }

    private TeamMember getMentor(Long mentorId) {
        List<TeamMember> mentors = teamMemberRepository.findByUserId(mentorId);
        if (teamMemberRepository.findByUserId(mentorId).isEmpty()) {
            throw new IllegalArgumentException(MENTOR_NOT_FOUND);
        }

        return mentors.get(0);
    }

    private List<TeamMember> getInterns(List<Long> internIds) {
        List<TeamMember> interns = teamMemberRepository.findAllByUserIdIn(internIds);
        if (interns.size() != internIds.size()) {
            throw new IllegalArgumentException(INTERN_NOT_FOUND);
        }

        return interns;
    }

    private void validateMentorAndInternsInSameTeam(Team mentorTeam, List<TeamMember> interns) {
        for (TeamMember intern : interns) {
            if (!mentorTeam.equals(intern.getTeam())) {
                throw new IllegalArgumentException(MENTOR_AND_INTERNS_NOT_IN_SAME_TEAM);
            }
        }
    }

    private void validateInternshipCreateDto(InternshipDto internshipDto) {
        Long mentorId = internshipDto.getMentorId();
        List<Long> internIds = internshipDto.getInternIds();
        Long projectId = internshipDto.getProjectId();

        validateProjectExist(projectId);
        validateMentorNotIntern(mentorId, internIds);
        validateStartDateAfterEndDate(internshipDto.getStartDate(), internshipDto.getEndDate());
        validateInternshipToLong(internshipDto.getStartDate(), internshipDto.getEndDate());

        TeamMember mentor = getMentor(mentorId);
        List<TeamMember> interns = getInterns(internIds);

        Team mentorTeam = mentor.getTeam();
        validateMentorAndInternsInSameTeam(mentorTeam, interns);
    }

    private void validateInternshipNotCompleted(Internship internship) {
        if (internship.getStatus() == InternshipStatus.COMPLETED) {
            throw new IllegalArgumentException(COMPLETED_INTERNSHIP);
        }
    }

    private void validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        validateStartDateAfterEndDate(startDate, endDate);
        validateInternshipToLong(startDate, endDate);
    }

    private void updateInternshipFields(Internship internship, InternshipUpdateDto internshipUpdateDto) {

        if (internshipUpdateDto.getMentorId() != null) {
            internship.setMentor(getMentor(internshipUpdateDto.getMentorId()));
        }

        if (internshipUpdateDto.getDescription() != null) {
            internship.setDescription(internshipUpdateDto.getDescription());
        }

        if (internshipUpdateDto.getName() != null) {
            internship.setName(internshipUpdateDto.getName());
        }

        if (internshipUpdateDto.getScheduleId() != null) {
            Schedule newSchedule = new Schedule();
            newSchedule.setId(internshipUpdateDto.getScheduleId());
            internship.setSchedule(newSchedule);
        }

        if (internshipUpdateDto.getEndDate() != null) {
            validateDates(internship.getStartDate(), internshipUpdateDto.getEndDate());
            internship.setEndDate(internshipUpdateDto.getEndDate());
        }

        if (internshipUpdateDto.getStatus() != null) {
            internship.setStatus(internshipUpdateDto.getStatus());
        }

        if (internshipUpdateDto.getInterns() != null) {
            processInternsUpdate(internship, internshipUpdateDto.getInterns());
        }
    }

    private void validateInternshipUpdate(InternshipUpdateDto internshipUpdateDto) {
        Internship internship = getById(internshipUpdateDto.getId());

        validateInternshipNotCompleted(internship);
        updateInternshipFields(internship, internshipUpdateDto);
    }

    private Optional<TeamMember> getInternById(Long internId) {
        if (internId == null) {
            throw new DataValidationException(INTERN_ID_NOT_PROVIDED);
        }

        return Optional.ofNullable(teamMemberRepository.findById(internId)
                .orElseThrow(() -> new EntityNotFoundException(INTERN_NOT_FOUND)));
    }

    private void validateExistingIntern(Internship internship, Long internId) {
        if (internship.getInterns().stream().noneMatch(currentIntern -> currentIntern.getUserId().equals(internId))) {
            throw new DataValidationException(TRYING_TO_ADD_NEW_INTERN);
        }
    }

    private void ensureInternCompletedAllTasks(InternshipParticipantUpdateDto intern, Internship internship, Long internId) {
        if (intern.getPassed()) {
            List<Task> tasks = internship.getProject().getTasks();
            boolean hasUnfinishedTasks = tasks.stream()
                    .anyMatch(task -> task.getPerformerUserId().equals(internId)
                            && task.getStatus() != TaskStatus.DONE);
            if (hasUnfinishedTasks) {
                throw new DataValidationException(PRESENT_UNFINISHED_TASKS);
            }
        }
    }

    private void updateInternRoles(InternshipParticipantUpdateDto intern, Optional<TeamMember> internTeamMember) {
        if (!intern.getPassed()) {
            internTeamMember.ifPresent(member -> member.getRoles().remove(TeamRole.INTERN));
        }

        if (intern.getNewRole() != null) {
            teamMemberService.updateInternRole(intern.getInternId(), intern.getNewRole());
        }
    }

    private void processInternsUpdate(Internship internship, List<InternshipParticipantUpdateDto> interns) {
        for (InternshipParticipantUpdateDto intern : interns) {
            Long internId = intern.getInternId();
            Optional<TeamMember> internTeamMember = getInternById(internId);

            validateExistingIntern(internship, internId);
            ensureInternCompletedAllTasks(intern, internship, internId);
            updateInternRoles(intern, internTeamMember);
            internTeamMember.ifPresent(teamMemberRepository::save);
        }
    }
}
