package faang.school.projectservice.service.internship;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.commonServiceMethods.CommonMethods;
import faang.school.projectservice.service.internship.filter.InternshipFilterService;
import faang.school.projectservice.validation.internship.InternshipValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class InternshipServiceImpl implements InternshipService {
    private final InternshipRepository internshipRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final InternshipValidator validator;
    private final InternshipMapper internshipMapper;
    private final InternshipFilterService internshipFilterService;
    private final ProjectRepository projectRepository;
    private final CommonMethods commonMethods;

    @Override
    @Transactional
    public InternshipDto createInternship(long userId, InternshipDto internshipDto) {

        Internship internship = internshipMapper.toEntity(internshipDto);
        setMentorToInternship(internship);
        internship.setCreatedBy(userId);

        validator.validateCreateInternship(userId, internship, internshipDto);

        addInternshipToProject(internshipDto, internship);
        internshipRepository.save(internship);
        log.info("Created internship {}", internship.getId());
        return internshipMapper.toDto(internship);
    }

    private void addInternsToInternship(Internship internship) {
        List<TeamMember> interns = internship.getInterns();
        for (TeamMember intern : interns) {
            internshipRepository.addInternToInternship(internship.getId(), intern.getId());
        }
    }

    private void setMentorToInternship(Internship internship) {
        TeamMember mentor = teamMemberRepository.findByUserIdAndProjectId(
                internship.getMentor().getUserId(),
                internship.getProject().getId());

        if(mentor == null){
            throw new NotFoundException(
                    String.format("Mentor %s not found", internship.getMentor().getId()));
        }

        internship.setMentor(mentor);
    }

    @Override
    @Transactional
    public InternshipDto updateInternship(long internshipId, InternshipDto updatedInternshipDto) {

        Internship internshipToUpdate = commonMethods.findEntityById(internshipRepository, internshipId, "Internship");
//        Internship updatedInternship = internshipMapper.toEntity(updatedInternshipDto);

        validator.validateUpdateInternship(internshipToUpdate, updatedInternshipDto);

        internshipMapper.update(updatedInternshipDto, internshipToUpdate);
        internshipRepository.save(internshipToUpdate);
        log.info("Updated internship {}", internshipToUpdate.getId());
        return internshipMapper.toDto(internshipToUpdate);
    }

    @Override
    @Transactional
    public InternshipDto addNewIntern(long internshipId, long newInternId) {
        Internship internship = commonMethods.findEntityById(internshipRepository, internshipId, "Internship");
        TeamMember newIntern = teamMemberRepository.findById(internshipId);

        validator.validateAddNewIntern(internship, newIntern);
        internship.getInterns().add(newIntern);

        log.info("Added intern {} to internship {}", newInternId, internshipId);
        return internshipMapper.toDto(internship);
    }

    @Override
    @Transactional
    public InternshipDto finishInternshipForIntern(long internshipId, long internId, String teamRole) {
        Internship internship = commonMethods.findEntityById(internshipRepository, internshipId, "Internship");
        TeamMember intern = getInternFromInternship(internship, internId);
        TeamRole newInternRoleAfterInternship = commonMethods.findValueInEnum(teamRole, TeamRole.class, "TeamRole");

        validator.validateFinishInternshipForIntern(internship, intern);

        changeInternRole(intern, newInternRoleAfterInternship);
        internship.getInterns().remove(intern);
        if (internship.getInterns().isEmpty()) {
            internshipRepository.deleteById(internship.getId());
        }

        log.info("Intern {} finished internship {} and got teamRole {}", internId, internshipId, teamRole);
        return internshipMapper.toDto(internship);
    }

    @Override
    @Transactional
    public InternshipDto removeInternFromInternship(long internshipId, long internId) {
        Internship internship = commonMethods.findEntityById(internshipRepository, internshipId, "Internship");
        TeamMember intern = getInternFromInternship(internship, internId);

        validator.validateRemoveInternFromInternship(internship, intern);

        internship.getInterns().remove(intern);
        if (internship.getInterns().isEmpty()) {
            internshipRepository.deleteById(internship.getId());
        }
        log.info("Removed intern {} from internship {}", internId, internshipId);
        return internshipMapper.toDto(internship);
    }

    @Override
    @Transactional(readOnly = true)
    public InternshipDto getInternshipById(long internshipId) {
        return internshipMapper.toDto(commonMethods.findEntityById(internshipRepository, internshipId, "Internship"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternshipDto> getInternshipsByFilter(InternshipFilterDto filterDto) {

        return internshipFilterService.applyFilters(internshipRepository.findAll().stream(), filterDto)
                .map(internshipMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternshipDto> getAllInternships() {

        return internshipRepository.findAll().stream()
                .map(internshipMapper::toDto)
                .toList();
    }

    @Transactional
    public void addInternshipToProject(InternshipDto internshipDto, Internship internship) {

        Project project = projectRepository.getProjectById(internshipDto.getProjectId());

        internship.setProject(project);
        if (project.getInternships() == null) {
            project.setInternships(new ArrayList<>(List.of(internship)));
        }
        project.getInternships().add(internship);
        log.info("Added internship {} to project {}", internship.getId(), project.getId());
    }

    private TeamMember getInternFromInternship(Internship internship, long internId) {

        return internship.getInterns().stream()
                .filter(teamMember -> teamMember.getId().equals(internId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(
                        String.format("Intern with id: %d not found in the internship: %d", internId, internship.getId())));
    }

    private void changeInternRole(TeamMember teamMember, TeamRole newRole) {
        if (teamMember.getRoles() == null) {
            teamMember.setRoles(new ArrayList<>());
        }

        validator.validateTeamMemberDontHaveThisRole(teamMember, newRole);

        teamMember.getRoles().add(newRole);
        teamMemberRepository.save(teamMember);
        log.info("Changed team member {} role from intern to {}", teamMember.getId(), newRole);
    }
}
