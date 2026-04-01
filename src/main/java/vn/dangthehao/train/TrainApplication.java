package vn.dangthehao.train;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {KafkaAutoConfiguration.class})
@EnableScheduling
@EnableJpaAuditing
@EnableAsync
public class TrainApplication {

  public static void main(String[] args) {
    SpringApplication.run(TrainApplication.class, args);
  }
}
