package faang.school.projectservice.service;

import faang.school.projectservice.config.audit.AuditorAwareImpl;
import faang.school.projectservice.dto.internship.InternshipCreateDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
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
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InternshipMapper mapper;
    private final InternshipValidator validator;
    private final AuditorAwareImpl auditor;
    private final List<InternshipFilter> filters;

    @Transactional
    public InternshipCreateDto create(InternshipCreateDto internshipCreateDto) {
        Internship internship = mapper.toEntity(internshipCreateDto);

        internship.setInterns(getInterns(internshipCreateDto.getInternsId()));
        internship.setProject(getProject(internshipCreateDto.getProjectId()));
        internship.setMentor(getMentor(internshipCreateDto.getMentorId()));
        internship.setCreatedBy(auditor.getCurrentAuditor().orElseThrow(() -> new EntityNotFoundException("Auditor not found")));
        validator.internshipCreateValidate(internship);
        internshipRepository.save(internship);
        log.info("Стажировка с id: {} успешно создана", internship.getId());
        return mapper.toDto(internship);
    }

    @Transactional
    public InternshipCreateDto updateInternship(InternshipUpdateDto internshipUpdateDto) {
        Internship internship = mapper.toEntity(getInternship(internshipUpdateDto.getId()));
        validator.internshipUpdateValidation(internship, internshipUpdateDto);

        if (internshipUpdateDto.getStatus().equals(InternshipStatus.COMPLETED)) {
            List<Task> tasks = internship.getProject().getTasks();

            for (TeamMember teamMember : internship.getInterns()) {
                if (validator.internValidation(teamMember, tasks)) {
                    teamMember.setRoles(List.of(internship.getRole()));
                } else {
                    teamMember.setRoles(List.of());
                }
                teamMemberRepository.save(teamMember);
            }
            internship.setStatus(internshipUpdateDto.getStatus());
        }

        if (validator.isInternsListNotEqualNotEmpty(internship, internshipUpdateDto)) {
            internship.setInterns(getInterns(internshipUpdateDto.getInternsId()));
        }
        internshipRepository.save(internship);
        log.info("Стажировка с id: {} успешно обновлена", internship.getId());
        return mapper.toDto(internship);
    }


    public TeamMember getMentor(Long mentorId) {
        return teamMemberRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException("Ментора с id: " + mentorId + " не существует!!!"));
    }

    public List<TeamMember> getInterns(List<Long> internsId) {
        List<TeamMember> interns = new ArrayList<>();
        for (Long internId : internsId) {
            interns.add(teamMemberRepository.findById(internId)
                    .orElseThrow(() -> new EntityNotFoundException("Стажера с id: " + internId + " не существует!!!")));
        }
        return interns;
    }

    public Project getProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Проекта с id: " + projectId + " не существует!!!"));
    }

    public InternshipCreateDto getInternship(Long id) {
        return mapper.toDto(internshipRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Стажировки с id: " + id + " не существует")));
    }

    public List<InternshipCreateDto> getInternshipByFilter(InternshipFilterDto filter) {
        Stream<Internship> internships = internshipRepository.findAll().stream();
        for (InternshipFilter i : filters) {
            internships = i.apply(internships, filter);
        }
        return internships
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
