package faang.school.projectservice.service.internship;

import faang.school.projectservice.adapter.InternshipRepositoryAdapter;
import faang.school.projectservice.adapter.ProjectRepositoryAdapter;
import faang.school.projectservice.adapter.ScheduleRepositoryAdapter;
import faang.school.projectservice.adapter.TeamMemberRepositoryAdapter;
import faang.school.projectservice.adapter.TeamRepositoryAdapter;
import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.dto.internship.InternshipUserInformationDto;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
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
    public List<InternshipDto> getInternshipsWithFilters(InternshipFilterDto filters) {
        Stream<Internship> internships = internshipRepositoryAdapter.findAll();
//        internshipFilters.stream()
//                .filter(filter -> filter.isApplicable(filters))
//                .forEach(filter -> filter.apply(internship, filters));
//        return internshipMapper.toDto(internship.toList());
        return internshipFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(internships, (stream, filter) -> filter.apply(stream, filters),
                        (newStream, oldStream) -> newStream)
                .map(internshipMapper::toDto)
                .toList();
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
