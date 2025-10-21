package practical.llm.model.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practical.llm.model.domain.Models;
import practical.llm.model.service.ModelService;

import java.util.List;

@RestController
@RequestMapping("/models")
@RequiredArgsConstructor
public class ModelController {

    private final ModelService modelService;

    @GetMapping("/eval")
    public List<Models> evalModels() {
        return modelService.findEvaluationModels();
    }
}
