package ru.practicum.ewm.model.location;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationDtoFull {

    Long id;

    Float lat;

    Float lon;
}
