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
import faang.school.projectservice.model.InternshipStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Schedule;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.validator.internship.InternshipServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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
        internshipServiceValidator.checkDataBeforeUpdate(internshipUpdateDto);
        Internship internship = internshipRepositoryAdapter.findById(internshipUpdateDto.getId());
        if (Objects.equals(internshipUpdateDto.getStatus(), InternshipStatus.COMPLETED)) { //completed
          for  (TeamMember teamMember : internship.getInterns()) {
               for (Stage stage : teamMember.getStages()) {
                   for (Task task : stage.getTasks()) {
//                       task.getStatus() == TaskStatus.DONE || task.getStatus() == TaskStatus.CANCELLED
                   }
               }
            }
            return null;
        }
//        return internshipMapper.toDto(InternshipUpdateDto);
//        нужно ли создавать новй маппер
        return null;
    }

    //    Обновить стажировку. Если стажировка завершена, то стажирующиеся должны получить новые роли на проекте,
//    если прошли, и быть удалены из списка участников проекта, если не прошли. Участник считается прошедшим стажировку,
//    если все запланированные задачи выполнены.
//    После старта стажировки нельзя добавлять новых стажёров. Стажировку можно пройти досрочно или досрочно быть уволенным.
    @Override
    public List<InternshipDto> getInternshipsWithFilters(InternshipFilterDto filters) {
        Stream<Internship> internships = internshipRepositoryAdapter.findAll();
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
