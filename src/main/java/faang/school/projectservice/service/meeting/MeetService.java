package faang.school.projectservice.service.meeting;

import faang.school.projectservice.dto.meeting.MeetDto;
import faang.school.projectservice.exception.AccessDeniedException;
import faang.school.projectservice.exception.ParticipantNotFoundException;
import faang.school.projectservice.mapper.moment.meeting.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.repository.adapter.meeting.MeetRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static faang.school.projectservice.model.MeetStatus.CANCELLED;
import static faang.school.projectservice.model.MeetStatus.PENDING;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetService {
    private final MeetRepository meetRepository;
    private final MeetMapper meetMapper;
    private final MeetRepositoryAdapter meetRepositoryAdapter;

    @Transactional
    public MeetDto create(long creatorId, MeetDto MeetDto) {
        Meet meet = meetMapper.toEntity(MeetDto);
        meet.setId(null);
        meet.setCreatorId(creatorId);
        meet.setStatus(PENDING);
        meet.setActive(true);
        LocalDateTime now = LocalDateTime.now();
        meet.setCreatedAt(now);
        meet.setUpdatedAt(now);

        Meet savedMeet = meetRepository.save(meet);
        return meetMapper.toDto(savedMeet);
    }

    @Transactional
    public MeetDto cancel(long meetId, long memberId) {
       Meet existing = meetRepositoryAdapter.fetchByIdOrThrow(meetId);
       assertCreator(existing, memberId);
       existing.setStatus(CANCELLED);
       existing.setActive(false);
       existing.setUpdatedAt(LocalDateTime.now());

        log.info("Meet {} cancelled by user {}", existing.getId(), memberId);
        return meetMapper.toDto(existing);
    }

    @Transactional
    public MeetDto removeParticipant(long meetId, long memberId, long participantId) {
        Meet meeting = meetRepositoryAdapter.fetchByIdOrThrow(meetId);
        assertCreator(meeting, memberId);

        boolean removed = meeting.getUserIds() != null &&
                meeting.getUserIds().remove(participantId);
        if (!removed) {
            throw new ParticipantNotFoundException(
                    "User " + participantId + " is not in meeting " + meetId);
        }

        meeting.setUpdatedAt(LocalDateTime.now());
        return meetMapper.toDto(meetRepository.save(meeting));
    }

    public MeetDto update(Long meetId, Long memberId, MeetDto updateMeetDto) {
        Meet existing  = meetRepositoryAdapter.fetchByIdOrThrow(meetId);
        assertCreator(existing, memberId);

        existing.setTitle(updateMeetDto.getTitle());
        existing.setDescription(updateMeetDto.getDescription());
        existing.setStartsAt(updateMeetDto.getScheduledAt());
        existing.setStatus(Optional.ofNullable(updateMeetDto.getStatus())
                .map(MeetStatus::valueOf)
                .orElse(existing.getStatus())
        );
        existing.setUpdatedAt(LocalDateTime.now());

        Meet savedMeet = meetRepository.save(existing);
        log.debug("Meet updated: {} by user {}", savedMeet.getId(), memberId);
        return meetMapper.toDto(savedMeet);

    }

    public MeetDto findById(long meetId) {
        return meetMapper.toDto(meetRepositoryAdapter.fetchByIdOrThrow(meetId));
    }

    public List<MeetDto> findAll() {
        return meetMapper.toDtoList(meetRepository.findAll());
    }

    public List<MeetDto> findProjectMeets(long projectId,
                                          Optional<String> titleContains,
                                          Optional<LocalDateTime> from,
                                          Optional<LocalDateTime> to) {
        List<Meet> meets = meetRepository.findAll().stream()
                .filter(m -> Objects.equals(m.getProject().getId(), projectId))
                .filter(m -> titleContains.map(t -> m.getTitle().toLowerCase().contains(t.toLowerCase())).orElse(true))
                .filter(m -> from.map(f -> !m.getScheduledAt().isBefore(f)).orElse(true))
                .filter(m -> to.map(t -> !m.getScheduledAt().isAfter(t)).orElse(true))
                .toList();
        return meetMapper.toDtoList(meets);
    }

    private void assertCreator(Meet meet, long memberId) {
        if (!memberIdEquals(meet.getCreatorId(), memberId)) {
            throw new AccessDeniedException("Only the meeting creator can modify/cancel it");
        }
    }

    private boolean memberIdEquals(Long firstMember, Long secondMember) {
        return Objects.equals(firstMember, secondMember);
    }
}
