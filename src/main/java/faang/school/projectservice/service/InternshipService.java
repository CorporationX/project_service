package faang.school.projectservice.service;

import faang.school.projectservice.config.audit.AuditorAwareImpl;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.validator.InternshipValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternshipService {
    private final InternshipRepository internshipRepository;
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final TeamMemberMapper teamMemberMapper;
    private final InternshipMapper mapper;
    private final InternshipValidator validator;
    private final AuditorAwareImpl auditor;
    private final List<InternshipFilter> filters;

    @Transactional
    public InternshipDto create(InternshipDto internshipDto) {
        Internship internship = mapper.toEntity(internshipDto);

        internship.setInterns(getInterns(internshipDto.getInternsIds()));
        internship.setProject(projectService.getProjectById(internshipDto.getProjectId()));
        internship.setMentor(teamMemberService.get(internshipDto.getMentorId()));
        internship.setCreatedBy(currentUserId());
        validator.internshipCreateValidate(internship);
        internshipRepository.save(internship);
        return mapper.toDto(internship);
    }

    @Transactional
    public InternshipDto update(InternshipUpdateDto internshipUpdateDto) {
        Internship internship = internshipRepository.findById(internshipUpdateDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Стажировки с id: " + internshipUpdateDto.getId() + " не существует"));
        validator.internshipUpdateValidation(internship, internshipUpdateDto);
        Long updaterId = currentUserId();

        if (internshipUpdateDto.getStatus().equals(InternshipStatus.COMPLETED)) {
            List<Task> tasks = internship.getProject().getTasks();
            List<TeamMemberDto> teamMemberDto = teamMemberMapper.teamMemberListToTeamMemberDtoList(internship.getInterns());
            for (TeamMemberDto teamMember : teamMemberDto) {
                if (validator.internValidation(teamMember.getId(), tasks)) {
                    teamMember.setRoles(List.of(internship.getRole()));
                } else {
                    teamMember.setRoles(List.of());
                }
                teamMemberService.updateMember(teamMember, updaterId, internship.getProject().getId());
            }
            internship.setStatus(internshipUpdateDto.getStatus());
        }
        if (validator.isInternsListNotEqualNotEmpty(internship, internshipUpdateDto)) {
            internship.setInterns(getInterns(internshipUpdateDto.getInternsIds()));
        }
        internshipRepository.save(internship);
        return mapper.toDto(internship);
    }

    public List<TeamMember> getInterns(List<Long> internsId) {
        List<TeamMember> interns = new ArrayList<>();
        for (Long internId : internsId) {
            interns.add(teamMemberService.get(internId));
        }
        return interns;
    }

    public InternshipDto get(Long id) {
        return mapper.toDto(internshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Стажировки с id: " + id + " не существует")));
    }

    public List<InternshipDto> getByFilter(InternshipFilterDto filter) {
        Stream<Internship> internships = internshipRepository.findAll().stream();
        for (InternshipFilter i : filters) {
            internships = i.apply(internships, filter);
        }
        return internships
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    private Long currentUserId() {
        return auditor.getCurrentAuditor().orElseThrow(() -> new EntityNotFoundException("Auditor not found"));
    }
}
