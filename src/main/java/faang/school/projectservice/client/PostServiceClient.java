package faang.school.projectservice.client;

import faang.school.projectservice.client.own_client.PostDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "post-service", url = "http://localhost:8081/post-service/posts")
public interface PostServiceClient {
    @PostMapping("/get")
    ResponseEntity<PostDto> getPost(@RequestBody PostDto postDto);
}