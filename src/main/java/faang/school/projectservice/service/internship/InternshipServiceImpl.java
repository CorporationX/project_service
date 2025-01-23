package faang.school.projectservice.service.internship;

import faang.school.projectservice.adapter.*;
import faang.school.projectservice.dto.internship.*;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.validator.internship.InternshipServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InternshipServiceImpl implements InternshipService {

    private final InternshipServiceValidator internshipServiceValidator;
    private final InternshipRepositoryAdapter internshipRepositoryAdapter;
    private final ProjectRepositoryAdapter projectRepositoryAdapter;
    private final TeamMemberRepositoryAdapter teamMemberRepositoryAdapter;
    private final TeamRepositoryAdapter teamRepositoryAdapter;
    private final ScheduleRepositoryAdapter scheduleRepositoryAdapter;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> internshipFilters;

    @Override
    public InternshipDto createInternship(InternshipDto internshipDto) {
        internshipServiceValidator.checkDataBeforeCreate(internshipDto);

        Project project = projectRepositoryAdapter.findById(internshipDto.getProjectId());

        List<TeamMember> interns = new ArrayList<>();
        Team team = new Team();
        team.setProject(project);
        team.setTeamMembers(new ArrayList<>());
        teamRepositoryAdapter.save(team); //Вынести может в отдельный сервис? Команду и участников команды
        for (InternshipUserInformationDto internshipUserInformationDto : internshipDto.getInterns()) {
            TeamMember teamMember = new TeamMember();
            teamMember.setTeam(team);
            teamMember.setUserId(internshipUserInformationDto.getUserId());
            teamMember.setNickname(internshipUserInformationDto.getNickname());
            List<TeamRole> teamRoles = new ArrayList<>();
            teamRoles.add(TeamRole.INTERN);
            teamMember.setRoles(teamRoles);
            teamMemberRepositoryAdapter.save(teamMember);
            interns.add(teamMember);
        }

        Schedule schedule = scheduleRepositoryAdapter.findById(internshipDto.getScheduleId());
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setProject(project);
        internship.setSchedule(schedule);
        internship.setInterns(interns);
        internshipRepositoryAdapter.save(internship);
        return internshipMapper.toDto(internship);
    }

    @Override
    public InternshipUpdateDto updateInternship(InternshipUpdateDto internshipUpdateDto) {
//        Internship internship = internshipRepositoryAdapter.findById(id);

//        return internshipMapper.toDto(InternshipUpdateDto);
        return null;
    }

    @Override
    public List<InternshipDto> getProjectInternshipsWithFilters(Long projectId, InternshipFilterDto filters) {
//        todo not work correctly
        Stream<Internship> internship = internshipRepositoryAdapter.findAllByProjectId(projectId);
        internshipFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(internship, filters));
        return internshipMapper.toDto(internship.toList());
    }

    @Override
    public List<InternshipDto> getAllInternships() {
        List<Internship> internships = internshipRepositoryAdapter.findAll().toList();
        return internshipMapper.toDto(internships);
    }

    @Override
    public InternshipDto getInternship(Long id) {
        Internship internship = internshipRepositoryAdapter.findById(id);
        return internshipMapper.toDto(internship);
    }

}
