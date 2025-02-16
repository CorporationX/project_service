package faang.school.projectservice.service;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.meet.MeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.exception.DataNotFoundException;
import faang.school.projectservice.filter.meet.MeetFilter;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validation.MeetValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class MeetService {
    private final MeetRepository meetRepository;
    private final MeetValidation meetValidation;
    private final MeetMapper meetMapper;
    private final UserContext userContext;
    private final List<MeetFilter> filters;

    public MeetDto createMeet(MeetDto meet) {
        meetValidation.validationMeet(meet);
        Meet meetEntity = meetMapper.toEntity(meet);
        meetEntity.setCreatedAt(LocalDateTime.now());

        Meet savedMeet = meetRepository.save(meetEntity);
        log.info("Meeting created idMeet: {}", savedMeet.getId());
        return meetMapper.toDto(savedMeet);
    }

    public MeetDto updateMeet(MeetDto meet) {
        meetValidation.meetExists(meet.getId());
        meetValidation.permissionCheck(userContext.getUserId(), meet.getCreatorId());

        Meet meetEntity = meetMapper.toEntity(meet);
        meetEntity.setUpdatedAt(LocalDateTime.now());

        Meet updatedMeet = meetRepository.save(meetEntity);
        log.info("Meeting updated meetId : {}", meet.getId());
        return meetMapper.toDto(updatedMeet);
    }

    public MeetDto cancelMeetById(Long meetId) {
        meetValidation.meetExists(meetId);
        Meet meetEntity = meetRepository.findById(meetId).get();
        meetValidation.permissionCheck(userContext.getUserId(), meetEntity.getCreatorId());

        meetEntity.setStatus(MeetStatus.CANCELLED);
        meetEntity.setUpdatedAt(LocalDateTime.now());

        Meet savedMeet = meetRepository.save(meetEntity);
        log.info("Meeting canceled meetId : {}", meetId);
        return meetMapper.toDto(savedMeet);
    }

    public MeetDto deleteMeetById(Long meetId) {
        meetValidation.meetExists(meetId);
        Meet meetEntity = meetRepository.findById(meetId).get();
        meetValidation.permissionCheck(userContext.getUserId(), meetEntity.getCreatorId());

        MeetDto meetDto = meetMapper.toDto(meetEntity);
        meetRepository.deleteById(meetId);

        log.info("Meeting deleted meetId : {}", meetId);
        return meetDto;
    }

    public List<MeetDto> findMeetsByProject(Long projectId, MeetFilterDto filterDto) {
        List<MeetDto> meets = meetRepository.findAll().stream()
                .filter(meet -> meet.getProject().getId().equals(projectId))
                .map(meetMapper::toDto)
                .toList();
        if (filterDto == null) {
            return meets;
        }
        for (MeetFilter filter : filters) {
            if (filter.isApplicable(filterDto)) {
                meets = filter.applyFilter(meets, filterDto);
            }
        }

        return meets;
    }

    public List<MeetDto> getAllMeets() {
        return meetRepository.findAll().stream().
                map(meetMapper::toDto).
                toList();
    }

    public MeetDto getMeetById(Long meetId) {
        meetValidation.meetExists(meetId);
        return meetMapper.toDto(meetRepository.findById(meetId).get());
    }
}
