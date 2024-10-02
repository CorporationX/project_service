package faang.school.projectservice.service.meet;

import faang.school.projectservice.service.project.ProjectService;
import faang.school.projectservice.service.meet.filter.MeetFilters;
import faang.school.projectservice.jpa.MeetRepository;
import faang.school.projectservice.mapper.meet.MeetMapper;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.service.meet.filter.MeetFilter;
import faang.school.projectservice.validator.meet.MeetValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class MeetService {
    private final MeetRepository meetRepository;
    private final ProjectService projectService;
    private final MeetMapper meetMapper;
    private final MeetValidator meetValidator;
    private final List<MeetFilter> meetFilters;

    @Transactional
    public Meet createMeet(Long userId, Long projectId, Meet meet) {
        var project = projectService.getProjectById(projectId);
        meetValidator.validateUser(userId);
        meetValidator.validateParticipants(meet.getUserIds());
        meet.setCreatorId(userId);
        meet.setProject(project);
        log.info("New Meet was created: {}.", meet);
        return meetRepository.save(meet);
    }

    @Transactional
    public Meet updateMeet(Long userId, Long meetId, Meet meet) {
        var foundEntity = getMeetById(userId, meetId);
        meetValidator.validateEditPermission(userId, foundEntity.getCreatorId());
        meetValidator.validateParticipants(meet.getUserIds());
        foundEntity = meetMapper.updateEntity(meet, foundEntity);
        log.info("Meet with id={} updated. New Meet={}.", meetId, foundEntity);
        return meetRepository.save(foundEntity);
    }

    @Transactional(readOnly = true)
    public Meet getMeetById(Long userId, Long id) {
        meetValidator.validateUser(userId);
        return meetRepository.findById(id).orElseThrow(() -> {
            log.debug("Meet with id={} not found.", id);
            return new EntityNotFoundException("Meet not found");
        });
    }

    @Transactional(readOnly = true)
    public Collection<Meet> getMeetsByProjectId(Long userId, Long projectId) {
        meetValidator.validateUser(userId);
        var project = projectService.getProjectById(projectId);
        return meetRepository.findAllByProject(project);
    }

    @Transactional
    public void cancelMeet(Long userId, Long meetId) {
        var foundMeet = getMeetById(userId, meetId);
        meetValidator.validateEditPermission(userId, foundMeet.getCreatorId());
        foundMeet.setStatus(MeetStatus.CANCELLED);
        meetRepository.save(foundMeet);
        log.info("Meet with id={} canceled.", meetId);
    }

    @Transactional(readOnly = true)
    public Collection<Meet> getMeetsByFilters(Long userId, Long projectId, MeetFilters meetFilters) {
        var foundMeets = getMeetsByProjectId(userId, projectId);
        Stream<Meet> meetStream = foundMeets.stream();

        return this.meetFilters.stream()
                .filter(filter -> filter.isApplicable(meetFilters))
                .reduce(
                        meetStream,
                        (stream, filter) -> filter.apply(stream, meetFilters),
                        (s1, s2) -> s1
                )
                .toList();
    }

    @Transactional
    public void deleteMeet(Long userId, Long meetId) {
        var foundMeet = getMeetById(userId, meetId);
        meetValidator.validateEditPermission(userId, foundMeet.getCreatorId());
        meetRepository.deleteById(meetId);
        log.info("Meet with id={} deleted.", meetId);
    }
}
