package ru.practicum.ewm.requests.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestUpdateDto {
    List<RequestDto> confirmedRequests;
    List<RequestDto> rejectedRequests;
}
