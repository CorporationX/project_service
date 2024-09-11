package faang.school.projectservice.service;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipInfoDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.exception.internship.EntityNotFoundException;
import faang.school.projectservice.exception.internship.IncorrectInternshipDurationException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternshipServiceImpl implements InternshipService {
    private final static String NO_INTERNS_MESSAGE = "No interns";
    private final static int maxInternshipDurationMonth = 3;
    private final static String INCORRECT_INTERNSHIP_DURATION_MESSAGE =
            "The internship period cannot be longer than %d months".formatted(maxInternshipDurationMonth);
    private final static String INCORRECT_MENTOR_ID_MESSAGE = "This mentor is not in this project";
    private final InternshipMapper internshipMapper;
    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;

    @Override
    public InternshipDto createInternship(CreateInternshipDto internship) {
        if (!isProjectHaveIntern(internship.getProjectId())) {
            throw new EntityNotFoundException(NO_INTERNS_MESSAGE);
        }
        long internshipDuration = ChronoUnit.MONTHS.between(internship.getStartDate(), internship.getEndDate());
        if (internshipDuration >= maxInternshipDurationMonth) {
            throw new IncorrectInternshipDurationException(INCORRECT_INTERNSHIP_DURATION_MESSAGE);
        }
        if (!isMentorInProject(internship.getProjectId(), internship.getMentorId())) {
            throw new EntityNotFoundException(INCORRECT_MENTOR_ID_MESSAGE);
        }
        Internship newInternship = internshipMapper.createInternshipDtoToInternship(internship);
        internshipRepository.save(newInternship);
        return internshipMapper.internshipToInternshipDto(newInternship);
    }

    @Override
    public void updateInternship(Long internshipId, UpdateInternshipDto updatedInternship) {

    }

    @Override
    public List<InternshipDto> getAllInternshipsOnProject(Long projectId) {
        return
    }

    @Override
    public List<InternshipDto> getAllInternships() {
        return List.of();
    }

    @Override
    public InternshipInfoDto getInternshipById(Long internshipId) {
        return
    }

    private boolean isMentorInProject(Long projectId, TeamMember mentor) {
        return projectRepository.getProjectById(projectId).getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(teamMember -> !teamMember.getRoles().equals(TeamRole.INTERN))
                .anyMatch(teamMember -> teamMember.getId().equals(mentor.getId()));
    }

    private boolean isProjectHaveIntern(Long projectId) {
        return projectRepository.getProjectById(projectId).getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(teamMember -> teamMember.getRoles().equals(TeamRole.INTERN));
    }
}