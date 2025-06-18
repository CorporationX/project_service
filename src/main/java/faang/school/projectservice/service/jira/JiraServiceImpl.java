package faang.school.projectservice.service.jira;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.client.JiraRestClient;
import faang.school.projectservice.dto.jira.issue.request.JiraCreateIssueRequest;
import faang.school.projectservice.dto.jira.issue.request.JiraGetMultipleIssuesDto;
import faang.school.projectservice.dto.jira.issue.request.JiraUpdateIssueRequest;
import faang.school.projectservice.dto.jira.issue.response.JiraCreateIssueResponse;
import faang.school.projectservice.dto.jira.issue.response.JiraGetIssueResponse;
import faang.school.projectservice.dto.jira.issue.response.JiraGetMultipleIssuesResponse;
import faang.school.projectservice.dto.jira.issue.response.JiraUpdateIssueResponse;
import faang.school.projectservice.exception.JiraIntegrationException;
import faang.school.projectservice.exception.jira.JiraAuthenticationException;
import faang.school.projectservice.exception.jira.JiraConfigurationProblemException;
import faang.school.projectservice.exception.jira.JiraConflictingUpdateException;
import faang.school.projectservice.exception.jira.JiraPermissionDeniedException;
import faang.school.projectservice.exception.jira.JiraResourceNotFoundException;
import faang.school.projectservice.exception.jira.JiraValidationException;
import faang.school.projectservice.service.JiraService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class JiraServiceImpl implements JiraService {

    private final JiraRestClient jiraRestClient;
    private final ObjectMapper objectMapper;

    private enum ErrorMessages {
        FAILED_CREATING_ISSUE,
        FAILED_UPDATING_ISSUE,
        FAILED_REQUESTING_ISSUES,
        FAILED_REQUESTING_ISSUES_FILTER,
        FAILED_TO_RECEIVE_ISSUE,
    }

    @Override
    public JiraCreateIssueResponse createIssue(JiraCreateIssueRequest jiraCreateIssueRequest) {
        return executeRequest(
                () -> jiraRestClient.createIssue(jiraCreateIssueRequest),
                ErrorMessages.FAILED_CREATING_ISSUE
        );
    }

    @Override
    public JiraUpdateIssueResponse changeIssue(long issueId, JiraUpdateIssueRequest requestBody) {
        return executeRequest(
                () -> jiraRestClient.changeIssue(issueId, requestBody),
                ErrorMessages.FAILED_UPDATING_ISSUE,
                response -> {
                    log.info("Issue updated successfully with Status: {}", response.getStatusCode());
                    if (response.hasBody()) {
                        return response.getBody();
                    } else {
                        log.debug("If body was expected, make sure parameter \"returnIssue\" is set to \"true\"");
                        return new JiraUpdateIssueResponse();
                    }
                });
    }

    @Override
    public JiraGetMultipleIssuesResponse getAllIssuesWithFilter(String projectKey,
                                                                JiraGetMultipleIssuesDto jiraGetMultipleIssuesDto) {
        return executeRequest(
                () -> jiraRestClient.getAllIssuesWithFilter(
                        projectKey,
                        jiraGetMultipleIssuesDto
                ),
                ErrorMessages.FAILED_REQUESTING_ISSUES_FILTER
        );
    }

    @Override
    public JiraGetMultipleIssuesResponse getAllIssues(String projectKey,
                                                      JiraGetMultipleIssuesDto jiraGetMultipleIssuesDto) {
        return executeRequest(
                () -> jiraRestClient.getAllIssues(
                        projectKey,
                        jiraGetMultipleIssuesDto
                ),
                ErrorMessages.FAILED_REQUESTING_ISSUES
        );
    }

    @Override
    public JiraGetIssueResponse getIssueById(long issueId) {
        return executeRequest(
                () -> jiraRestClient.getIssueById(issueId),
                ErrorMessages.FAILED_TO_RECEIVE_ISSUE
        );
    }

    private <T> T executeRequest(Supplier<ResponseEntity<T>> requestSupplier,
                                 ErrorMessages errorMessage) {
        return executeRequest(requestSupplier, errorMessage, ResponseEntity::getBody);
    }

    private <T, R> R executeRequest(Supplier<ResponseEntity<T>> requestSupplier,
                                    ErrorMessages errorMessage,
                                    Function<ResponseEntity<T>, R> responseHandler) {
        try {
            ResponseEntity<T> response = requestSupplier.get();

            return responseHandler.apply(response);
        } catch (HttpClientErrorException e) {
            handleJiraException(errorMessage, e);
            throw e;
        }
    }

    private void handleJiraException(ErrorMessages errorMessage, HttpClientErrorException e) {
        HttpStatusCode statusCode = e.getStatusCode();
        String errorDetails = extractJiraErrorDetails(e.getResponseBodyAsString());

        String errorFullMessage = String.format("[Status: %s] %s: %s",
                statusCode, errorMessage.toString(), errorDetails);

        if (statusCode == HttpStatus.BAD_REQUEST) {
            throw new JiraValidationException(errorFullMessage);
        } else if (statusCode == HttpStatus.UNAUTHORIZED) {
            throw new JiraAuthenticationException(errorFullMessage);
        } else if (statusCode == HttpStatus.FORBIDDEN) {
            throw new JiraPermissionDeniedException(errorFullMessage);
        } else if (statusCode == HttpStatus.NOT_FOUND) {
            throw new JiraResourceNotFoundException(errorFullMessage);
        } else if (statusCode == HttpStatus.CONFLICT) {
            throw new JiraConflictingUpdateException(errorFullMessage);
        } else if (statusCode == HttpStatus.UNPROCESSABLE_ENTITY) {
            throw new JiraConfigurationProblemException(errorFullMessage);
        } else {
            throw new JiraIntegrationException(errorFullMessage, (HttpStatus) statusCode, e);
        }
    }

    private String extractJiraErrorDetails(String responseBodyAsString) {
        try {
            if (responseBodyAsString.isEmpty()) {
                return "No error details provided";
            }

            JsonNode root = objectMapper.readTree(responseBodyAsString);
            StringBuilder details = new StringBuilder();

            if (root.has("errorMessages") && root.get("errorMessages").isArray()) {
                for (JsonNode msg : root.get("errorMessages")) {
                    details.append(msg.asText()).append(";\n");
                }
            }

            if (root.has("errors") && root.get("errors").isObject()) {
                JsonNode errors = root.get("errors");
                Iterator<Map.Entry<String, JsonNode>> fields = errors.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    details.append(field.getKey())
                            .append(": ")
                            .append(field.getValue().asText())
                            .append(";\n");
                }
            }

            return !details.isEmpty() ? details.toString() : responseBodyAsString;
        } catch (IOException ex) {
            log.warn("Failed to parse Jira error response", ex);
            return responseBodyAsString;
        }
    }
}