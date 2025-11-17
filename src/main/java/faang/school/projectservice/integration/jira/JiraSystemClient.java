package faang.school.projectservice.integration.jira;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.api.domain.Transition;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.api.domain.input.LinkIssuesInput;
import com.atlassian.jira.rest.client.api.domain.input.TransitionInput;
import faang.school.projectservice.integration.jira.cache.JiraCacheService;
import faang.school.projectservice.integration.jira.config.JiraProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Системный клиент Jira (использует JRJC)
 * Выполняет операции от имени системного аккаунта (бота)
 *
 * Use cases:
 * - Scheduled синхронизация
 * - Массовые операции
 * - Служебные задачи
 * - Fallback когда у пользователя нет OAuth токена
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JiraSystemClient {
    private final JiraProperties jiraProperties;
    private final JiraRestClient jiraClient;
    private final JiraCacheService cacheService;

    public String createIssue(IssueInput issueInput) {
        log.info("Creating issue via System Client: {}", issueInput.getField("summary"));

        try {
            BasicIssue issue = jiraClient.getIssueClient()
                    .createIssue(issueInput)
                    .claim();

            log.info("Issue created successfully via System Client: {}", issue.getKey());
            return issue.getKey();
        } catch (Exception e) {
            log.error("Failed to create issue via System Client", e);
            throw new RuntimeException("Failed to create issue", e);
        }
    }

    public Issue getIssue(String issueKey) {
        log.debug("Fetching issue via System Client: {}", issueKey);

        try {
            Issue issue = jiraClient.getIssueClient()
                    .getIssue(issueKey)
                    .claim();

            log.debug("Issue fetched successfully: {}", issueKey);
            return issue;
        } catch (Exception e) {
            log.error("Failed to fetch issue: {}", issueKey, e);
            throw new RuntimeException("Failed to fetch issue: " + issueKey, e);
        }
    }

    public void updateIssue(String issueKey, IssueInput issueInput) {
        log.info("Updating issue via System Client: {}", issueKey);

        try {
            jiraClient.getIssueClient()
                    .updateIssue(issueKey, issueInput)
                    .claim();

            // Инвалидировать кэш (удалить issue и transitions)
            cacheService.invalidateIssue(issueKey);

            log.info("Issue updated successfully: {}", issueKey);
        } catch (Exception e) {
            log.error("Failed to update issue: {}", issueKey, e);
            throw new RuntimeException("Failed to update issue: " + issueKey, e);
        }
    }

    public List<Issue> searchIssues(String jql, int maxResults) {
        log.debug("Searching issues via System Client with JQL: {}", jql);

        try {
            SearchResult result = jiraClient.getSearchClient()
                    .searchJql(jql, maxResults, 0, null)
                    .claim();
            List<Issue> issues = StreamSupport.stream(
                    result.getIssues().spliterator(),
                    false
            ).collect(Collectors.toUnmodifiableList());

            if (issues.isEmpty()) {
                log.warn("No issues found for JQL: {}", jql);
            }

            log.debug("Found {} issues", issues.size());
            return issues;

        } catch (Exception e) {
            log.error("Failed to search issues with JQL: {}", jql, e);
            throw new RuntimeException("Failed to search issues", e);
        }
    }

    public Iterable<Transition> getTransitions(String issueKey) {
        log.debug("Fetching transitions for issue: {}", issueKey);

        // Попытка получить из кэша
        Optional<List<Transition>> cached = cacheService.getTransitionsList(issueKey);
        if (cached.isPresent()) {
            log.debug("Transitions retrieved from cache: {}", issueKey);
            return cached.get();
        }

        try {
            Issue issue = getIssue(issueKey);
            Iterable<Transition> transitions = jiraClient.getIssueClient()
                    .getTransitions(issue)
                    .claim();

            // Конвертировать в List для кэширования
            List<Transition> transitionList = new ArrayList<>();
            transitions.forEach(transitionList::add);

            // Сохранить в кэш
            cacheService.putTransitions(issueKey, transitionList);

            log.debug("Transitions fetched successfully: {}", issueKey);
            return transitionList;
        } catch (Exception e) {
            log.error("Failed to fetch transitions for issue: {}", issueKey, e);
            throw new RuntimeException("Failed to fetch transitions", e);
        }
    }

    public void performTransition(String issueKey, TransitionInput transitionInput) {
        log.info("Performing transition for issue: {}", issueKey);

        try {
            jiraClient.getIssueClient()
                    .transition(getIssue(issueKey), transitionInput)
                    .claim();

            // Инвалидировать кэш (issue изменился, transitions тоже могут измениться)
            cacheService.invalidateIssue(issueKey);

            log.info("Transition performed successfully for issue: {}", issueKey);

        } catch (Exception e) {
            log.error("Failed to perform transition for issue: {}", issueKey, e);
            throw new RuntimeException("Failed to perform transition", e);
        }
    }

    public void linkIssues(String sourceKey, String targetKey, String linkType) {
        log.info("Linking issues: {} -> {} ({})", sourceKey, targetKey, linkType);

        try {
            LinkIssuesInput linkInput = new LinkIssuesInput(
                    sourceKey,
                    targetKey,
                    linkType
            );

            jiraClient.getIssueClient()
                    .linkIssue(linkInput)
                    .claim();

            // Инвалидировать кэш для обеих issues
            cacheService.invalidateIssue(sourceKey);
            cacheService.invalidateIssue(targetKey);

            log.info("Issues linked successfully");

        } catch (Exception e) {
            log.error("Failed to link issues", e);
            throw new RuntimeException("Failed to link issues", e);
        }
    }

    public void deleteIssue(String issueKey, boolean deleteSubtasks) {
        log.warn("Deleting issue via System Client: {}", issueKey);

        try {
            jiraClient.getIssueClient()
                    .deleteIssue(issueKey, deleteSubtasks)
                    .claim();

            // Удалить из кэша
            cacheService.invalidateIssue(issueKey);

            log.info("Issue deleted successfully: {}", issueKey);

        } catch (Exception e) {
            log.error("Failed to delete issue: {}", issueKey, e);
            throw new RuntimeException("Failed to delete issue: " + issueKey, e);
        }
    }
}


