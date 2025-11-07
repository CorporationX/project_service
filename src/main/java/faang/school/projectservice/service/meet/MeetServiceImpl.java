package faang.school.projectservice.service.meet;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.meet.CreateMeetDto;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.filter.meet.MeetFilter;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetServiceImpl implements MeetService {

    private final MeetRepository meetRepository;
    private final MeetMapper meetMapper;
    private final ProjectRepository projectRepository;
    private final UserContext userContext;
    private final List<MeetFilter> meetFilters;

    @Override
    public MeetDto create(CreateMeetDto createMeetDto) {
        Project project = getProjectById(createMeetDto.projectId());

        Meet meet = meetMapper.toMeet(createMeetDto);
        meet.setProject(project);
        meet.setCreatorId(userContext.getUserId());
        meet.setStatus(MeetStatus.PENDING);

        Meet savedMeet = meetRepository.save(meet);
        log.info("Meet {} has been created", savedMeet.getId());
        return meetMapper.toMeetDto(savedMeet);
    }

    @Override
    public MeetDto update(long meetId, UpdateMeetDto updateMeetDto) {
        Meet meet = getMeetById(meetId);

        creatorValidator(meet);
        statusValidator(meet);

        meetMapper.update(updateMeetDto, meet);

        if (updateMeetDto.projectId() != null) {
            Project project = getProjectById(updateMeetDto.projectId());
            meet.setProject(project);
        }

        meetRepository.save(meet);
        log.info("Meet {} has been updated", meet.getId());
        return meetMapper.toMeetDto(meet);
    }

    @Override
    public MeetDto cancel(long meetId) {
        Meet meet = getMeetById(meetId);

        creatorValidator(meet);
        statusValidator(meet);

        meet.setStatus(MeetStatus.CANCELLED);
        meetRepository.save(meet);
        log.info("Meet {} has been canceled", meet.getId());

        return meetMapper.toMeetDto(meet);
    }

    @Override
    public void delete(long meetId) {
        Meet meet = getMeetById(meetId);

        creatorValidator(meet);

        meetRepository.delete(meet);
        log.info("Meet {} has been deleted", meet.getId());
    }

    @Override
    public List<MeetDto> getAll() {
        return meetRepository
                .findAll()
                .stream()
                .map(meetMapper::toMeetDto)
                .toList();
    }

    @Override
    public MeetDto getById(long meetId) {
        return meetMapper.toMeetDto(meetRepository.getByIdOrThrow(meetId));
    }

    @Override
    public List<MeetDto> getByFilters(MeetFilterDto meetFilterDto) {
        Stream<Meet> meetStream = meetRepository.findAll().stream();

        for (MeetFilter meetFilter : meetFilters) {
            if (meetFilter.isApplicable(meetFilterDto)) {
                meetStream = meetFilter.apply(meetStream, meetFilterDto);
            }
        }
        return meetStream.map(meetMapper::toMeetDto).toList();
    }

    private void creatorValidator(Meet meet) {
        if (userContext.getUserId() != meet.getCreatorId()) {
            String errorMessage = "Not allowed. You are not a creator of meet %d".formatted(meet.getId());
            log.error(errorMessage);
            throw new ForbiddenException(errorMessage);
        }
    }

    private void statusValidator(Meet meet) {
        if (!meet.getStatus().equals(MeetStatus.PENDING)) {
            String errorMessage = "Not allowed. Meet %d not in status '%s'"
                    .formatted(meet.getId(), MeetStatus.PENDING.name());
            log.error(errorMessage);
            throw new DataValidationException(errorMessage);
        }
    }

    private Meet getMeetById(long meetId) {
        Meet meet = meetRepository.getByIdOrThrow(meetId);
        log.debug("Got meet {} from base", meet.getId());

        return meet;
    }

    private Project getProjectById(long projectId) {
        Project project = projectRepository.getByIdOrThrow(projectId);
        log.debug("Got project {} from base", project.getId());

        return project;
    }
}