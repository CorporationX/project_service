package faang.school.projectservice.service.internship;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.internship.filter.InternshipFilterService;
import faang.school.projectservice.validator.internship.InternshipValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InternshipServiceImpl implements InternshipService {
    private final InternshipRepository internshipRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InternshipValidator validator;
    private final InternshipMapper internshipMapper;
    private final InternshipFilterService internshipFilterService;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional
    // TODO проверить наличия ментора
    // TODO проверить, что нам есть кого стажировать
    public InternshipDto createInternship(InternshipDto internshipDto) {
        validator.validateInternshipExistence(internshipDto);

        Internship internship = internshipMapper.toEntity(internshipDto);

        validator.validateInternshipCreation(internship);

        addInternshipToProject(internshipDto, internship);

        internshipRepository.save(internship);

        return internshipMapper.toDto(internship);
    }

    @Override
    @Transactional
    public InternshipDto updateInternship(InternshipDto updatedInternshipDto) {

        Internship internship = internshipRepository.findById(updatedInternshipDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Internship not found with id: " + updatedInternshipDto.getId()));

        Internship updatedInternship = internshipMapper.toEntity(updatedInternshipDto);

        validator.validateInternshipUpdate(internship, updatedInternship);

        internshipRepository.deleteById(internship.getId());
        internshipRepository.save(updatedInternship);

        return internshipMapper.toDto(updatedInternship);

    }

    @Override
    @Transactional
    public InternshipDto addNewIntern(long internshipId, long newInternId) {

        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new IllegalArgumentException("Internship not found with id: " + internshipId));

        TeamMember newIntern = teamMemberRepository.findById(internshipId);

        validator.validateAddingNewIntern(internship, newIntern);

        internship.getInterns().add(newIntern);

        return internshipMapper.toDto(internship);
    }

    @Override
    @Transactional
    public InternshipDto finishInternshipForIntern(long internshipId, long internId, String teamRole) {

        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new IllegalArgumentException("Internship not found with id: " + internshipId));

        TeamMember intern = searchInternInInternship(internship, internId);
        TeamRole newInternRoleAfterInternship = getTeamRole(teamRole);

        validator.validateFinishingInternshipForIntern(internship, intern);

        changeInternRole(intern, newInternRoleAfterInternship);

        internship.getInterns().remove(intern);

        if(internship.getInterns().isEmpty()){
            return deleteInternship(internship);
        }

        return internshipMapper.toDto(internship);
    }

    private TeamRole getTeamRole(String teamRole) {
        try {
            return TeamRole.valueOf(teamRole);
        } catch (IllegalArgumentException e) {
            throw new DataValidationException("TeamRole with name: " + teamRole + " not found");
        }
    }
    @Override
    @Transactional
    public InternshipDto removeInternFromInternship(long internshipId, long internId) {

        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new IllegalArgumentException("Internship not found with id: " + internshipId));

        TeamMember intern = searchInternInInternship(internship, internId);

        validator.validateRemovingInternFromInternship(internship, intern);

        internship.getInterns().remove(intern);

        if(internship.getInterns().isEmpty()){
            deleteInternship(internship);
        }

        return internshipMapper.toDto(internship);
    }

    @Override
    @Transactional
    public InternshipDto getInternshipById(long internshipId) {

        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new IllegalArgumentException("Internship not found with id: " + internshipId));

        return internshipMapper.toDto(internship);
    }

    @Override
    public List<InternshipDto> getInternshipsByFilter(InternshipFilterDto filterDto) {

        List<Internship> internships = internshipRepository.findAll();

        return internshipFilterService.applyFilters(internships.stream(), filterDto)
                .map(internshipMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public List<InternshipDto> getAllInternships() {

        List<Internship> internships = internshipRepository.findAll();

        return internships.stream()
                .map(internshipMapper::toDto)
                .toList();
    }

    private TeamMember searchInternInInternship(Internship internship, long internId) {

        return internship.getInterns().stream()
                .filter(teamMember -> teamMember.getId().equals(internId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Intern with id: " + internId
                                                               + "not found in the internship: " + internship.getId()));
    }

    private void changeInternRole(TeamMember teamMember, TeamRole newRole) {

        if (teamMember.getRoles() == null) {
            teamMember.setRoles(new ArrayList<>());
        }

        if (!teamMember.getRoles().contains(newRole)) {
            teamMember.getRoles().add(newRole);
        }

        teamMemberRepository.save(teamMember);
    }

    @Transactional
    public void addInternshipToProject(InternshipDto internshipDto, Internship internship) {

        Project project = projectRepository.getProjectById(internshipDto.getProjectId());

        internship.setProject(project);

        if (project.getInternships() == null) {
            project.setInternships(new ArrayList<>(List.of(internship)));
        }
        project.getInternships().add(internship);
    }

    private InternshipDto deleteInternship(Internship internship) {
        try {
            internshipRepository.deleteById(internship.getId());
            return internshipMapper.toDto(internship);

        } catch (Exception e) {
            throw new NotFoundException("Internship with id: " + internship.getId() + " not exist");
        }
    }
}
