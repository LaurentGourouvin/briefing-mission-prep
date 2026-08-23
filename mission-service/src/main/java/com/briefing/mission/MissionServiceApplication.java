package com.briefing.mission;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Orchestrateur de la SAGA de preparation de mission.
 *
 * @EnableFeignClients : active le scan des interfaces @FeignClient (appels sync).
 * Spring Kafka s'auto-configure via spring-kafka (pas d'annotation requise ici
 * pour le PRODUCER ; @EnableKafka ne sert que cote consumer/@KafkaListener).
 */
@SpringBootApplication
@EnableFeignClients
public class MissionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MissionServiceApplication.class, args);
    }
}
