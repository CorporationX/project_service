package faang.school.projectservice.service.meet;

import faang.school.projectservice.dto.meet.MeetFilterDto;
import faang.school.projectservice.dto.meet.MeetRequestDto;
import faang.school.projectservice.dto.meet.MeetResponseDto;
import faang.school.projectservice.filter.meet.MeetFilter;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.meet.MeetMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.meet.Meet;
import faang.school.projectservice.model.meet.MeetStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.meet.MeetValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

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
        Meet meetToCreate = meetMapper.toEntity(dto);
        meetToCreate.setCreatorId(creatorId);
        Meet savedMeet = meetRepository.save(meetToCreate);
        return meetMapper.toDto(savedMeet);
    }

    @Override
    @Transactional
    public MeetResponseDto update(long creatorId, MeetRequestDto dto) {
        Meet meetToUpdate = findMeetById(dto.id());
        meetValidator.validateMeetToUpdate(meetToUpdate, creatorId);
        List<Meet> meetsByCreatorId = findMeetsByCreatorId(creatorId);
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
        Meet meetToDelete = findMeetById(id);
        meetValidator.validateMeetToDelete(meetToDelete, creatorId);
        meetRepository.delete(meetToDelete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeetResponseDto> findAllByProjectIdFilter(Long projectId, MeetFilterDto filter) {
        Project project = projectRepository.getProjectById(projectId);
        Stream<Meet> meets = project.getMeets().stream();
        return meetFilters.stream()
                .filter(currentFilter -> currentFilter.isApplicable(filter))
                .reduce(meets, (stream, f) -> f.apply(stream, filter), (s1, s2) -> s1)
                .map(meetMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MeetResponseDto findById(Long id) {
        Meet meet = findMeetById(id);
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