package faang.school.projectservice.config.context;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;

import java.util.List;

@Configuration
@OpenAPIDefinition
public class SwaggerConfig {

    @Bean
    public OpenAPI swaggerApiConfig() {
        Info info = new Info()
                .title("Project Service")
                .description("Medusa branch - Project Service documentation of CorporationX")
                .version("1.0");

        Parameter userIdHeader = new Parameter()
                .name("x-user-id")
                .in(ParameterIn.HEADER.toString())
                .required(true)
                .schema(new Schema<Long>().type("integer").format("int64"));

        Components components = new Components()
                .addParameters("x-user-id", userIdHeader);

        Server localServer = new Server();
        localServer.setUrl("http://localhost:8082");
        localServer.setDescription("Local Project_Service URL");

        return new OpenAPI()
                .info(info)
                .components(components).servers(List.of(localServer));
    }
}