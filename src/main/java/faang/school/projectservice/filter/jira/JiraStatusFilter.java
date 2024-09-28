package faang.school.projectservice.filter.jira;

import faang.school.projectservice.dto.jira.JiraDto;

public class JiraStatusFilter implements JiraFilter {
    @Override
    public boolean isApplicable(JiraDto dto) {
        return dto.getStatus() != null;
    }

    @Override
    public String addFilter(JiraDto dto) {
        return "status = '" + dto.getStatus() + "'";
    }
}
