package faang.school.projectservice.service;

import faang.school.projectservice.config.audit.AuditorAwareImpl;
import faang.school.projectservice.dto.meet.CreateMeetDto;
import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.dto.meet.UpdateMeetDto;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.validator.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetService {

    private final MeetRepository meetRepository;
    private final MeetMapper meetMapper;
    private final UserValidator userValidator;
    private final AuditorAwareImpl auditorAware;
    private final ProjectService projectService;

    @Transactional
    public MeetResponseDto createMeet(CreateMeetDto createMeetDto) {
        userValidator.validateCurrentUserExists();
        Meet meet = meetMapper.fromCreateDto(createMeetDto);
        meet.setCreatorId(auditorAware.getCurrentAuditor().get());
        meet.setProject(projectService.getProjectById(createMeetDto.getProjectId()));
        meet.setStatus(MeetStatus.PENDING);
        return meetMapper.toResponseDto(meetRepository.save(meet));
    }

    @Transactional(readOnly = true)
    public List<MeetResponseDto> findAll() {
        return meetRepository.findAll().stream()
                .map(meetMapper::toResponseDto)
                .toList();
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
    public MeetResponseDto findById(long id) {
        return meetMapper.toResponseDto(
                meetRepository.findById(id)
                        .orElseThrow(() -> {
                            log.warn("Meet id {} not found", id);
                            return new EntityNotFoundException("Meet no found by id: " + id);
                        })
        );
    }

    @Transactional
    public MeetResponseDto updateMeet(UpdateMeetDto updateMeetDto) {
        Meet meet = meetRepository.findById(updateMeetDto.getId())
                .orElseThrow(() -> {
                    log.warn("Cannot update meet with id: {}, because not found", updateMeetDto.getId());
                    return new EntityNotFoundException(
                            "Cannot update meet with id: " + updateMeetDto.getId() + ", because not found");
                });
        userValidator.validateUserIsMeetCreator(meet);
        meetMapper.update(meet, updateMeetDto);
        return meetMapper.toResponseDto(meetRepository.save(meet));
    }

    @Transactional
    public MeetResponseDto cancelMeet(long id) {
        Meet meet = meetRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot cancel meet with id: {}, because not found", id);
                    return new EntityNotFoundException("Cannot cancel meet with id: "
                            + id + ", because not found");
                });
        userValidator.validateUserIsMeetCreator(meet);
        meet.setStatus(MeetStatus.CANCELLED);
        return meetMapper.toResponseDto(meetRepository.save(meet));
    }

    @Transactional
    public void deleteMeet(long id) {
        Meet meet = meetRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Cannot delete meet with id: {}, because not found", id);
                    return new EntityNotFoundException("Cannot delete meet with id: "
                            + id + ", because not found");
                });
        userValidator.validateUserIsMeetCreator(meet);
        meetRepository.delete(meet);
    }
}
