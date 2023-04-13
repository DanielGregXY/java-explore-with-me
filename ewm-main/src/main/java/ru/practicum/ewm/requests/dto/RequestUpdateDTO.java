package ru.practicum.ewm.requests.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestUpdateDTO {
    List<RequestDTO> confirmedRequests;
    List<RequestDTO> rejectedRequests;
}
