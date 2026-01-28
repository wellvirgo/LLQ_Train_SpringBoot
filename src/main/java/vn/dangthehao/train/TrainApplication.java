package vn.dangthehao.train;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrainApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrainApplication.class, args);
	}

}
