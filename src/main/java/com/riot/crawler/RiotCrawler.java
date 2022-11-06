package com.riot.crawler;

import com.RiotAPIConnection;
import com.entity.match.Match;
import com.riot.db.entity.MatchEntity;
import com.riot.db.entity.SummonerEntity;
import com.riot.db.repository.MatchRepository;
import com.riot.db.repository.SummonerRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
public class RiotCrawler implements Runnable{

    private final RiotAPIConnection conn;
    private final MatchRepository matchRepository;
    private final SummonerRepository summonerRepository;
    private Queue<String> puuidQueue = new LinkedList<>();

    Set<String> summonerPuuidCheckSet = new HashSet<>(); //중복되는 Summoner를 다시 조회하지 않기 위해

    @Override
    @SneakyThrows(value = InterruptedException.class)
    public void run() {
            final String initpuuid = "ftKz9UE7xtPCK0T63DIor8sxhxPmZQ_TFgcsSmQ8CnU_1DYniE4BvX3HcSd4AmkBWON7FS3qLci_fw";
            puuidQueue.add(initpuuid);
            summonerPuuidCheckSet.add(initpuuid);

            while(!puuidQueue.isEmpty()){
                try{
                    String puuid = puuidQueue.poll();

                    SummonerEntity summonerEntity = new SummonerEntity(conn.getSummonerByPUUID(puuid));
                    summonerRepository.save(summonerEntity);

                    List<Match> summonerMatch = conn.getMatchesBySummonerPUUID(puuid, 0, 10);

                    summonerMatch.forEach((match)->{
                        matchRepository.save(new MatchEntity(match, summonerEntity));
                        for (String participantsPuuid : match.getMetadata().getParticipants()) {
                            if(summonerPuuidCheckSet.contains(participantsPuuid)) continue;
                            summonerPuuidCheckSet.add(participantsPuuid);
                            puuidQueue.add(participantsPuuid);
                        }
                    });
                }
                catch (RiotAPIConnection.ExceedRateLimit e){
                    Thread.sleep(120000); //라이엇 API 대기시간 2분을 기다린다.
                }
                catch (IOException e){e.printStackTrace();}
            }
    }
}
