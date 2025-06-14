package faang.school.projectservice.repository.specification;

import faang.school.projectservice.dto.project.RequestFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class ProjectSpecification {

    public static Specification<Project> hasName(String name) {
        return ((root, query, cb) ->
                name == null ? cb.conjunction() : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
    }

    public static Specification<Project> hasStatus(ProjectStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    public Specification<Project> hasMemberWithUserId(long userId) {
        return ((root, query, cb) -> {
            Join<Project, Team> teamJoin = root.join("teams", JoinType.INNER);
            Join<Team, TeamMember> teamMemberJoin = teamJoin.join("teamMembers", JoinType.INNER);

            return cb.equal(teamMemberJoin.get("userId"), userId);
        });
    }

    public Specification<Project> buildFilter(RequestFilterDto filter) {
        return Specification
                .where(hasName(filter.getName()))
                .and(hasStatus(filter.getStatus()))
                .and(hasMemberWithUserId(filter.getUserId()));
    }


}