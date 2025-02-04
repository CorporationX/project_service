package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.meet.CreateMeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.repository.MeetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeetService {
    private final MeetRepository meetRepository;
    private final MeetMapper meetMapper;
    private final ProjectService projectService;
    private final UserServiceClient userServiceClient;

    @Transactional
    public MeetResponseDto createMeet(CreateMeetDto createMeetDto) {
        if (userServiceClient.getUser(createMeetDto.getCreatorId()) == null) {
            throw new DataValidationException("Meet creator not exists with id: " + createMeetDto.getCreatorId());
        }
        Meet meet = meetMapper.fromCreateDto(createMeetDto);
        meet.setProject(projectService.findEntityById(createMeetDto.getProjectId()));
        meet.setStatus(MeetStatus.PENDING);
        return meetMapper.toResponseDto(meetRepository.save(meet));
    }

    @Transactional(readOnly = true)
    public List<MeetResponseDto> findProjectMeetsByFilter(long projectId, MeetFilterDto filter) {
        return meetRepository.findByFilter(
                        projectId,
                        filter.getTitlePattern(),
                        Optional.ofNullable(filter.getDatePattern())
                                .map(LocalDate::toString)
                                .orElse(null))
                .map(meetMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MeetResponseDto> findAll() {
        return meetRepository.findAll().stream()
                .map(meetMapper::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public MeetResponseDto findById(long id) {
        return meetMapper.toResponseDto(
                meetRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Meet no found by id: " + id))
        );
    }

    @Transactional
    public MeetResponseDto updateMeet(UpdateMeetDto updateMeetDto) {
        Meet meet = meetRepository.findById(updateMeetDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cannot update meet with id: " + updateMeetDto.getId() + ", because not found")
                );
        meetMapper.update(meet, updateMeetDto);
        return meetMapper.toResponseDto(meetRepository.save(meet));
    }

    @Transactional
    public MeetResponseDto cancelMeet(long id) {
        Meet meet = meetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cannot cancel meet with id: "
                        + id + ", because not found")
                );
        meet.setStatus(MeetStatus.CANCELLED);
        return meetMapper.toResponseDto(meetRepository.save(meet));
    }

    @Transactional
    public void deleteMeet(long id) {
        meetRepository.deleteById(id);
    }
}
