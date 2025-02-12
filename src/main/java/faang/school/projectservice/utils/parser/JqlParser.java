package faang.school.projectservice.utils.parser;

import faang.school.projectservice.dto.jira.issue.IssueFilterDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static javax.naming.ldap.Rdn.escapeValue;

@Slf4j
@Component
public class JqlParser {
    public String buildJql(IssueFilterDto filters) {
        if (filters == null) {
            log.debug("Filters object is null, returning empty JQL.");
            return "";
        }

        List<String> jqlParts = new ArrayList<>();

        if (filters.getTypeIdPattern() != null) {
            jqlParts.add(String.format("issueType = '%s'", filters.getTypeIdPattern()));
        }

        if (StringUtils.hasText(filters.getSummaryPattern())) {
            String summaryPattern = escapeValue(filters.getSummaryPattern());
            jqlParts.add(String.format("summary ~ '%s'", summaryPattern));
        }

        if (StringUtils.hasText(filters.getDescriptionPattern())) {
            String descriptionPattern = escapeValue(filters.getDescriptionPattern());
            jqlParts.add(String.format("description ~ '%s'", descriptionPattern));
        }

        if (StringUtils.hasText(filters.getAssigneeNamePattern())) {
            String assigneePattern = escapeValue(filters.getAssigneeNamePattern());
            jqlParts.add(String.format("assignee = '%s'", assigneePattern));
        }

        if (StringUtils.hasText(filters.getReporterNamePattern())) {
            String reporterPattern = escapeValue(filters.getReporterNamePattern());
            jqlParts.add(String.format("reporter = '%s'", reporterPattern));
        }

        String jql = String.join(" AND ", jqlParts);
        log.debug("Generated JQL: {}", jql);
        return jql;
    }
}
