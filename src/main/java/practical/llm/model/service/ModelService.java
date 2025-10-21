package practical.llm.model.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practical.llm.model.domain.Models;
import practical.llm.model.mapper.ModelMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelService {
    private final ModelMapper modelMapper;

    public List<Models> findEvaluationModels() {
        return modelMapper.findEvaluationModels();
    }
}
