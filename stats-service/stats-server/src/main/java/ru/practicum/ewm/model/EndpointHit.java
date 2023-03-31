package ru.practicum.ewm.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "endpoint_hit")
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     Long id;

    @Nullable
    @Length(max = 50)
     String app;

    @Nullable
    @Length(max = 200)
     String uri;

    @Nullable
    @Length(max = 50)
     String ip;

     LocalDateTime timestamp;
}