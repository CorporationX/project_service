package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.SearchDto;
import faang.school.projectservice.dto.internship.UpdateInternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class InternshipServiceImpl implements InternshipService {

    private static final int INTERNSHIP_DURATION_MONTHS = 3;
    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;
    private final InternshipMapper internshipMapper;
    private final UserContext userContext;
    private final List<InternshipFilter> internshipFilters;

    @Override
    public InternshipDto createInternship(Long projectId, CreateInternshipDto internshipDto) {

        Project project = projectRepository.getByIdOrThrow(projectId);

        validateCreate(project, internshipDto);

        LocalDateTime now = LocalDateTime.now();
        Long createdBy = userContext.getUserId();
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setProject(project);
        internship.setCreatedBy(createdBy);
        internship.setCreatedAt(now);
        internship.setRole(TeamRole.INTERN);

        return internshipMapper.toDto(internshipRepository.save(internship));
    }

    @Override
    public InternshipDto updateInternship(Long internshipId, UpdateInternshipDto internshipDto) {

        Long userId = userContext.getUserId();
        LocalDateTime now = LocalDateTime.now();
        Internship internship = internshipRepository.getByIdOrThrow(internshipId);
        List<TeamMember> teamMembers = internship.getInterns();


        if (!internshipDto.dismissedTeamMembersId().isEmpty()) {
            validateUpdate(teamMembers, internshipDto.dismissedTeamMembersId());
            List<TeamMember> dismissedList = teamMembers.stream()
                    .filter(teamMember -> internshipDto
                            .dismissedTeamMembersId()
                            .contains(
                                    teamMember.getUserId()
                            )).toList();
            internship.getInterns().removeAll(dismissedList);
            log.info("Досрочно уволены {}", dismissedList);
        }

        if (!internshipDto.aheadOfScheduleTeamMembersId().isEmpty()) {
            validateUpdate(teamMembers, internshipDto.aheadOfScheduleTeamMembersId());
            List<TeamMember> aheadList = teamMembers.stream()
                    .filter(teamMember -> internshipDto
                            .aheadOfScheduleTeamMembersId()
                            .contains(
                                    teamMember.getId()
                            )).toList();
            aheadList.forEach(teamMember -> teamMember.getRoles()
                    .add(internshipDto.role()));
            internship.getInterns().removeAll(aheadList);
            log.info("Досрочно прошли стажировку {}", aheadList);
        }
        internshipMapper.update(internshipDto, internship);
        internship.setUpdatedAt(now);
        internship.setUpdatedBy(userId);

        return internshipMapper.toDto(internshipRepository.save(internship));

    }

    @Override
    public List<InternshipDto> findInternships(SearchDto searchDto) {
        Stream<Internship> filtered = internshipRepository.findAll().stream();

        for (InternshipFilter filter : internshipFilters) {
            if (filter.isApplicable(searchDto)) {
                filtered = filter.apply(filtered, searchDto);
            }
        }

        return internshipMapper.toDtoList(filtered.toList());
    }

    @Override
    public List<InternshipDto> findAll() {
        return internshipMapper.toDtoList(internshipRepository.findAll());
    }

    @Override
    public InternshipDto findById(Long internshipId) {
        Internship internship = internshipRepository.getByIdOrThrow(internshipId);
        return internshipMapper.toDto(internship);
    }

    private void validateCreate(Project project, CreateInternshipDto internshipDto) {

        if (internshipDto.startDate().plusMonths(INTERNSHIP_DURATION_MONTHS)
                .isBefore(internshipDto.endDate())) {
            String message = "Стажировка не может быть дольше " + INTERNSHIP_DURATION_MONTHS +
                    "месяцев";
            log.warn(message);
            throw new DataValidationException(message);
        }

        Long mentorId = internshipDto.mentorId();
        List<Team> projectTeams = project.getTeams();
        List<Long> memberIds = projectTeams.stream()
                .flatMap(team -> team.getTeamMembers()
                        .stream().map(TeamMember::getUserId))
                .toList();

        if (!memberIds.contains(mentorId)) {
            String message = "Выбран ментор не из команды проекта";
            log.warn(message);
            throw new DataValidationException(message);
        }


    }

    private void validateUpdate(List<TeamMember> teamMembers, List<Long> idListDto) {
        List<Long> idListEntity = teamMembers.stream().map(TeamMember::getUserId).toList();
        if (!new HashSet<>(idListEntity).containsAll(idListDto)) {
            String message = "Список стажеров проекта не содержит всех выбранных";
            log.warn(message);
            throw new DataValidationException(message);

        }

    }


}
