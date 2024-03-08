package com.byeon.task.service;

import com.byeon.task.dto.NoteCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RankingService {


    private final RedisTemplate<String, Object> redisTemplate;
    @Async
    public void saveRanking(NoteCreateDto noteCreateDto) {
        redisTemplate.opsForZSet().incrementScore("ranking", noteCreateDto, 1);
    }

    public List<Object> getTopFiveRank() {
        Set<Object> ranking = redisTemplate.opsForZSet().reverseRange("ranking", 0, 4);
        if(ranking == null) return Collections.emptyList();
        return new ArrayList<>(ranking);
    }
}
