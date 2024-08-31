package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
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
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InternshipValidator internshipValidator;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> internshipFilters;

    @Transactional
    public InternshipDto createInternship(InternshipDto internshipDto) {
        internshipValidator.validateDto(internshipDto);
        internshipValidator.validateInternshipDuration(internshipDto);
        Internship internship = internshipMapper.toEntity(internshipDto);
        assignProjectAndMentor(internship, internshipDto.projectId(), internshipDto.mentorId());
        Internship savedInternship = internshipRepository.save(internship);
        return internshipMapper.toDto(savedInternship);
    }

    @Transactional
    public InternshipDto updateInternship(long id, InternshipDto internshipDto) {
        internshipValidator.validateDto(internshipDto);
        internshipValidator.validateInternshipDuration(internshipDto);
        Internship existingInternship = internshipRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Internship with ID: %d not found",
                id)));
        internshipValidator.validateInternshipNotStarted(internshipDto);
        updateInternshipFields(existingInternship, internshipDto);
        assignProjectAndMentor(existingInternship, internshipDto.projectId(), internshipDto.mentorId());
        internshipRepository.save(existingInternship);
        if (existingInternship.getStatus() == InternshipStatus.COMPLETED) {
            handleCompletedInternship(existingInternship);
        }
        return internshipMapper.toDto(existingInternship);
    }

    @Transactional(readOnly = true)
    public List<InternshipDto> getInternshipsByFilter(InternshipFilterDto internshipFilterDto) {
        List<Internship> internships = internshipRepository.findAll();
        return internshipFilters.stream()
            .filter(internshipFilter -> internshipFilter.isApplicable(internshipFilterDto))
            .reduce(internships.stream(), (stream, filter) -> filter.apply(stream, internshipFilterDto),
                Stream::concat)
            .map(internshipMapper::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<InternshipDto> getAllInternships() {
        List<Internship> internships = internshipRepository.findAll();
        return internshipMapper.toDtoList(internships);
    }

    @Transactional(readOnly = true)
    public InternshipDto getInternshipById(long id) {
        Internship internship = internshipRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Internship with ID: %d not found",
                id)));
        return internshipMapper.toDto(internship);
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

    private void assignProjectAndMentor(Internship internship, Long projectId, Long mentorId) {
        if (projectId != null) {
            Project project = projectRepository.getProjectById(projectId);
            internship.setProject(project);
        }
        if (mentorId != null) {
            TeamMember mentor = teamMemberRepository.findById(mentorId);
            internship.setMentorId(mentor);
        }
    }

    private void handleCompletedInternship(Internship internship) {
        List<Task> tasks = internship.getProject().getTasks();
        boolean allTasksCompleted = tasks.stream().allMatch(task -> task.getStatus().equals(TaskStatus.DONE));
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
