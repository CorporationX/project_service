package faang.school.projectservice.repository.adapter.meeting;

import faang.school.projectservice.exception.ProjectNotFoundException;
import faang.school.projectservice.model.Meet;
import faang.school.projectservice.repository.MeetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeetRepositoryAdapter {

    private final MeetRepository meetRepository;

    public Meet fetchByIdOrThrow(long id) {
        return meetRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Meeting with id=" + id + " not found"));
    }
}
