package org.example.webquizapi.mapper;

import org.example.webquizapi.dto.QuizDto;
import org.example.webquizapi.entity.Quiz;
import org.springframework.stereotype.Component;

@Component
public class QuizMapper {
    // Dto -> Entity
    public Quiz dtoToEntity(QuizDto dto) {
        return new Quiz(dto.getId(), dto.getTitle(), dto.getText(), dto.getOptions(), dto.getAnswer());
    }

    // Entity -> Dto
    public QuizDto entityToDto(Quiz entity) {
        return new QuizDto(entity.getId(), entity.getTitle(), entity.getText(), entity.getOptions(), entity.getAnswer(), entity.getCreator().getEmail());
    }
}
