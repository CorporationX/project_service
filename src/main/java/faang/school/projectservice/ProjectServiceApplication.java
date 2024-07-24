package faang.school.projectservice;

import faang.school.projectservice.dto.client.initiative.WriteInitiativeDto;
import faang.school.projectservice.dto.client.state.WriteStageDto;
import faang.school.projectservice.model.initiative.InitiativeStatus;
import faang.school.projectservice.service.InitiativeService;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
public class ProjectServiceApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext cx = new SpringApplicationBuilder(ProjectServiceApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
        InitiativeService initiativeService = cx.getBean(InitiativeService.class);
        TransactionTemplate txt = cx.getBean(TransactionTemplate.class);
        txt.executeWithoutResult(tx -> {
            initiativeService.create(new WriteInitiativeDto(4L, "222", "des",
                    List.of(new WriteStageDto("n1", 4L), new WriteStageDto("n2", 4L)), InitiativeStatus.DONE, 5L));
        });

    }
}
