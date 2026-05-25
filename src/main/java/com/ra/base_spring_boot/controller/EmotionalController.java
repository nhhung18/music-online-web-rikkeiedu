package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.services.external.emotion.IEmotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/emotional")
@RequiredArgsConstructor
public class EmotionalController {
    private final IEmotionService emotionalService;

    @GetMapping
    public ResponseEntity<?> detectEmotion(@RequestParam String text){
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(emotionalService.analyzeEmotion(text))
                        .build()
        );
    }
}
