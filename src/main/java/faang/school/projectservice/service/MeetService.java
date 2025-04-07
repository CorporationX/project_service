package faang.school.projectservice.service;

import faang.school.projectservice.dto.MeetDto;
import faang.school.projectservice.exception.MeetNotFoundException;
import faang.school.projectservice.exception.MeetWrongCreatorException;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.repository.MeetRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetService {
    private final MeetRepository meetRepository;
    private final MeetMapper meetMapper;

    public List<MeetDto> getAllMeets() {
        List<Meet> meets = meetRepository.findAll();

        if (meets.isEmpty()) {
            log.info("No meets found");
            return List.of();
        }

        return meets.stream()
                .map(meetMapper::toDto)
                .toList();
    }

    public List<MeetDto> getMeetsByFilter(String title, LocalDateTime startDate) {
        List<Meet> meets;

        if (title != null && !title.isEmpty() && startDate != null) {
            meets = meetRepository.findByTitleContainingIgnoreCaseAndStartsAt(title, startDate);
        } else if (title != null && !title.isEmpty()) {
            meets = meetRepository.findByTitleContainingIgnoreCase(title);
        } else if (startDate != null) {
            meets = meetRepository.findByStartsAt(startDate);
        } else {
            meets = meetRepository.findAll();
        }

        return meets.stream()
                .map(meetMapper::toDto)
                .toList();
    }

    public MeetDto getMeetById(long id) {
        Meet meet = meetRepository.findById(id)
                .orElseThrow(() -> meetNotFoundException(id));
        return meetMapper.toDto(meet);
    }

    public MeetDto createMeet(@NotNull MeetDto meetDto) {
        Meet meet = meetMapper.toEntity(meetDto);
        meet.setStatus(MeetStatus.PENDING);
        meet = meetRepository.save(meet);
        log.info("Meet with id {} created", meet.getId());
        return meetMapper.toDto(meet);
    }

    public MeetDto updateMeet(long id, @NotNull MeetDto meetDto, long userId) {
        Meet meet = meetRepository.findById(id)
                .orElseThrow(() -> meetNotFoundException(id));
        if (meet.getCreatorId() != meetDto.creatorId()) {
            throw meetWrongCreatorException(id, userId);
        }
        meet.setTitle(meetDto.title());
        meet.setDescription(meetDto.description());
        meet.setStartsAt(meetDto.startsAt());
        meet.setUpdatedAt(LocalDateTime.now());
        meet = meetRepository.save(meet);
        return meetMapper.toDto(meet);
    }

    public MeetDto cancelMeet(long id, long userId) {
        Meet meet = meetRepository.findById(id)
                .orElseThrow(() -> meetNotFoundException(id));
        if (meet.getCreatorId() != userId) {
            throw meetWrongCreatorException(id, userId);
        }
        meet.setStatus(MeetStatus.CANCELLED);
        meet = meetRepository.save(meet);
        return meetMapper.toDto(meet);
    }

    public void deleteMeet(long id, long userId) {
        Meet meet = meetRepository.findById(id).orElseThrow(() -> meetNotFoundException(id));
        if (meet.getCreatorId() != userId) {
            throw meetWrongCreatorException(id, userId);
        }
        meetRepository.deleteById(id);
    }

    private MeetNotFoundException meetNotFoundException(long id) {
        log.error("Meet with id {} not found", id);
        return new MeetNotFoundException(id);
    }

    private MeetWrongCreatorException meetWrongCreatorException(long id, long userId) {
        log.error("Meet with id {} is not created by user with id {}", id, userId);
        return new MeetWrongCreatorException("Only creator can update the meet");
    }
}
