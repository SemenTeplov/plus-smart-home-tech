package main.java.app;

import main.java.app.starter.AggregationStarter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
        AggregationStarter aggregator = context.getBean(AggregationStarter.class);

        aggregator.start();
    }
}
