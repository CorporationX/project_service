package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.filter.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.service.internship.filter.InternshipFilter;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.FOREIGN_MENTOR_EXCEPTION;
import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.NEW_INTERNS_EXCEPTION;
import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.NON_EXISTING_INTERNSHIP_EXCEPTION;
import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.NON_EXISTING_INTERN_EXCEPTION;

@Service
@RequiredArgsConstructor
public class InternshipService {
    private final InternshipFiller internshipFiller;
    private final InternshipRepository internshipRepository;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> filters;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;


    public InternshipDto create(InternshipDto internshipDto) {
        verifyMentorsProject(internshipDto);
        verifyExistenceOfInterns(internshipDto);

        Internship internship = internshipMapper.toEntity(internshipDto);
        internshipFiller.fillEntity(internship,
                internshipDto.getProjectId(),
                internshipDto.getMentorId(),
                internshipDto.getInternsIds());

        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    public InternshipDto update(InternshipDto internshipDto) {
        verifyMentorsProject(internshipDto);
        verifyExistenceOfInterns(internshipDto);
        verifyUpdatedInterns(internshipDto);

        Internship internshipToBeUpdated = internshipRepository.findById(internshipDto.getId())
                .orElseThrow(() -> new DataValidationException(NON_EXISTING_INTERNSHIP_EXCEPTION.getMessage()));

        internshipMapper.update(internshipDto, internshipToBeUpdated);
        internshipFiller.fillEntity(internshipToBeUpdated,
                internshipDto.getProjectId(),
                internshipDto.getMentorId(),
                internshipDto.getInternsIds());

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

    private void verifyExistenceOfInterns(InternshipDto internshipDto) {
        List<Long> internsIds = internshipDto.getInternsIds();
        var existingInternIds = internsIds.stream()
                .filter(teamMemberService::existsById)
                .toList();

        if (!existingInternIds.equals(internsIds)) {
            throw new DataValidationException(NON_EXISTING_INTERN_EXCEPTION.getMessage());
        }
    }

    private void verifyMentorsProject(InternshipDto internshipDto) {
        var internshipsProject = projectService.getProjectById(internshipDto.getProjectId());
        var mentorsProject = teamMemberService.getTeamMembersProject(internshipDto.getMentorId());

        if (!internshipsProject.equals(mentorsProject)) {
            throw new DataValidationException(FOREIGN_MENTOR_EXCEPTION.getMessage());
        }
    }

    private void verifyUpdatedInterns(InternshipDto internshipDto) {
        var internshipBeforeUpdate = internshipRepository.findById(internshipDto.getId())
                .orElseThrow(() -> new DataValidationException(NON_EXISTING_INTERNSHIP_EXCEPTION.getMessage()));

        var internsBeforeUpdate = new HashSet<>(internshipBeforeUpdate.getInterns());
        var internsAfterUpdate = internshipDto.getInternsIds().stream()
                .map(teamMemberService::getTeamMemberById)
                .toList();

        if (!internsBeforeUpdate.containsAll(internsAfterUpdate)) {
            throw new DataValidationException(NEW_INTERNS_EXCEPTION.getMessage());
        }
    }
}