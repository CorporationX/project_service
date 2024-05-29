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
}
