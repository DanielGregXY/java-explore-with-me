package ru.practicum.ewm.events.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.categories.dto.CategoryDTO;
import ru.practicum.ewm.user.dto.UserDTO;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShortEventDTO {
    private Long id;
    private String annotation;
    private CategoryDTO category;
    private Integer confirmedRequests;
    private String eventDate;
    private UserDTO.UserShortDTO initiator;
    private Boolean paid;
    private String title;
    private Long views;
    private Long rating;
}
