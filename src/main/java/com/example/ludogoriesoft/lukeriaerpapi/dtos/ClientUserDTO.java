package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClientUserDTO {
    private Long id;
    @NotNull(message = "Моля въведете кой клиент искате да изберете!")
    private Long client_id;
    @NotNull(message = "Моля въведете кой потребител искате да изберете!")
    private Long user_id;
}
