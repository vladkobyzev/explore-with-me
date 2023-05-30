package ru.practicum.main_service.config;

import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.main_service.dto.comments.CommentFullDto;
import ru.practicum.main_service.dto.comments.CommentShortDto;
import ru.practicum.main_service.models.Comment;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true).setMatchingStrategy(MatchingStrategies.STRICT);



        // Конвертер для преобразования Comment в long
        Converter<Comment, Long> commentToLongConverter = new AbstractConverter<Comment, Long>() {
            protected Long convert(Comment comment) {
                return comment != null ? comment.getId() : null;
            }
        };

        // Настройка маппинга для поля parentComment CommentFullDto
        modelMapper.createTypeMap(Comment.class, CommentFullDto.class)
                .addMappings(mapper -> mapper.using(commentToLongConverter).map(Comment::getParentComment, CommentFullDto::setParentComment));



        // Настройка маппинга для поля parentComment CommentShortDto
        modelMapper.createTypeMap(Comment.class, CommentShortDto.class)
                .addMappings(mapper -> mapper.using(commentToLongConverter).map(Comment::getParentComment, CommentShortDto::setParentComment));




        return modelMapper;
    }
}
