package faang.school.projectservice.service.internship;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.commonServiceMethods.CommonMethods;
import faang.school.projectservice.service.internship.filter.InternshipFilterService;
import faang.school.projectservice.validation.internship.InternshipValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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
    private final TeamRepository teamRepository;
    private final CommonMethods commonMethods;

    @Override
    @Transactional
    public InternshipDto createInternship(long userId, InternshipDto internshipDto) {

        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setCreatedBy(userId);

        validator.validateCreateInternship(userId, internship, internshipDto);

        addInternshipToProject(internshipDto, internship);
        internshipRepository.save(internship);
        log.info("Created internship {}", internship.getId());
        return internshipMapper.toDto(internship);

//        Duration duration = Duration.between(internshipDto.getStartDate(), internshipDto.getEndDate());
//        if (duration.toDays() > 90) {
//            throw new IllegalArgumentException("Internship duration cannot exceed 3 months");
//        }
//
//        TeamMember mentor = teamMemberRepository.findById(internshipDto.getMentorId());
//        Team t = mentor.getTeam();
//        Team team = teamRepository.findById(t.getId())
//                .orElseThrow(() -> new IllegalArgumentException("Team not found"));
//
//        Project p = team.getProject();
//        if (p.getId() != internshipDto.getProjectId()) {
//            throw new IllegalArgumentException("Mentor is not part of the project team");
//        }
//
//        // Validate interns
//        List<TeamMember> interns = new ArrayList<>();
//        for (Long internId : internshipDto.getInternsId()) {
//            TeamMember intern = teamMemberRepository.findById(internId);
//            interns.add(intern);
//        }
//
//        Internship internship = internshipMapper.toEntity(internshipDto);
//        internship.setCreatedBy(userId);
//        internshipRepository.save(internship);
//        return internshipMapper.toDto(internship);
    }

    private TeamMember setMentorToInternship(long userId, InternshipDto internshipDto) {
        TeamMember mentor = teamMemberRepository.findByUserIdAndProjectId(
                userId,
                internshipDto.getProjectId());

        if(mentor == null){
            throw new NotFoundException(
                    String.format("Mentor %s not found", internshipDto.getMentorId()));
        }

        return mentor;
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
