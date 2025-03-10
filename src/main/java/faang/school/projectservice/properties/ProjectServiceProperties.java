package faang.school.projectservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "project-service")
public class ProjectServiceProperties {
  private String apiVersion;
  private Redis redis;
  private Jira jira;
  private Kafka kafka;

  @Data
  public static class Redis {
    private String host;
    private Integer port;
    private Channel channel;

    @Data
    public static class Channel {
      private String calculationsChannel;
    }
  }

  @Data
  public static class Jira {
    private String url;
    private String username;
    private String token;
  }

  @Data
  public static class Kafka {
    private String teamTopic;
  }
}