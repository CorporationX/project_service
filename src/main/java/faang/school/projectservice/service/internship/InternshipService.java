package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
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
import faang.school.projectservice.service.teamMember.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.FOREIGN_MENTOR_EXCEPTION;
import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.NEW_INTERNS_EXCEPTION;
import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.NON_EXISTING_INTERNSHIP_EXCEPTION;
import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.NON_EXISTING_INTERN_EXCEPTION;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipFiller internshipFiller;
    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> filters;
    private final TeamMemberService teamMemberService;

    public InternshipDto create(InternshipDto internshipDto) {
        Project project = projectRepository.getProjectById(internshipDto.getProjectId());
        TeamMember mentor = teamMemberRepository.findById(internshipDto.getMentorId());
        List<TeamMember> interns = teamMemberRepository.findAllByIds(internshipDto.getInternsIds());

        verifyMentorsProject(project, mentor);
        verifyExistenceOfAllInterns(interns, internshipDto.getInternsIds().size());

        Internship internship = internshipMapper.toEntity(internshipDto);
        internshipFiller.fillEntity(internship, project, mentor, interns);

        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    public InternshipDto update(InternshipDto internshipDto) {
        Project project = projectRepository.getProjectById(internshipDto.getProjectId());
        TeamMember mentor = teamMemberRepository.findById(internshipDto.getMentorId());
        List<TeamMember> interns = teamMemberRepository.findAllByIds(internshipDto.getInternsIds());
        Internship internshipToBeUpdated = internshipRepository.findById(internshipDto.getId())
                .orElseThrow(() -> new DataValidationException(NON_EXISTING_INTERNSHIP_EXCEPTION.getMessage()));

        verifyMentorsProject(project, mentor);
        verifyExistenceOfAllInterns(interns, internshipDto.getInternsIds().size());
        verifyUpdatedInterns(internshipToBeUpdated, interns);

        internshipMapper.update(internshipDto, internshipToBeUpdated);
        internshipFiller.fillEntity(internshipToBeUpdated, project, mentor, interns);

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
        return internshipMapper.toDto(internshipRepository.findAll());
    }

    public InternshipDto getInternshipById(long internshipId) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new DataValidationException(NON_EXISTING_INTERNSHIP_EXCEPTION.getMessage()));

        return internshipMapper.toDto(internship);
    }

    private void verifyExistenceOfAllInterns(List<TeamMember> interns, Integer internsNumInDto) {
        if (interns.size() != internsNumInDto) {
            throw new DataValidationException(NON_EXISTING_INTERN_EXCEPTION.getMessage());
        }
    }

    private void verifyMentorsProject(Project project, TeamMember mentor) {
        Project mentorsProject = mentor.getTeam().getProject();

        if (!project.equals(mentorsProject)) {
            throw new DataValidationException(FOREIGN_MENTOR_EXCEPTION.getMessage());
        }
    }

    private void verifyUpdatedInterns(Internship internshipBeforeUpdate, List<TeamMember> internsAfterUpdate) {
        Set<TeamMember> internsBeforeUpdate = new HashSet<>(internshipBeforeUpdate.getInterns());

        if (!internsBeforeUpdate.containsAll(internsAfterUpdate)) {
            throw new DataValidationException(NEW_INTERNS_EXCEPTION.getMessage());
        }
    }
}