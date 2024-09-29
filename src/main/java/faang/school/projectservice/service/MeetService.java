package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.google.calendar.GoogleCalendarService;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.CalendarEventMapper;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.specification.MeetSpecificationBuilder;
import faang.school.projectservice.validator.MeetValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final UserContext userContext;
    private final MeetValidator validator;
    private final GoogleCalendarService googleCalendarService;
    private final MeetRepository meetRepository;
    private final ProjectRepository projectRepository;
    private final MeetMapper meetMapper;
    private final CalendarEventMapper calendarEventMapper;

    @Transactional
    public MeetDto create(MeetDto meetDto) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        validator.validateUserIsCreator(userId, meetDto.getCreatorId());

        Project project = projectRepository.findById(meetDto.getProjectId());
        long googleEventId = googleCalendarService.createEvent(calendarEventMapper.toCalendarEventDto(meetDto));
        Meet meet = buildMeet(meetDto, project, googleEventId);
        return meetMapper.toDto(meetRepository.save(meet));
    }

    @Transactional
    public MeetDto update(long meetId, MeetDto meetDto) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        validator.validateUserIsCreator(userId, meetDto.getCreatorId());
        validator.validateIdFromPath(meetId, meetDto.getId());
        Meet meet = findMeet(meetId);

        meetDto.setId(meetId);
        if (meetMapper.toDto(meet).equals(meetDto)) {
            return meetDto;
        }

        googleCalendarService.update(calendarEventMapper.toCalendarEventDto(meetDto));

        meet.setTitle(meetDto.getTitle());
        meet.setDescription(meetDto.getDescription());
        meet.setStartDate(meetDto.getStartDate());
        meet.setEndDate(meetDto.getEndDate());
        meet.setStatus(meetDto.getStatus());
        meet.setUserIds(meetDto.getUserIds());
        log.info(String.format("Meet id = %d, title = \"%s\" was updated at %s",
                meetId, meetDto.getTitle(), LocalDateTime.now().format(DATE_TIME_FORMATTER)));
        return meetMapper.toDto(meetRepository.save(meet));
    }

    @Transactional
    public void delete(Long meetId) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);

        Meet meet = findMeet(meetId);
        googleCalendarService.delete(meet.getGoogleEventId());
        validator.validateUserIsCreator(userId, meet.getCreatorId());
        meetRepository.delete(meet);
    }

    public List<MeetDto> getByFilter(MeetFilterDto filterDto) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        Specification<Meet> spec = MeetSpecificationBuilder.buildSpecification(filterDto);
        return meetMapper.toDtoList(meetRepository.findAll(spec));
    }

    public Page<MeetDto> getAll(Pageable pageable) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int offset = pageNumber * pageSize;

        List<Meet> meets = meetRepository.findAllWithLimitAndOffset(pageSize, offset);
        long total = meetRepository.count();
        return new PageImpl<>(meetMapper.toDtoList(meets), pageable, total);
    }

    public MeetDto getById(Long meetId) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        Meet meet = findMeet(meetId);
        return meetMapper.toDto(meet);
    }

    private Meet buildMeet(MeetDto meetDto, Project project, long googleEventId) {
        return Meet.builder()
                .title(meetDto.getTitle())
                .description(meetDto.getDescription())
                .startDate(meetDto.getStartDate())
                .endDate(meetDto.getEndDate())
                .project(project)
                .creatorId(meetDto.getCreatorId())
                .status(MeetStatus.PENDING)
                .userIds(meetDto.getUserIds())
                .googleEventId(googleEventId)
                .build();
    }

    private Meet findMeet(long meetId) {
        return meetRepository.findById(meetId).orElseThrow(
                () -> new NoSuchElementException(String.format("Meet with id = %d not found", meetId)));
    }
}

