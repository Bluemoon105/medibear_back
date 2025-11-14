package com.app.medibear.service.stress;

import com.app.medibear.dto.StressReportDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

public interface StressLLMService {
    String generateCoaching(StressReportDTO dto);
    String forwardAudioToFastApi(MultipartFile file);
    String chat(Map<String, Object> body);
}