package faang.school.projectservice;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
public class ProjectServiceApplication implements CommandLineRunner {

//    @Autowired
//    private JiraRestClient jiraRestClient;

    public static void main(String[] args) {
        new SpringApplicationBuilder(ProjectServiceApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

    @Override
    public void run(String... args) throws Exception {
//        String jiraBaseUrl = "https://atlassian-jira-integration-test.atlassian.net"; // Замените на URL вашего Jira инстанса
//        String username = "atlassian.jira.integration@gmail.com"; // Замените на ваш email
//        String apiToken = "ATATT3xFfGF0iTo8KNsID1lHyJmQmQ26Nj6AcPxeAj_4simiq7wM7xFkAeucbHEC0qmIwWwGrebrelI9qyrLbtA7QG7kRDRbHj2o0ZiZjMKEKYAIXpmAcqJgoMBtHjjq4z95xQm8S0FevbOgmTdcWTdqq9NHMIHjREeJgZ94gGV-y4ryfWhH1OI=3813B801"; // Замените на ваш API токен
//        String projectKey = "KAN"; // Замените на ключ вашего проекта
//
//        String auth = Base64.getEncoder().encodeToString((username + ":" + apiToken).getBytes(StandardCharsets.UTF_8));
//
//        String issueJson = String.format("""
//                {
//                  "fields": {
//                     "project":
//                     {
//                        "key": "%s"
//                     },
//                     "summary": "REST API Issue",
//                     "description": {
//                        "type": "doc",
//                        "version": 1,
//                        "content": [
//                            {
//                                "type": "paragraph",
//                                "content": [
//                                    {
//                                        "text": "Creating an issue via REST API",
//                                        "type": "text"
//                                    },
//                                    {
//                                        "text": "Creating an issue via REST API - 2",
//                                        "type": "text"
//                                    }
//                                ]
//                            }
//                        ]
//                     },
//                     "issuetype": {
//                        "name": "Task"
//                     }
//                  }
//                }""", projectKey);
//
//
//
//        HttpClient client = HttpClient.newHttpClient();
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(jiraBaseUrl + "/rest/api/3/issue"))
//                .header("Authorization", "Basic " + auth)
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(issueJson))
//                .build();
//
//        try {
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//            System.out.println("Response status code: " + response.statusCode());
//            System.out.println("Response body: " + response.body());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


}
