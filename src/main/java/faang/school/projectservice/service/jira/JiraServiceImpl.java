package faang.school.projectservice.service.jira;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.client.jira.JiraRestClient;
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

    @Override
    public JiraCreateIssueResponse createIssue(JiraCreateIssueRequest jiraCreateIssueRequest) {
        return executeRequest(
                () -> jiraRestClient.createIssue(jiraCreateIssueRequest),
                "Failed creating issue"
        );
    }

    @Override
    public JiraUpdateIssueResponse changeIssue(long issueId, JiraUpdateIssueRequest requestBody) {
        return executeRequest(
                () -> jiraRestClient.changeIssue(issueId, requestBody),
                "Failed updating issue",
                response -> {
                    log.info("Issue updated successfully with Status: {}",
                            response.getStatusCode());
                    if (response.hasBody()) {
                        log.info("Updated issue:\n{}", response.getBody());

                        return response.getBody();
                    } else {
                        log.warn("Issue was updated, but no body was requested.");
                        log.warn("If body was expected, make sure parameter \"returnIssue\" is set to \"true\"");

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
                "Failed requesting multiple issues with filter"
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
                "Failed requesting multiple issues"
        );
    }

    @Override
    public JiraGetIssueResponse getIssueById(long issueId) {
        return executeRequest(
                () -> jiraRestClient.getIssueById(issueId),
                "Failed to receive an issue"
        );
    }

    private <T> T executeRequest(Supplier<ResponseEntity<T>> requestSupplier,
                                 String errorMessage) {
        return executeRequest(requestSupplier, errorMessage, ResponseEntity::getBody);
    }

    private <T, R> R executeRequest(Supplier<ResponseEntity<T>> requestSupplier,
                                    String errorMessage,
                                    Function<ResponseEntity<T>, R> responseHandler) {
        try {
            ResponseEntity<T> response = requestSupplier.get();
            logSuccessfulResponse(response);

            return responseHandler.apply(response);
        } catch (HttpClientErrorException e) {
            handleJiraException(errorMessage, e);
            throw e; // supposedly never reaches this line
        }
    }

    private void handleJiraException(String errorMessage, HttpClientErrorException e) {
        HttpStatusCode statusCode = e.getStatusCode();
        String errorDetails = extractJiraErrorDetails(e.getResponseBodyAsString());

        String errorFullMessage = String.format("[Status: %s] %s: %s",
                statusCode, errorMessage, errorDetails);

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

    private void logSuccessfulResponse(ResponseEntity<?> response) {
        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Successful response with code: {}", response.getStatusCode());
            log.info("Response body:\n{}", response.getBody());
        }
    }
}