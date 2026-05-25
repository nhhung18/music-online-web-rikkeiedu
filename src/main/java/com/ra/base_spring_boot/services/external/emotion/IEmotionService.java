package com.ra.base_spring_boot.services.external.emotion;

import com.ra.base_spring_boot.dto.resp.EmotionResp;

public interface IEmotionService {
    EmotionResp analyzeEmotion(String text);
}
