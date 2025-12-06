package faang.school.projectservice.integration.jira.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JiraTransitionsResponse {
    
    @JsonProperty("transitions")
    private List<Transition> transitions;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Transition {
        private String id;
        private String name;
        
        @JsonProperty("to")
        private To to;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class To {
        private String id;
        private String name;
    }
}

