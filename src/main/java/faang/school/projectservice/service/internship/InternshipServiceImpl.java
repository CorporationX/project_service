package faang.school.projectservice.service.internship;

import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.dto.client.internship.InternshipFilterDto;
import faang.school.projectservice.dto.client.internship.InternshipUpdateDto;
import faang.school.projectservice.dto.client.internship.InternshipUserInformationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.mapper.internship.InternshipMapper;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.InternshipRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.ScheduleRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class InternshipServiceImpl implements InternshipService {

    private final InternshipRepository internshipRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final ScheduleRepository scheduleRepository;
    private final InternshipMapper internshipMapper;
    private final List<InternshipFilter> internshipFilters;

    @Override
    public InternshipDto createInternship(InternshipDto internshipDto) {
        checkInternshipBeforeCreate(internshipDto);
//        дописать адаптер как прослойку получения данных между сервисом и репозиторием
        Project project = projectRepository.findById(internshipDto.getProjectId()).orElseThrow();

        // а потом получить id и тут записать
//        for (Long id : internshipDto.getInternsId()) {
//            List<TeamRole> teamRoles = new ArrayList<>();
//            teamRoles.add(TeamRole.INTERN);
//            TeamMember teamMember = new TeamMember();
//            teamMember.setTeam(team);
//            teamMember.setRoles(teamRoles);
//            teamMember.setUserId(id);
//            teamMembers.add(teamMember);
//        }
//        for 1 project list of teams
//        create in TeamRepository
//        create in TeamMemberRepository
//        create in InternshipRepository
//        Тут сохранить сначала команду, а потом в команду
        List<TeamMember> interns = new ArrayList<>();
        Team team = new Team();
        team.setProject(project);
        team.setTeamMembers(new ArrayList<>());
        teamRepository.save(team); //Вынести может в отдельный сервис? Команду и участников команды
        for (InternshipUserInformationDto internshipUserInformationDto : internshipDto.getInterns()) {
            TeamMember teamMember = new TeamMember();
            teamMember.setTeam(team);
            teamMember.setUserId(internshipUserInformationDto.getId());
            teamMember.setNickname(internshipUserInformationDto.getNickname());
            List<TeamRole> teamRoles = new ArrayList<>();
            teamRoles.add(TeamRole.INTERN);
            teamMember.setRoles(teamRoles);
            teamMemberRepository.save(teamMember);
            interns.add(teamMember);
        }

        Schedule schedule = scheduleRepository.findById(internshipDto.getScheduleId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("Schedule с id: %s не найден!", internshipDto.getScheduleId())));
        Internship internship = internshipMapper.toEntity(internshipDto);
        internship.setProject(project);
        internship.setSchedule(schedule);
        internship.setInterns(interns);
        internshipRepository.save(internship);
        return internshipMapper.toDto(internship);
    }

    @Override
    public InternshipUpdateDto updateInternship(InternshipUpdateDto internshipUpdateDto) {
//        Internship internship = internshipRepository.findById(internshipUpdateDto.getId()).orElseThrow(()-> throw new IllegalArgumentException("")));
//                getReferenceById(internshipUpdateDto.getId());

//        return internshipMapper.toDto(InternshipUpdateDto);
        return null;
    }

    @Override
    public List<InternshipDto> getInternshipsWithFilters(InternshipFilterDto filters) {
        Stream<Internship> internship = internshipRepository.findAll().stream();
        internshipFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .forEach(filter -> filter.apply(internship, filters));
        return internshipMapper.toDto(internship.toList());
    }

    @Override
    public List<InternshipDto> getAllInternships() {
        List<Internship> internships = internshipRepository.findAll().stream().toList();
        return internshipMapper.toDto(internships);
    }

    @Override
    public InternshipDto getInternship(Long id) {
        Internship internship = internshipRepository.findById(id).orElseThrow();
        return internshipMapper.toDto(internship);
    }

    private void checkInternshipBeforeCreate(InternshipDto internshipDto) {
        Project project = getProjectById(internshipDto.getProjectId());
        ProjectStatus projectStatus = project.getStatus();
        if (Objects.equals(projectStatus, ProjectStatus.ON_HOLD) ||
                Objects.equals(projectStatus, ProjectStatus.CANCELLED) ||
                Objects.equals(projectStatus, ProjectStatus.COMPLETED)) {
            throw new DataValidationException(String.format("It is not possible to add an internship to a project " +
                    "with the status: %s", projectStatus));
        }
//        adapter
        TeamMember mentor = teamMemberRepository.findById(internshipDto.getMentorId())
                .orElseThrow(() -> new IllegalArgumentException(String.format("User с id: %s not found!", internshipDto.getMentorId())));

         if ( teamMemberRepository.findByUserIdAndProjectId(internshipDto.getMentorId(),project.getId()) == null){
                         throw new DataValidationException(String.format("Mentor with id %d not from project %d team",
                    internshipDto.getMentorId(), internshipDto.getProjectId()));
         }
//
//        mentor.getTeam()
//        List<TeamMember> teamMemberList =
//
//        if (project.getTeams().stream().flatMap(team -> team.getTeamMembers().stream())
//                .filter(teamMember -> Objects.equals(teamMember, mentor)).findAny().orElse(null) != null) {
//            throw new DataValidationException(String.format("Mentor with id %d not from project %d team",
//                    internshipDto.getMentorId(), internshipDto.getProjectId()));
//        }
    }

    private Project getProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow();
    }
}
