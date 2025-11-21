package faang.school.projectservice.service.internship;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.common.PageResponse;
import faang.school.projectservice.dto.internship.InternshipCreateDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TaskRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.vacancy.TeamMemberService;
import faang.school.projectservice.validator.internship.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static faang.school.projectservice.validator.internship.InternshipValidator.MAX_INTERNSHIP_LENGTH_MONTHS;

@RequiredArgsConstructor
@Service
public class InternshipService {

    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TaskRepository taskRepository;
    private final TeamMemberService teamMemberService;
    private final UserContext userContext;

    @Transactional
    public InternshipDto createInternship(InternshipCreateDto createDto) {

        InternshipValidator.validateLengthDate(createDto.startDate(), createDto.endDate());

        Project project = projectRepository.findByIdOrThrow(createDto.projectId());
        TeamMember mentor = teamMemberRepository.findMentorByIdOrThrow(createDto.mentorId());
        InternshipValidator.validateMentorBelongsToProject(project, mentor);

        List<TeamMember> interns = teamMemberRepository.findAllById(createDto.internsIds());
        InternshipValidator.validateInternsNotNullAndNotEmpty(interns);

        long createdBy = userContext.getUserId();

        Internship internship = InternshipMapper.toEntity(
                createDto,
                project,
                mentor,
                interns,
                createdBy);
        internship.setEndDate(ifEndIsNull(createDto));

        internship = internshipRepository.save(internship);

        return internshipMapper.toDto(internship);
    }

    @Transactional
    public InternshipDto updateInternship(long internshipId, InternshipUpdateDto updateDto) {
        Internship internship = internshipRepository.findByIdOrThrow(internshipId);
        InternshipValidator.validateLengthDate(internship.getStartDate(), updateDto.endDate());
        InternshipValidator.validateIfInternshipIsStatusComplete(internship.getStatus());

        if (updateDto.status() == InternshipStatus.COMPLETED) {
            handleInternshipCompletion(internship);
            internship.setEndDate(LocalDateTime.now());
        } else {
            InternshipMapper.update(updateDto, internship);
        }

        internship = internshipRepository.save(internship);
        return internshipMapper.toDto(internship);
    }

    @Transactional(readOnly = true)
    public PageResponse<InternshipDto> getInternshipsByFiler(InternshipFilterDto filterDto, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<Internship> example = Example.of(Internship.builder()
                .status(filterDto.status())
                .role(filterDto.role())
                .name(filterDto.name())
                .build(), matcher);

        Page<Internship> pageInternship = internshipRepository.findAll(example, pageable);
        return PageResponse.from(pageInternship, internshipMapper::toDto);
    }

    @Transactional(readOnly = true)
    public InternshipDto getInternshipById(long internshipId) {
        Internship internship = internshipRepository.findByIdOrThrow(internshipId);
        return internshipMapper.toDto(internship);
    }

    private LocalDateTime ifEndIsNull(InternshipCreateDto createDto) {
        LocalDateTime start = createDto.startDate();
        if (createDto.endDate() == null) {
            return start.plusMonths(MAX_INTERNSHIP_LENGTH_MONTHS);
        }
        return createDto.endDate();
    }

    private void handleInternshipCompletion(Internship internship) {
        List<TeamMember> interns = internship.getInterns();
        for (TeamMember intern : interns) {
            List<Task> tasks = taskRepository.findByPerformerUserId(intern.getId());

            if (!areAllTasksCompleted(tasks)) {
                teamMemberService.removeMemberFromTeam(intern);
            }
        }
        for (TeamMember intern : interns) {
            intern.setRoles(Collections.singletonList(internship.getRole()));
        }
    }

    private boolean areAllTasksCompleted(List<Task> tasks) {
        return tasks.stream().allMatch(task -> task.getStatus() == TaskStatus.DONE);
    }
}