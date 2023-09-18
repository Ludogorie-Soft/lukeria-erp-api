package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
    private Long id;
    @NotNull(message = "Моля въведете името на бизнеса!")
    private String businessName;
    @NotNull(message = "Моля въведете името на бизнеса и с английски букви!")
    private String englishBusinessName;
    @Pattern(regexp = "\\d{5,}", message = "Моля въведете поне 5 цифри за Ид.Номер - ЕИК!")
    private String idNumEIK;
    private boolean hasIdNumDDS = false;
    @NotNull(message = "Моля въведете адреса!")
    private String address;
    @NotNull(message = "Моля въведете адреса на бизнеса и с английски букви!")
    private String englishAddress;
    private boolean isBulgarianClient = false;
    private String mol;
    private String englishMol;
}
