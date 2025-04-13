package faang.school.projectservice.service.meet.implementations;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.meet.MeetCreateDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.dto.meet.MeetUpdateDto;
import faang.school.projectservice.exception.EntityAlreadyExistException;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.exception.UserNotFoundException;
import faang.school.projectservice.mapper.meet.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.specification.MeetSpecification;
import faang.school.projectservice.service.meet.interfaces.MeetService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetServiceImpl implements MeetService {
    private final MeetRepository meetRepository;
    private final ProjectRepository projectRepository;
    private final MeetMapper meetMapper;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final MeetSpecification meetSpecification;

    @Override
    @Transactional
    public MeetResponseDto createMeet(MeetCreateDto meetCreateDto) {
        long userId = userContext.getUserId();
        checkUser(userId);
        Project project = getProjectById(meetCreateDto.getProjectId());
        project.getMeets().stream()
                .filter(m -> m.getTitle().equals(meetCreateDto.getTitle()) &&
                        m.getStartsAt().equals(meetCreateDto.getStartsAt()) &&
                        m.getCreatorId() == userId)
                .findFirst().ifPresent(m -> {
                    throw new EntityAlreadyExistException(String.format("Meet with title %s already exists: meetId: %d",
                            m.getTitle(), m.getId()));
                });
        Meet meet = meetMapper.toEntity(meetCreateDto, userId);
        return meetMapper.toDto(meetRepository.save(meet));
    }

    @Override
    @Transactional
    public MeetResponseDto updateMeet(MeetUpdateDto meetUpdateDto) {
        checkUser(userContext.getUserId());
        Project project = getProjectById(meetUpdateDto.getProjectId());
        Meet meet = getMeetById(meetUpdateDto.getId());
        checkUserWithCreator(meet);
        meetMapper.updateMeetFromDto(meetUpdateDto, meet);
        return meetMapper.toDto(meetRepository.save(meet));
    }

    @Override
    @Transactional
    public void deleteMeet(long meetId) {
        checkUser(userContext.getUserId());
        Meet meet = getMeetById(meetId);
        checkUserWithCreator(meet);
        meetRepository.delete(meet);
    }

    @Override
    public MeetResponseDto getMeet(long meetId) {
        return meetMapper.toDto(getMeetById(meetId));
    }

    @Override
    public List<MeetResponseDto> getFilteredMeetsByProjectId(long projectId, MeetFilterDto meetFilterDto) {
        Specification<Meet> spec = meetSpecification.filterBy(
                projectId,
                meetFilterDto.getTitle(),
                meetFilterDto.getStartDate(),
                meetFilterDto.getEndDate());

        return meetMapper.toDto(meetRepository.findAll(spec));
    }

    private void checkUser(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            int statusCode = e.status();
            log.error("Error while fetching user: id={}, status={}, message={}",
                    userId, statusCode, e.getMessage(), e);
            switch (statusCode) {
                case 404:
                    throw new UserNotFoundException("User with id " + userId + " not found");
                case 400:
                    throw new IllegalArgumentException("Invalid user id: " + userId);
                case 500:
                    throw new RuntimeException("User service is unavailable. Try again later.");
                default:
                    throw new RuntimeException("Failed to fetch user: id=" + userId);
            }
        }
    }

    private Project getProjectById(long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Project not found: projectId: %d",
                        projectId)));
    }

    private Meet getMeetById(long meetId) {
        return meetRepository.findById(meetId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Meet not found: meetId: %d",
                        meetId)));
    }

    private void checkUserWithCreator(Meet meet) {
        if (userContext.getUserId() != meet.getCreatorId()) {
            throw new ForbiddenException("You are not allowed to update this meet: meetId: " + meet.getId());
        }
    }
}
