package faang.school.projectservice.service;

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
import faang.school.projectservice.repository.InternshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Project is not found"));
        TeamMember mentor = teamMemberRepository.findByIdAndProjectId(dto.getMentorId(), dto.getProjectId());
        List<TeamMember> interns = teamMemberRepository.findAllById(dto.getInternIds());
        interns.forEach(intern -> intern.addRole(dto.getInternshipRole()));

        validateCreateDto(dto, mentor);

        Internship internship = internshipMapper.createDtoToEntity(dto);
        internship.setProject(project);
        internship.setMentor(mentor);
        internship.setInterns(interns);
        internship.setCreatedAt(LocalDateTime.now());

        return internshipMapper.entityToResponseDto(internshipRepository.save(internship));
    }

    private static void validateCreateDto(CreateInternshipDto dto, TeamMember mentor) {
        if (Period.between(dto.getStartDate().toLocalDate(), dto.getEndDate().toLocalDate()).getMonths() > 3) {
            throw new IllegalArgumentException("The duration of the internship should not exceed 3 months");
        }
        if (mentor == null) {
            throw new IllegalArgumentException("The internship should contain mentor from project");
        }
    }

    @Transactional
    public ResponseInternshipDto update(UpdateInternshipDto dto) {
        Internship internship = internshipRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("The internship not found"));
        Project project = internship.getProject();

        finishInternship(dto, internship, project);
        internship.setStatus(dto.getStatus());
        internship.setUpdatedAt(LocalDateTime.now());
        internship.setName(dto.getName());
        internship.setUpdatedBy(dto.getUpdatedBy());

        return internshipMapper.entityToResponseDto(internship);
    }

    private void finishInternship(UpdateInternshipDto dto, Internship internship, Project project) {
        if (dto.getStatus().equals(InternshipStatus.COMPLETED) && internship.getStatus().equals(InternshipStatus.IN_PROGRESS)) {
            List<TeamMember> interns = internship.getInterns();
            for (TeamMember intern : interns) {
                List<TaskStatus> taskOfIntern =
                        taskRepository.findAllByProjectIdAndPerformerUserId(internship.getProject().getId(), intern.getId())
                                .stream()
                                .map(Task::getStatus)
                                .toList();
                if (taskOfIntern.stream().allMatch(task -> task.equals(TaskStatus.DONE))) {
                    intern.finishInternship();
                } else {
                    project.getTeams().forEach(team -> team.getTeamMembers().remove(intern));
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<ResponseInternshipDto> findByFilter(InternshipFilterDto internshipFilterDto) {
        Stream<Internship> internships =
                internshipRepository.findAllByProjectId(internshipFilterDto.getProjectId()).stream();

        for (InternshipFilter filter : internshipFilters) {
            if (filter.isApplicable(internshipFilterDto)) {
                internships = filter.apply(internships, internshipFilterDto);
            }
        }

        return internshipMapper.entityListToDtoList(internships.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponseInternshipDto> findAll() {
        return internshipMapper.entityListToDtoList(internshipRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ResponseInternshipDto findById(Long id) {
        return internshipMapper.entityToResponseDto(
                internshipRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("The internship with id " + id + " is not found")));
    }

}
