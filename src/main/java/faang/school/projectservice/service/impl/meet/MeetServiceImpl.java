package faang.school.projectservice.service.impl.meet;

import faang.school.projectservice.model.dto.meet.MeetFilterDto;
import faang.school.projectservice.model.dto.meet.MeetRequestDto;
import faang.school.projectservice.model.dto.meet.MeetResponseDto;
import faang.school.projectservice.model.filter.meet.MeetFilter;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.model.mapper.meet.MeetMapper;
import faang.school.projectservice.model.entity.meet.Meet;
import faang.school.projectservice.model.entity.meet.MeetStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.MeetService;
import faang.school.projectservice.validator.meet.MeetValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetServiceImpl implements MeetService {

    private final MeetRepository meetRepository;
    private final ProjectRepository projectRepository;
    private final MeetMapper meetMapper;
    private final MeetValidator meetValidator;
    private final List<MeetFilter> meetFilters;

    @Override
    @Transactional
    public MeetResponseDto create(long creatorId, MeetRequestDto dto) {
        var meetToCreate = meetMapper.toEntity(dto);
        var project = projectRepository.findById(dto.projectId());
        meetToCreate.setCreatorId(creatorId);
        meetToCreate.setProject(project);
        var savedMeet = meetRepository.save(meetToCreate);
        return meetMapper.toDto(savedMeet);
    }

    @Override
    @Transactional
    public MeetResponseDto update(long creatorId, MeetRequestDto dto) {
        var meetToUpdate = findMeetById(dto.id());
        meetValidator.validateMeetToUpdate(meetToUpdate, creatorId);
        var meetsByCreatorId = findMeetsByCreatorId(creatorId);
        meetToUpdate.setUpdatedAt(LocalDateTime.now());
        if (dto.status() == MeetStatus.CANCELLED) {
            meetToUpdate.setStatus(MeetStatus.CANCELLED);
            meetToUpdate.setUpdatedAt(LocalDateTime.now());
        }
        meetMapper.update(meetToUpdate, dto);
        meetsByCreatorId.add(meetToUpdate);
        meetRepository.save(meetToUpdate);
        return meetMapper.toDto(meetToUpdate);
    }

    @Override
    @Transactional
    public void delete(long creatorId, Long id) {
        var meetToDelete = findMeetById(id);
        meetValidator.validateMeetToDelete(meetToDelete, creatorId);
        meetRepository.delete(meetToDelete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeetResponseDto> findAllByProjectIdFilter(Long projectId, MeetFilterDto filter) {
        var project = projectRepository.getProjectById(projectId);
        var meets = project.getMeets().stream();
        return meetFilters.stream()
                .filter(currentFilter -> currentFilter.isApplicable(filter))
                .reduce(meets, (stream, f) -> f.apply(stream, filter), (s1, s2) -> s1)
                .map(meetMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MeetResponseDto findById(Long id) {
        var meet = findMeetById(id);
        return meetMapper.toDto(meet);
    }

    private List<Meet> findMeetsByCreatorId(long creatorId) {
        return meetRepository.findByCreatorId(creatorId);
    }

    private Meet findMeetById(Long id) {
        return meetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meet with ID: %d was not found".formatted(id)));
    }
}