package com.riot.crawler;

import com.RiotAPIConnection;
import com.riot.db.repository.MatchRepository;
import com.riot.db.repository.SummonerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"com.riot.db"})
public class RiotCrawlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RiotCrawlerApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(SummonerRepository summonerRepository, MatchRepository matchRepository){
        return (args)->{
            RiotCrawler crawler = new RiotCrawler(new RiotAPIConnection("RGAPI-5c555e60-aaf3-4954-bd5d-6ae9ffc6addd"), matchRepository, summonerRepository);
            crawler.run();
        };
    }
}
