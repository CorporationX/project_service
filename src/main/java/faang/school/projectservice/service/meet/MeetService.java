package faang.school.projectservice.service.meet;

import faang.school.projectservice.service.filters.meet.MeetSpecification;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.model.MeetStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.MeetRepository;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MeetService {
    private final MeetRepository repository;
    private final ProjectService projectService;

    public Meet create(Long projectId, Meet inputMeet) {
        Meet meet = fetchMeetWithProject(projectId, inputMeet);
        return repository.save(meet);
    }

    public Meet update(Long meetId, Long projectId, Meet inputMeet) {
        Meet fromDb = repository.findByIdOrThrow(meetId);
        checkOwner(inputMeet.getCreatorId(), fromDb);

        Meet meet = fetchMeetWithProject(projectId, inputMeet);
        Meet updatedMeet = updateMeetFields(meet, fromDb);
        return repository.save(updatedMeet);
    }


    public void cancelMeet(Long meetId, Long creatorId) {
        Meet fromDb = repository.findByIdOrThrow(meetId);
        checkOwner(creatorId, fromDb);
        fromDb.setStatus(MeetStatus.CANCELLED);
        repository.save(fromDb);
    }

    public void deleteMeet(Long meetId, Long creatorId) {
        Meet fromDb = repository.findByIdOrThrow(meetId);
        checkOwner(creatorId, fromDb);
        repository.deleteById(meetId);
    }

    public Meet getById(Long meetId) {
        return repository.findByIdOrThrow(meetId);
    }

    public List<Meet> findAll() {
        return repository.findAll();
    }

    public List<Meet> getMeetsWithFilters(Long projectId, String title, LocalDateTime startDate) {
        Specification<Meet> spec = MeetSpecification.byFilters(projectId, title, startDate);
        return repository.findAll(spec);
    }

    private Meet fetchMeetWithProject(Long projectId, Meet inputMeet){
        Project project = projectService.findProjectById(projectId);
        inputMeet.setProject(project);
        return inputMeet;
    }


    private void checkOwner(Long creatorId, Meet existedMeet) {
        if (existedMeet.getCreatorId() != creatorId) {
            throw new IllegalArgumentException("Only creator can change meet!");
        }
    }

    private Meet updateMeetFields(Meet inputMeet, Meet existedMeet) {
        existedMeet.setTitle(inputMeet.getTitle());
        existedMeet.setDescription(inputMeet.getDescription());
        existedMeet.setStatus(inputMeet.getStatus());
        existedMeet.setProject(inputMeet.getProject());
        existedMeet.setUserIds(inputMeet.getUserIds());
        existedMeet.setStartsAt(inputMeet.getStartsAt());
        existedMeet.setUpdatedAt(LocalDateTime.now());
        return existedMeet;
    }
}
