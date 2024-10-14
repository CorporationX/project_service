package faang.school.projectservice.service.impl;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.app.AppConfig;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.model.dto.UserDto;
import faang.school.projectservice.model.dto.CalendarEventDto;
import faang.school.projectservice.model.dto.ZonedDateTimeDto;
import faang.school.projectservice.model.dto.MeetDto;
import faang.school.projectservice.model.dto.MeetFilterDto;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.mapper.calendar.CalendarMeetDtoEventDtoMapper;
import faang.school.projectservice.model.entity.Meet;
import faang.school.projectservice.model.enums.MeetStatus;
import faang.school.projectservice.model.entity.Project;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.GoogleCalendarService;
import faang.school.projectservice.service.MeetService;
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

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetServiceImpl implements MeetService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final UserContext userContext;
    private final MeetValidator validator;
    private final GoogleCalendarService
            googleCalendarService;
    private final MeetRepository meetRepository;
    private final ProjectRepository projectRepository;
    private final MeetMapper meetMapper;
    private final CalendarMeetDtoEventDtoMapper calendarMeetDtoEventDtoMapper;
    private final UserServiceClient userServiceClient;
    private final AppConfig appConfig;

    @Override
    @Transactional
    public MeetDto create(MeetDto meetDto) throws GeneralSecurityException, IOException {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        validator.validateUserIsCreator(userId, meetDto.getCreatorId());

        if (meetDto.getUserIds() != null && !meetDto.getUserIds().isEmpty()) {
            setAttendeeEmails(meetDto);
        }

        Project project = projectRepository.findById(meetDto.getProjectId());
        CalendarEventDto calendarEventDto = googleCalendarService.createEvent(calendarMeetDtoEventDtoMapper.toCalendarEventDto(meetDto));
        Meet meet = buildMeet(meetDto, project, calendarEventDto.getId());
        return meetMapper.toDto(meetRepository.save(meet));
    }

    @Override
    @Transactional
    public MeetDto update(long meetId, MeetDto meetDto) throws GeneralSecurityException, IOException {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        validator.validateUserIsCreator(userId, meetDto.getCreatorId());
        validator.validateIdFromPath(meetId, meetDto.getId());
        Meet meet = findMeet(meetId);

        meetDto.setId(meetId);
        if (meetMapper.toDto(meet).equals(meetDto)) {
            return meetDto;
        }

        googleCalendarService.update(calendarMeetDtoEventDtoMapper.toCalendarEventDto(meetDto));
        ZonedDateTimeDto startDate = meetDto.getStartDate();
        ZonedDateTimeDto endDate = meetDto.getEndDate();
        meet.setTitle(meetDto.getTitle());
        meet.setDescription(meetDto.getDescription());
        meet.setStartDate(startDate.getLocalDateTime());
        meet.setEndDate(endDate.getLocalDateTime());
        meet.setStatus(meetDto.getStatus());
        meet.setUserIds(meetDto.getUserIds());
        log.info(String.format("Meet id = %d, title = \"%s\" was updated at %s",
                meetId, meetDto.getTitle(), LocalDateTime.now().format(DATE_TIME_FORMATTER)));
        return meetMapper.toDto(meetRepository.save(meet));
    }

    @Override
    @Transactional
    public void delete(Long meetId) throws GeneralSecurityException, IOException {
        long userId = userContext.getUserId();
        validator.validateUser(userId);

        Meet meet = findMeet(meetId);
        googleCalendarService.delete(meet.getCalendarEventId());
        validator.validateUserIsCreator(userId, meet.getCreatorId());
        meetRepository.delete(meet);
    }

    @Override
    public List<MeetDto> getByFilter(MeetFilterDto filterDto) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        Specification<Meet> spec = MeetSpecificationBuilder.buildSpecification(filterDto);
        return meetMapper.toDtoList(meetRepository.findAll(spec));
    }

    @Override
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

    @Override
    public MeetDto getById(Long meetId) {
        long userId = userContext.getUserId();
        validator.validateUser(userId);
        Meet meet = findMeet(meetId);
        return meetMapper.toDto(meet);
    }

    private Meet buildMeet(MeetDto meetDto, Project project, String calendarEventId) {
        ZonedDateTimeDto startDate = meetDto.getStartDate();
        ZonedDateTimeDto endDate = meetDto.getEndDate();
        return Meet.builder()
                .title(meetDto.getTitle())
                .description(meetDto.getDescription())
                .startDate(startDate.getLocalDateTime())
                .endDate(endDate.getLocalDateTime())
                .project(project)
                .creatorId(meetDto.getCreatorId())
                .status(MeetStatus.TENTATIVE)
                .userIds(meetDto.getUserIds())
                .calendarEventId(calendarEventId)
                .build();
    }

    private Meet findMeet(long meetId) {
        return meetRepository.findById(meetId).orElseThrow(
                () -> new NoSuchElementException(String.format("Meet with id = %d not found", meetId)));
    }

    private void setAttendeeEmails(MeetDto meetDto) {
        List<UserDto> users = userServiceClient.getUsersByIds(meetDto.getUserIds());
        List<String> usersEmails = users.stream()
                .map(UserDto::getEmail)
                .toList();
        meetDto.setAttendeeEmails(usersEmails);
    }
}

