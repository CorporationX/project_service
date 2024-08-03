package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final TeamMemberRepository teamMemberRepository;

    public List<Long> getNewMemberIds(List<Project> projects) {
        Set<Long> memberIds = new HashSet<>();
        projects.forEach(project -> teamMemberRepository.findByProjectId(project.getId())
                .forEach(member -> memberIds.add(member.getId())));

        return new ArrayList<>(memberIds);
    }

    public List<Long> findDifferentMemberIds(List<Long> userIdsFromDataBase, List<Long> newUserIds) {
        newUserIds.removeAll(userIdsFromDataBase);
        return newUserIds;
    }
}
