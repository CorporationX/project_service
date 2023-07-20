package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.ResponseInternshipDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.jpa.ProjectJpaRepository;
import faang.school.projectservice.jpa.TaskRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final ProjectJpaRepository projectRepository;
    private final TeamMemberJpaRepository teamMemberRepository;
    private final TaskRepository taskRepository;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> internshipFilters;

    @Transactional
    public ResponseInternshipDto create(CreateInternshipDto dto) {
        TeamMember mentor = teamMemberRepository.findByUserIdAndProjectId(dto.getMentorId(), dto.getProjectId());
        Project project = projectRepository.findById(dto.getProjectId()).orElseThrow(() -> new IllegalArgumentException("Project not found"));
        List<TeamMember> interns = teamMemberRepository.findAllById(dto.getInternIds());

        if (dto.getName().isBlank()) {
            throw new IllegalArgumentException("Name shouldn't be blank");
        }
        if (dto.getInternIds() == null || dto.getInternIds().isEmpty()) {
            throw new IllegalArgumentException("The internship should contains interns");
        }
        if (Period.between(dto.getStartDate().toLocalDate(), dto.getEndDate().toLocalDate()).getMonths() > 3) {
            throw new IllegalArgumentException("The duration of the internship should not exceed 3 months");
        }
        if (mentor == null) {
            throw new IllegalArgumentException("The internship should contain mentor from project");
        }

        Internship internship = internshipMapper.createDtoToEntity(dto);
        internship.setProject(project);
        internship.setMentor(mentor);
        internship.setInterns(interns);

        return internshipMapper.entityToResponseDto(internshipRepository.save(internship));
    }

    @Transactional
    public ResponseInternshipDto update(UpdateInternshipDto dto) {
        Internship internship = internshipRepository.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("The internship not found"));
        Project project = internship.getProject();

        if (dto.getStatus().equals(InternshipStatus.COMPLETED) && internship.getStatus().equals(InternshipStatus.IN_PROGRESS)) {
            List<TeamMember> interns = internship.getInterns();
            interns.forEach(intern -> {
                List<TaskStatus> taskOfIntern = taskRepository.findAllByProjectIdAndPerformerUserId(internship.getProject().getId(), intern.getId())
                        .stream()
                        .map(Task::getStatus)
                        .toList();
                if (taskOfIntern.stream().allMatch(task -> task.equals(TaskStatus.DONE))) {
                    intern.addRole(internship.getInternshipRole());
                    intern.deleteRole(TeamRole.INTERN);
                } else {
                    project.getTeam().getTeamMembers().remove(intern);
                }
            });
        }

        if (dto.getMentorId() != null) {
            TeamMember newMentor = teamMemberRepository.findById(dto.getMentorId()).orElseThrow(() -> new IllegalArgumentException("Mentor is not found"));
            if (newMentor.getRoles().contains(internship.getInternshipRole())) {
                internship.setMentor(newMentor);
            }
        }

        internship.setUpdatedAt(LocalDateTime.now());
        internship.setName(dto.getName());
        internship.setUpdatedBy(dto.getUpdatedBy());

        return internshipMapper.entityToResponseDto(internship);
    }

    public List<ResponseInternshipDto> findByFilter(InternshipFilterDto internshipFilterDto) {
        Stream<Internship> internships = internshipRepository.findAllByProjectId(internshipFilterDto.getProjectId()).stream();
        for (InternshipFilter filter : internshipFilters) {
            if (filter.isApplicable(internshipFilterDto)) {
                internships = filter.apply(internships, internshipFilterDto);
            }
        }

        return internshipMapper.entityListToDtoList(internships.toList());
    }

    public List<ResponseInternshipDto> findAll() {
        return internshipMapper.entityListToDtoList(internshipRepository.findAll());
    }

    public ResponseInternshipDto findById(Long id) {
        return internshipMapper.entityToResponseDto(
                internshipRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("The internship not found")));
    }

}
