package faang.school.projectservice.client.jira;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JiraRestClient {

    private final RestTemplate jiraRestTemplate;

    public <T> ResponseEntity<T> post(String url, Object body, Class<T> bodyClass) {
        return jiraRestTemplate.postForEntity(url, body, bodyClass);
    }

    public <T> ResponseEntity<T> put(String url, Object body, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);
        return jiraRestTemplate.exchange(url, HttpMethod.PUT, requestEntity, responseType);
    }

    public <T> ResponseEntity<T> get(String url, Map<String, String> queryParams, Class<T> responseType) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        queryParams.forEach(builder::queryParam);
        URI uri = builder.build().toUri();
        return jiraRestTemplate.getForEntity(uri, responseType);
    }
}
