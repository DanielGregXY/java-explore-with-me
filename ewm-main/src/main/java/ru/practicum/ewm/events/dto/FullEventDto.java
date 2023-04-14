package ru.practicum.ewm.events.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.user.dto.UserDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FullEventDto {
    Long id;
    String annotation;
    CategoryDto category;
    Integer confirmedRequests;
    String createdOn;
    String description;
    String eventDate;
    UserDTO.UserShortDTO initiator;
    Location location;
    Boolean paid;
    Integer participantLimit;
    String publishedOn;
    Boolean requestModeration;
    String state;
    String title;
    Long views;
    Long rating;
}
