package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipCreateDto;
import faang.school.projectservice.dto.internship.InternshipEditDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipReadDto;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipCreateMapper;
import faang.school.projectservice.mapper.internship.InternshipReadMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.validator.internship.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final ProjectService projectService;
    private final TeamMemberService teamMemberService;
    private final TeamMemberRepository teamMemberRepository;
    private final InternshipRepository internshipRepository;
    private final InternshipCreateMapper internshipCreateMapper;
    private final InternshipReadMapper internshipReadMapper;
    private final InternshipValidator internshipValidator;
    private final List<InternshipFilter> filters;

    public InternshipReadDto createInternship(InternshipCreateDto internshipDto) {
        internshipValidator.validateInternshipCreation(internshipDto);
        Internship internship = internshipCreateMapper.toEntity(internshipDto);

        internship.setProject(projectService.getProject(internshipDto.getProjectId()));
        internship.setMentorId(teamMemberService.findById(internshipDto.getMentorId()));
        internship.setInterns(getInternsById(internshipDto.getInternsIds()));

        return internshipReadMapper.toDto(internshipRepository.save(internship));
    }

    public InternshipReadDto updateInternship(InternshipEditDto internshipDto) {
        internshipValidator.validateInternshipUpdating(internshipDto);

        Internship internship = findInternshipById(internshipDto.getId());
        List<TeamMember> interns = new ArrayList<>();
        TeamMember intern;

        for (Long id : internshipDto.getInternsIds()) {
            if (internshipValidator.validateInternCompletedInternship(internshipDto, id)) {
                intern = teamMemberService.findById(id);

                intern.getRoles().add(internshipDto.getRole());
                interns.add(intern);
            }
        }

        if (!interns.isEmpty()) {
            internship.setInterns(interns);
        }

        Internship updatedInternship = internshipRepository.save(internship);

        return internshipReadMapper.toDto(updatedInternship);
    }

    public List<InternshipReadDto> getInternshipsByFilters(InternshipFilterDto internshipDto) {
        List<Internship> internships = findInternships();
        List<InternshipFilter> applicableFilters = filters.stream()
                .filter(internshipFilter -> internshipFilter.isApplicable(internshipDto))
                .toList();

        if (applicableFilters.isEmpty()) {
            return List.of();
        }

        return applicableFilters.stream()
                .reduce(internships.stream(),
                        (internshipStream, internshipFilter) -> internshipFilter.apply(internshipStream, internshipDto),
                        (list1, list2) -> list1)
                .map(internshipReadMapper::toDto)
                .toList();
    }

    public List<Internship> findInternships() {
        return internshipRepository.findAll();
    }

    public List<InternshipReadDto> getInternships() {
        return findInternships().stream()
                .map(internshipReadMapper::toDto)
                .toList();
    }

    public Internship findInternshipById(long internshipId) {
        return internshipRepository.findById(internshipId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Стажировка с ID %d не найдена", internshipId)
                ));
    }

    public InternshipReadDto getInternshipById(long internshipId) {
        return internshipReadMapper.toDto(findInternshipById(internshipId));
    }

    private List<TeamMember> getInternsById(List<Long> internsIds) {
        return teamMemberRepository.findAllById(internsIds);
    }
}
