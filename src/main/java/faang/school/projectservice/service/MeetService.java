package faang.school.projectservice.service;

import faang.school.projectservice.dto.meet.MeetCreateRequest;
import faang.school.projectservice.dto.meet.MeetFilterRequest;
import faang.school.projectservice.dto.meet.MeetResponse;
import faang.school.projectservice.dto.meet.MeetUpdateRequest;
import faang.school.projectservice.exception.MeetingOwnershipRequiredException;
import faang.school.projectservice.mapper.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.service.filter.meet.MeetFilter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MeetService {

    private final UserValidator userValidator;
    private final ProjectValidator projectValidator;
    private final MeetMapper meetMapper;
    private final MeetRepository meetRepository;
    private final List<MeetFilter> meetFilters;

    public MeetResponse createMeet(@Valid MeetCreateRequest meetCreateRequest) {
        userValidator.validateUser(meetCreateRequest.creatorId());
        if (meetCreateRequest.userIds() != null) {
            userValidator.validateUsers(meetCreateRequest.userIds());
        }

        if (meetCreateRequest.projectId() != null) {
            projectValidator.validateProject(meetCreateRequest.projectId());
        }

        Meet meet = meetMapper.toEntity(meetCreateRequest);
        return meetMapper.toMeetResponse(meetRepository.save(meet));
    }

    @Transactional
    public MeetResponse updateMeet(@Valid MeetUpdateRequest meetUpdateRequest) {
        userValidator.validateUser(meetUpdateRequest.userId());
        Meet meet = getMeet(meetUpdateRequest.meetId());
        if (!Objects.equals(meetUpdateRequest.userId(), meet.getCreatorId())) {
            throw new MeetingOwnershipRequiredException("Изменять встречу может только владелец");
        }

        meetMapper.updateMeet(meetUpdateRequest, meet);
        return meetMapper.toMeetResponse(meet);
    }

    @Transactional
    public void deleteMeet(Long meetId, Long userId) {
        userValidator.validateUser(userId);
        if (!meetRepository.existsById(meetId)) {
            throw new EntityNotFoundException("Meet not found");
        }
        if (!meetRepository.isUserOwnerMeet(meetId, userId)) {
            throw new MeetingOwnershipRequiredException("Удалять встречу может только владелец");
        }

        meetRepository.deleteById(meetId);
    }

    @Transactional(readOnly = true)
    public MeetResponse getMeetById(Long meetId) {
        return meetMapper.toMeetResponse(getMeet(meetId));
    }

    @Transactional(readOnly = true)
    public List<MeetResponse> getMeetsByFilter(@Valid MeetFilterRequest filterRequest) {
        projectValidator.validateProject(filterRequest.projectId());
        Stream<Meet> meets = meetRepository.findByProjectId(filterRequest.projectId());

        for (MeetFilter filter : meetFilters) {
            meets = filter.filter(meets, filterRequest);
        }

        return meets.map(meetMapper::toMeetResponse).toList();
    }

    public List<MeetResponse> getMeetsByProjectId(Long projectId) {
        projectValidator.validateProject(projectId);
        Stream<Meet> meets = meetRepository.findByProjectId(projectId);

        return meets.map(meetMapper::toMeetResponse).toList();
    }

    private Meet getMeet(Long id) {
        return meetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Meet not found"));
    }
}