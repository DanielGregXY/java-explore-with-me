package ru.practicum.ewm.events.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShortEventDto {
    private Long id;
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private String eventDate;
    private UserDto.UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;
}
