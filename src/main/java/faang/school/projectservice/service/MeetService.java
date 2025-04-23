package faang.school.projectservice.service;

import faang.school.projectservice.dto.MeetDto;
import faang.school.projectservice.exception.MeetNotFoundException;
import faang.school.projectservice.exception.MeetWrongCreatorException;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.repository.MeetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        Meet meet = getMeet(id);
        return meetMapper.toDto(meet);
    }

    public MeetDto createMeet(MeetDto meetDto) {
        Meet meet = meetMapper.toEntity(meetDto);
        meet.setStatus(MeetStatus.PENDING);
        Meet createdMeet = meetRepository.save(meet);
        log.info("Meet with id {} created", meet.getId());
        return meetMapper.toDto(createdMeet);
    }

    public MeetDto updateMeet(long id, MeetDto meetDto, long userId) {
        Meet meet = getMeet(id);
        if (meet.getCreatorId() != meetDto.creatorId()) {
            meetWrongCreatorException(id, userId);
        }
        meet.setTitle(meetDto.title());
        meet.setDescription(meetDto.description());
        meet.setStartsAt(meetDto.startsAt());
        meet.setUpdatedAt(LocalDateTime.now());
        Meet updatedMeet = meetRepository.save(meet);
        return meetMapper.toDto(updatedMeet);
    }

    public MeetDto cancelMeet(long id, long userId) {
        Meet meet = getMeet(id);
        if (meet.getCreatorId() != userId) {
            meetWrongCreatorException(id, userId);
        }
        meet.setStatus(MeetStatus.CANCELLED);
        Meet canceledMeet = meetRepository.save(meet);
        return meetMapper.toDto(canceledMeet);
    }

    public void deleteMeet(long id, long userId) {
        Meet meet = getMeet(id);
        if (meet.getCreatorId() != userId) {
            meetWrongCreatorException(id, userId);
        }
        meetRepository.deleteById(id);
    }

    private void meetNotFoundException(long id) {
        log.error("Meet with id {} not found", id);
        throw new MeetNotFoundException(id);
    }

    private void meetWrongCreatorException(long id, long userId) {
        log.error("Meet with id {} is not created by user with id {}", id, userId);
        throw new MeetWrongCreatorException("Only creator can update the meet");
    }

    private Meet getMeet(long id) {
        Optional<Meet> optionalMeet = meetRepository.findById(id);
        if (optionalMeet.isEmpty()) {
            meetNotFoundException(id);
        }
        return optionalMeet.get();
    }
}
