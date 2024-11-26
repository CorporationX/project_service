package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.filter.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.internship.filter.InternshipFilter;
import faang.school.projectservice.service.teammember.TeamMemberService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.NON_EXISTING_INTERNSHIP_EXCEPTION;

@Service
@AllArgsConstructor
@Setter
public class InternshipService {
    private final InternshipVerifier internshipVerifier;
    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InternshipMapper internshipMapper;
    private List<InternshipFilter> filters;
    private final TeamMemberService teamMemberService;

    public InternshipDto create(InternshipDto internshipDto) {
        InternshipData internshipData = getInternshipData(internshipDto);

        Internship internship = internshipMapper.toEntity(internshipDto);
        fillEntity(internship, internshipData.getProject(), internshipData.getMentor(), internshipData.getInterns());

        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    public InternshipDto update(InternshipDto internshipDto) {
        InternshipData internshipData = getInternshipData(internshipDto);
        List<TeamMember> interns = internshipData.getInterns();
        Internship internshipToBeUpdated = getInternship(internshipDto.getId());

        internshipVerifier.verifyUpdatedInterns(internshipToBeUpdated, interns);

        internshipMapper.updateEntity(internshipDto, internshipToBeUpdated);
        fillEntity(internshipToBeUpdated, internshipData.getProject(), internshipData.getMentor(), interns);

        if (internshipToBeUpdated.getStatus().equals(InternshipStatus.COMPLETED)) {
            teamMemberService.hireInterns(internshipToBeUpdated);
        }

        return internshipMapper.toDto(internshipRepository.save(internshipToBeUpdated));
    }

    public List<InternshipDto> getInternshipsOfProject(long projectId, InternshipFilterDto filtersDto) {
        List<Internship> projectInternships = internshipRepository.findAll().stream()
                .filter(internship -> internship.getProject().getId().equals(projectId))
                .toList();

        return filters.stream()
                .filter(filter -> filter.isApplicable(filtersDto))
                .flatMap(filter -> filter.apply(projectInternships, filtersDto))
                .distinct()
                .map(internshipMapper::toDto)
                .toList();
    }

    public List<InternshipDto> getAllInternships() {
        return internshipMapper.toDtoList(internshipRepository.findAll());
    }

    public InternshipDto getInternshipById(long internshipId) {
        Internship internship = getInternship(internshipId);

        return internshipMapper.toDto(internship);
    }

    private void fillEntity(Internship internship, Project project, TeamMember mentor, List<TeamMember> interns) {
        internship.setProject(project);
        internship.setMentorId(mentor);
        internship.setInterns(interns);
    }

    private Internship getInternship(Long internshipDto) {
        return internshipRepository.findById(internshipDto)
                .orElseThrow(() -> new DataValidationException(NON_EXISTING_INTERNSHIP_EXCEPTION.getMessage()));
    }

    private InternshipData getInternshipData(InternshipDto internshipDto) {
        Project project = projectRepository.getProjectById(internshipDto.getProjectId());
        TeamMember mentor = teamMemberRepository.findById(internshipDto.getMentorId());
        List<TeamMember> interns = teamMemberRepository.findAllByIds(internshipDto.getInternsIds());

        internshipVerifier.verifyMentorsProject(project, mentor);
        internshipVerifier.verifyExistenceOfAllInterns(interns, internshipDto.getInternsIds().size());

        return InternshipData.builder()
                .project(project)
                .mentor(mentor)
                .interns(interns)
                .build();
    }
}