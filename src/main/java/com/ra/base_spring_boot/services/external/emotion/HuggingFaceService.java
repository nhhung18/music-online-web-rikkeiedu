package com.ra.base_spring_boot.services.external.emotion;

import com.ra.base_spring_boot.dto.resp.EmotionResp;
import com.ra.base_spring_boot.model.Genre;
import com.ra.base_spring_boot.repository.IGenreRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.Map;

@Service
@RequiredArgsConstructor
public class HuggingFaceService implements IEmotionService {

    private static final String API_URL = "http://localhost:5000/predict";
    private final IGenreRepository genreRepository;

    @Override
    public EmotionResp analyzeEmotion(String text) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> body = Map.of("text", text);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, request, String.class);

        JSONObject result = new JSONObject(response.getBody());
        String emotion = result.getString("label");
        Genre genre= genreRepository.findByGenreName(emotion)
                .orElse(null);
        return EmotionResp.builder()
                .emotion(emotion)
                .genre(genre)
                .build();
    }
}