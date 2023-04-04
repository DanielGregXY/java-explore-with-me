package ru.practicum.ewm.model.compilation;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.model.event.EventDtoShort;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDtoFull {

     Long id;

     List<EventDtoShort> events;

     Boolean pinned;

     String title;
}
