package faang.school.projectservice.service.meet;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetTitleDateFilter;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.meet.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.teammember.TeamMemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class MeetService {
    private final MeetRepository meetRepository;
    private final UserContext userContext;
    private final MeetMapper meetMapper;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;

    @Transactional
    public MeetDto createMeet(long projectId, MeetDto meetDto) {
        long userId = userContext.getUserId();
        teamMemberService.validateUserIsProjectMember(userId, projectId);
        Meet meet = meetMapper.toEntity(meetDto);
        meet.setCreatorId(userId);
        meet.setProject(projectService.getById(projectId));
        Meet savedMeet = meetRepository.save(meet);
        log.info("New meet created: {}", savedMeet);
        return meetMapper.toDto(savedMeet);
    }

    @Transactional
    public MeetDto updateMeet(long meetId, UpdateMeetDto updateMeetDto) {
        long userId = userContext.getUserId();
        Meet meet = validateProjectMembershipAndGetMeet(userId, meetId);
        validateUserIsMeetCreatorWhenUpdate(meet, userId);
        meetMapper.updateMeetFromDto(updateMeetDto, meet);
        log.info("Updated meet: {}", meet);
        return meetMapper.toDto(meet);
    }

    @Transactional
    public MeetDto changeMeetStatus(long meetId, MeetStatus status) {
        long userId = userContext.getUserId();
        Meet meet = validateProjectMembershipAndGetMeet(userId, meetId);
        validateUserIsMeetCreatorWhenChangeStatus(meet, userId);
        meet.setStatus(status);
        log.info("Updated meet status to {} for meet {}", status, meet);
        return meetMapper.toDto(meet);
    }

    @Transactional
    public void addParticipant(long meetId, long participantId) {
        long userId = userContext.getUserId();
        Meet meet = validateProjectMembershipAndGetMeet(userId, meetId);
        validateUserIsMeetCreatorWhenManageParticipants(meet, userId, participantId);
        teamMemberService.validateUserIsProjectMember(participantId, meet.getProject().getId());
        if (!meet.getUserIds().contains(participantId)) {
            meet.getUserIds().add(participantId);
        }
    }

    @Transactional
    public void deleteParticipant(long meetId, long participantId) {
        long userId = userContext.getUserId();
        Meet meet = validateProjectMembershipAndGetMeet(userId, meetId);
        validateUserIsMeetCreatorWhenManageParticipants(meet, userId, participantId);
        meet.getUserIds().remove(participantId);
    }


    public List<MeetDto> getForProjectFilteredByTitleAndDateRange(long projectId, MeetTitleDateFilter filter) {
        return meetRepository
                .findAllForProjectFilterByTitlePatternAndDateRange
                        (
                                filter.getTitle(),
                                filter.getMinDate(),
                                filter.getMaxDate(),
                                projectId
                        )
                .stream()
                .map(meetMapper::toDto)
                .toList();
    }

    public List<MeetDto> getAllProjectMeets(long projectId) {
        teamMemberService.validateUserIsProjectMember(userContext.getUserId(), projectId);
        return meetRepository
                .findAllByProject(projectId)
                .stream()
                .map(meetMapper::toDto)
                .toList();
    }

    public MeetDto getById(long meetId) {
        long userId = userContext.getUserId();
        Meet meet = meetRepository.findById(meetId)
                .orElseThrow(() -> new EntityNotFoundException("Meet not found"));
        teamMemberService.validateUserIsProjectMember(userId, meet.getProject().getId());
        return meetMapper.toDto(meet);
    }

    private Meet validateProjectMembershipAndGetMeet(long userId, long meetId) {
        Meet meet = meetRepository.findById(meetId).orElseThrow(() -> {
            log.error("Meet with id {} not found", meetId);
            return new EntityNotFoundException("Meet not fount");
        });
        teamMemberService.validateUserIsProjectMember(userId, meet.getProject().getId());
        return meet;
    }

    private void validateUserIsMeetCreatorWhenUpdate(Meet meet, long userId) {
        if (meet.getCreatorId() != userId) {
            log.error("User with id {} tried to update meet {} but is not creator", userId, meet);
            throw new AccessDeniedException("Only creator can update meet");
        }
    }

    private void validateUserIsMeetCreatorWhenChangeStatus(Meet meet, long userId) {
        if (meet.getCreatorId() != userId) {
            log.error("User with id {} tried to change meet status to for meet {} but is not creator",
                    userId, meet);
            throw new AccessDeniedException("Only creator can change meet status");
        }
    }

    private void validateUserIsMeetCreatorWhenManageParticipants(Meet meet, long userId, long participantId) {
        if (meet.getCreatorId() != userId) {
            log.error("User with id {} tried to add participant with id {} for meet {} but is not creator",
                    userId, participantId, meet);
            throw new AccessDeniedException("Only creator can add participant to meet");
        }
    }
}
