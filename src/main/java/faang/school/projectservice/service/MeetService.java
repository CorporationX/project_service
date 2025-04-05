package faang.school.projectservice.service;

import faang.school.projectservice.dto.MeetDto;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.repository.MeetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetService {
    private final MeetRepository meetRepository;
    private final MeetMapper meetMapper;

    public List<MeetDto> getAllMeets() {
        List<Meet> meets = meetRepository.findAll();
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
                .collect(Collectors.toList());
    }

    public MeetDto getMeetById(long id) {
        Meet meet = meetRepository.findById(id)
                .orElseThrow(() -> meetNotFoundException(id));
        return meetMapper.toDto(meet);
    }

    public MeetDto createMeet(MeetDto meetDto) {
        System.out.println("meetDto from service: " + meetDto);
        Meet meet = meetMapper.toEntity(meetDto);
        meet.setStatus(MeetStatus.PENDING);
        meet = meetRepository.save(meet);
        return meetMapper.toDto(meet);
    }

    public MeetDto updateMeet(long id, MeetDto meetDto) {
        Meet meet = meetRepository.findById(id)
                .orElseThrow(() -> meetNotFoundException(id));
        if (meet.getCreatorId() != meetDto.creatorId()) {
            log.error("Meet with id {} is not created by user with id {}", id, meetDto.creatorId());
            throw new IllegalArgumentException("Only creator can update the meet");
        }
        meet.setTitle(meetDto.title());
        meet.setDescription(meetDto.description());
        meet.setStartsAt(meetDto.startsAt());
        meet.setUpdatedAt(LocalDateTime.now());
        meet = meetRepository.save(meet);
        return meetMapper.toDto(meet);
    }

    public MeetDto cancelMeet(long id) {
        Meet meet = meetRepository.findById(id)
                .orElseThrow(() -> meetNotFoundException(id));
        meet.setStatus(MeetStatus.CANCELLED);
        meet = meetRepository.save(meet);
        return meetMapper.toDto(meet);
    }

    public void deleteMeet(long id) {
        meetRepository.deleteById(id);
    }

    private IllegalArgumentException meetNotFoundException(long id) {
        log.error("Meet with id {} not found", id);
        return new IllegalArgumentException("Meet not found");
    }
}
