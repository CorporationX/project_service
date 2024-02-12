package faang.school.projectservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.TaskDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TaskStatus;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class JiraService {
    private final OkHttpClient client;
    private final TaskService taskService;
    private final String jiraBaseUrl;
    private final String encodedCredentials;
    private final UserContext userContext;

    @Autowired
    public JiraService(OkHttpClient client,
                       TaskService taskService,
                       UserContext userContext,
                       @Value("${jira.base-url}") String jiraBaseUrl,
                       @Value("${jira.auth-token}") String jiraAuthToken,
                       @Value("${jira.email}") String jiraEmail) {
        this.client = client;
        this.taskService = taskService;
        this.jiraBaseUrl = jiraBaseUrl;
        this.userContext = userContext;

        String credentials = jiraEmail + ":" + jiraAuthToken;
        encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    @Transactional
    public String createIssue(String issueJson, long performerUserId, long projectId)  {
        RequestBody body = RequestBody.create(issueJson, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(jiraBaseUrl + "/rest/api/2/issue")
                .post(body)
                .header("Authorization", "Basic " + encodedCredentials)
                .build();


        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();

            TaskDto taskDto = convertJsonToDto(issueJson, responseBody);
            taskDto.setProjectId(projectId);
            taskDto.setReporterUserId(userContext.getUserId());
            taskDto.setPerformerUserId(performerUserId);
            taskDto.setStatus(TaskStatus.TODO);
            taskService.createTask(taskDto);
            return responseBody;
        } catch (IOException e) {
            throw new DataValidationException("Ошибка при создании задачи в Jira: " + e.getMessage());
        }
    }

    @Transactional
    public String updateIssue(String issueKey, String issueJson) {
        RequestBody body = RequestBody.create(issueJson, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(jiraBaseUrl + "/rest/api/2/issue/" + issueKey)
                .put(body)
                .header("Authorization", "Basic " + encodedCredentials)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();

            TaskDto taskDto = convertJsonToDto(issueJson, responseBody);
            taskService.updateTask(taskDto);
            return responseBody;
        } catch (IOException e) {
            throw new DataValidationException("Ошибка при обновлении задачи в Jira: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public String getIssues(String jql) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(jiraBaseUrl + "/rest/api/2/search").newBuilder();
        urlBuilder.addQueryParameter("jql", jql);
        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .header("Authorization", "Basic " + encodedCredentials)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            throw new DataValidationException("Ошибка при получении задачи в Jira: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public String getIssueById(String issueId) {
        Request request = new Request.Builder()
                .url(jiraBaseUrl + "/rest/api/3/issue/" + issueId)
                .get()
                .header("Authorization", "Basic " + encodedCredentials)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            throw new DataValidationException("Ошибка при получении всех задач в Jira: " + e.getMessage());
        }
    }

    public TaskDto convertJsonToDto(String request, String response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNodeReq = objectMapper.readTree(request);
        JsonNode rootNodeRes = objectMapper.readTree(response);
        TaskDto taskDto = new TaskDto();

        JsonNode fieldsNode = rootNodeReq.path("fields");
        if (!fieldsNode.isMissingNode()) {
            taskDto.setId(rootNodeRes.path("id").asLong());
            taskDto.setName(fieldsNode.path("summary").asText());
            taskDto.setDescription(fieldsNode.path("description").asText());
        }

        return taskDto;
    }
}
