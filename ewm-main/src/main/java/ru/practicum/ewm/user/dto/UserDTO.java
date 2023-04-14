package ru.practicum.ewm.user.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    @NotNull
    @Email
    private String email;
    @NotNull
    @NotBlank
    private String name;

    @Getter
    @Setter
    public static class UserShortDTO {
        private Long id;
        private String name;

        public UserShortDTO(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
