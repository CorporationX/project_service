package faang.school.projectservice.service.meet;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.PageRequestDto;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.exception.ResourceNotFoundException;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.meet.Meet;
import faang.school.projectservice.model.meet.MeetStatus;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.meet.filter.MeetFilter;
import faang.school.projectservice.service.team.TeamService;
import faang.school.projectservice.validator.meet.MeetValidator;
import faang.school.projectservice.validator.project.ProjectValidator;
import faang.school.projectservice.validator.team.TeamValidator;
import faang.school.projectservice.validator.user.UserValidator;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@Builder
@RequiredArgsConstructor
@Slf4j
public class MeetService {

    private final TeamService teamService;
    private final ProjectRepository projectRepository;
    private final MeetRepository meetRepository;
    private final MeetMapper meetMapper;
    private final List<MeetFilter> meetFilters;
    private final UserContext userContext;
    private final UserValidator userValidator;
    private final ProjectValidator projectValidator;
    private final TeamValidator teamValidator;
    private final MeetValidator meetValidator;


    @Transactional
    public MeetDto create(MeetDto meetDto) {
        userValidator.isUserActive(userContext.getUserId());

        Project project = projectRepository.getProjectById(meetDto.getProjectId());

        TeamDto team = teamService.getById(meetDto.getTeamId());
        teamValidator.doesTeamHaveAnUser(team, userContext.getUserId());

        Meet meetToCreate = meetMapper.toEntity(meetDto);
        meetToCreate.setTeam(Team.builder().id(team.getId()).build());
        meetToCreate.setProject(project);
        meetToCreate.setCreatedBy(userContext.getUserId());
        meetToCreate.setStatus(MeetStatus.PLANNED);

        Meet savedMeet = meetRepository.save(meetToCreate);
        return meetMapper.toDto(savedMeet);
    }

    @Transactional
    public MeetDto update(MeetDto meet) {
        Meet meetToUpdate = meetRepository.findById(meet.getId())
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Meet %s id doesn't exist", meet.getId())));

        meetValidator.userIsMeetCreator(userContext.getUserId(), meetToUpdate.getCreatedBy());

        meetToUpdate.setTitle(meet.getTitle());
        meetToUpdate.setDescription(meet.getDescription());
        meetToUpdate.setStatus(meet.getStatus());
        meetToUpdate.setStartDate(meet.getStartDate());
        meetToUpdate.setEndDate(meet.getEndDate());

        Meet updatedMeet = meetRepository.save(meetToUpdate);

        return meetMapper.toDto(updatedMeet);
    }

    @Transactional
    public void delete(Long id) {
        long userId = userContext.getUserId();
        MeetDto meet = getById(id);
        meetValidator.userIsMeetCreator(userId, meet.getCreatedBy());
        meetRepository.deleteById(meet.getId());
    }

    @Transactional
    public MeetDto getById(Long id) {
        Meet foundedMeet = meetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Meet %s id doesn't exist", id)));

        return meetMapper.toDto(foundedMeet);
    }

    @Transactional
    public List<MeetDto> getProjectMeetsByFilter(Long projectId, MeetFilterDto meetFilter) {
        projectValidator.existsProject(projectId);

        List<MeetDto> meets = meetMapper.toDtoList(meetRepository.getByProjectId(projectId));

        return meetFilters.stream()
                .filter(filter -> filter.isApplicable(meetFilter))
                .reduce(meets.stream(),
                        (meetsStream, filter) -> filter.apply(meetsStream, meetFilter),
                        Stream::concat)
                .toList();
    }

    @Transactional
    public Page<MeetDto> getAllPageable(PageRequestDto pageRequest) {
        Sort sort = pageRequest.getSortDirection().equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(pageRequest.getSortBy()).ascending() :
                Sort.by(pageRequest.getSortBy()).descending();

        Pageable pageable = PageRequest.of(pageRequest.getPage(), pageRequest.getSize(), sort);
        return meetRepository.findAll(pageable).map(meetMapper::toDto);
    }
}
