package faang.school.projectservice.filter.jira;

import faang.school.projectservice.model.dto.jira.JiraDto;

public class JiraUserFilter implements JiraFilter {
    @Override
    public boolean isApplicable(JiraDto dto) {
        return dto.getNameUser() != null;
    }

    @Override
    public String addFilter(JiraDto dto) {
        return "assignee = '" + dto.getNameUser() + "'";
    }
}
