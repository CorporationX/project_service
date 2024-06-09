package faang.school.projectservice.validator;

import faang.school.projectservice.dto.MeetDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeetValidator {

    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;

    public void validateExistsMeetCreator(MeetDto meetDto) {
        TeamMember meetCreator = teamMemberRepository.findById(meetDto.getCreatedBy());
        Project project = projectRepository.getProjectById(meetDto.getProjectId());
        if (!project.getTeams().contains(meetCreator.getTeam())) {
            throw new DataValidationException("The meet creator is not from the project team");
        }
    }

    public void validateMeetCreator(Long updaterId, Long creatorId) {
        if (!Objects.equals(updaterId, creatorId)) {
            throw new DataValidationException(
                    String.format("Team member not creator for this meet. Creator ID: %d, Updater ID: %d",
                            creatorId, updaterId));
        }
    }
}
