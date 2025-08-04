package faang.school.projectservice.service.internshi;

import faang.school.projectservice.apimodel.InternshipDto;
import faang.school.projectservice.apimodel.InternshipStatus;
import faang.school.projectservice.apimodel.TeamRole;
import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.ProjectService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * InternshipServiceImpl — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>
 *
 * @author agent
 * @since 04.08.2025
 */
@Service
@RequiredArgsConstructor
public class InternshipServiceImpl implements InternshipService {

    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectService projectService;
    private final InternshipMapper internshipMapper;

    @Override
    public InternshipDto createInternship(Long projectId, InternshipDto dto) {
        validateCreateDto(dto);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        validateMentorShip(project, dto);

        Internship internship = internshipMapper.toEntity(dto);
        internship.setProject(project);

        TeamMember mentor = new TeamMember();
        mentor.setId(dto.getMentorId().longValue());
        internship.setMentorId(mentor);

        List<TeamMember> interns = dto.getTraineeIds().stream()
                .map(id -> {
                    TeamMember member = new TeamMember();
                    member.setId(id.longValue());
                    return member;
                })
                .toList();

        internship.setInterns(interns);

        Internship saved = internshipRepository.save(internship);
        return internshipMapper.toDto(saved);
    }

    @Override
    public InternshipDto updateInternship(Long id, InternshipDto dto) {
        Internship existing = internshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Internship not found"));

        validateUpdateDto(existing, dto);

        // Используем маппер для преобразования apimodel.InternshipStatus в модельный статус, если нужно
        existing.setStatus(internshipMapper.map(dto.getStatus()));
        existing.setEndDate(dto.getEndDate().toLocalDateTime());

        if (dto.getStatus() == InternshipStatus.COMPLETED || dto.getStatus() == InternshipStatus.FAILED) {
            applyCompletionLogic(existing);
        }

        Internship saved = internshipRepository.save(existing);
        return internshipMapper.toDto(saved);
    }

    @Override
    public List<InternshipDto> findAll() {
        return internshipRepository.findAll().stream()
                .map(internshipMapper::toDto)
                .toList();
    }

    @Override
    public Optional<InternshipDto> findById(Long id) {
        return internshipRepository.findById(id)
                .map(internshipMapper::toDto);
    }

    @Override
    public List<InternshipDto> findByProject(Long projectId, @Nullable InternshipStatus status, @Nullable TeamRole role) {
        return internshipRepository.findAll().stream()
                .filter(i -> i.getProject().getId().equals(projectId))
                .filter(i -> status == null || internshipMapper.map(i.getStatus()) == status)
                .filter(i -> role == null || i.getInterns().stream()
                        .anyMatch(member -> internshipMapper.map(member.getRole()) == role))
                .map(internshipMapper::toDto)
                .toList();
    }

    private void applyCompletionLogic(Internship internship) {
        for (TeamMember intern : internship.getInterns()) {
            boolean completed = projectService.hasCompletedAllTasks(intern.getId(), internship.getId());

            InternshipStatus status = internshipMapper.map(internship.getStatus());
            if (status == InternshipStatus.COMPLETED && completed) {
                projectService.promoteIntern(intern.getId(), internship.getProject().getId());
            } else {
                projectService.removeMemberFromProject(intern.getId(), internship.getProject().getId());
            }
        }
    }

    private void validateMentorShip(Project project, InternshipDto dto) {
        long mentorId = dto.getMentorId();
        List<Integer> traineeIds = dto.getTraineeIds();

        for (Integer traineeId : traineeIds) {
            var mentorDto = userServiceClient.getMentor(traineeId);
            if (mentorDto == null || mentorDto.id() != mentorId) {
                throw new IllegalArgumentException("Trainee " + traineeId + " has a different mentor");
            }
        }

        boolean mentorInTeam = project.getTeams().stream()
                .anyMatch(member -> member.getId().equals(mentorId));

        if (!mentorInTeam) {
            throw new IllegalArgumentException("Mentor must be a member of the project team");
        }
    }

    private void validateCreateDto(InternshipDto dto) {
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new IllegalArgumentException("Start and end dates are required");
        }

        if (dto.getEndDate().isAfter(dto.getStartDate().plusMonths(3))) {
            throw new IllegalArgumentException("Internship cannot last more than 3 months");
        }

        if (dto.getTraineeIds() == null || dto.getTraineeIds().isEmpty()) {
            throw new IllegalArgumentException("Intern list cannot be empty");
        }

        if (dto.getMentorId() == null) {
            throw new IllegalArgumentException("Mentor must be provided");
        }
    }

    private void validateUpdateDto(Internship existing, InternshipDto dto) {
        InternshipStatus existingStatus = internshipMapper.map(existing.getStatus());

        if (existingStatus == InternshipStatus.IN_PROGRESS) {
            if (!existing.getInterns().stream().map(TeamMember::getId)
                    .map(Long::intValue).toList()
                    .equals(dto.getTraineeIds())) {
                throw new IllegalArgumentException("Cannot change interns after internship has started");
            }

            if (!existing.getMentorId().getId().equals(dto.getMentorId().longValue())) {
                throw new IllegalArgumentException("Cannot change mentor after internship has started");
            }
        }

        if (dto.getEndDate() == null) {
            throw new IllegalArgumentException("End date must be specified");
        }
    }
}