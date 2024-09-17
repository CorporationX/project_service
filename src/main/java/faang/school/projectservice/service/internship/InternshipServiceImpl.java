package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.intership.InternshipValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InternshipServiceImpl implements InternshipService {
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final List<InternshipFilter> internshipFilters;
    private final InternshipValidator internshipValidator;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    public InternshipDto createInternship(InternshipDto internshipDto) {
        internshipValidator.validateDto(internshipDto);
        internshipValidator.validateInternshipDuration(internshipDto);

        var interns = teamMemberRepository.findAllById(internshipDto.internsId());
        var internship = internshipMapper.toEntity(internshipDto);

        internship.setInterns(interns);
        assignProjectAndMentor(internship, internshipDto.projectId(), internshipDto.mentorId());
        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    @Override
    @Transactional
    public InternshipDto updateInternship(long id, InternshipDto internshipDto) {
        internshipValidator.validateDto(internshipDto);
        internshipValidator.validateInternshipDuration(internshipDto);

        var existingInternship = internshipRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Internship with ID: %d not found".formatted(id)));

        internshipValidator.validateInternshipNotStarted(internshipDto);
        updateInternshipFields(existingInternship, internshipDto);
        assignProjectAndMentor(existingInternship, internshipDto.projectId(), internshipDto.mentorId());
        internshipRepository.save(existingInternship);

        if (existingInternship.getStatus() == InternshipStatus.COMPLETED)
            handleCompletedInternship(existingInternship);

        return internshipMapper.toDto(existingInternship);
    }

    @Override
    public List<InternshipDto> getAllInternships() {
        return internshipRepository.findAll().stream()
                .map(internshipMapper::toDto)
                .toList();
    }

    @Override
    public List<InternshipDto> getAllInternshipsByFilter(InternshipFilterDto filters) {
        var internships = internshipRepository.findAll().stream();
        return internshipFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(internships, (stream, filter) -> filter.applyFilter(stream, filters),
                        (newStream, oldStream) -> newStream)
                .map(internshipMapper::toDto)
                .toList();
    }

    @Override
    public InternshipDto getInternshipById(long id) {
        return internshipMapper.toDto(internshipRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Internship with ID: %d not found".formatted(id))));
    }

    private void assignProjectAndMentor(Internship internship, Long projectId, Long mentorId) {
        if (projectId != null) {
            var project = projectRepository.getProjectById(projectId);
            internship.setProject(project);
        }
        if (mentorId != null) {
            var mentor = teamMemberRepository.findById(mentorId);
            internship.setMentorId(mentor);
        }
    }

    private void updateInternshipFields(Internship existingInternship, InternshipDto internshipDto) {
        if (internshipDto.name() != null) {
            existingInternship.setName(internshipDto.name());
        }

        if (internshipDto.description() != null) {
            existingInternship.setDescription(internshipDto.description());
        }

        if (internshipDto.status() != null) {
            existingInternship.setStatus(internshipDto.status());
        }

        if (internshipDto.startDate() != null) {
            existingInternship.setStartDate(internshipDto.startDate());
        }

        if (internshipDto.endDate() != null) {
            existingInternship.setEndDate(internshipDto.endDate());
        }
    }

    private void handleCompletedInternship(Internship internship) {
        List<Task> tasks = internship.getProject().getTasks();
        boolean allTasksCompleted = tasks.stream()
                .allMatch(task -> task.getStatus().equals(TaskStatus.DONE));
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
        var internsToRemove = internship.getInterns();
        internsToRemove.forEach(intern -> {
            internship.getInterns().remove(intern);
            teamMemberRepository.save(intern);
        });
        internshipRepository.save(internship);
    }
}
