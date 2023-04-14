package ru.practicum.ewm.events.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ShortEventDto {
    Long id;
    String annotation;
    CategoryDto category;
    Integer confirmedRequests;
    String eventDate;
    UserDTO.UserShortDTO initiator;
    Boolean paid;
    String title;
    Long views;
    Long rating;
}
