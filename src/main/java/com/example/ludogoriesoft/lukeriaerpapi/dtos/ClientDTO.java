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
    @Pattern(regexp = "^[a-zA-Z0-9\s!@#$%^&*()-_=+'\"]*$", message = "Името на бизнеса на английски трябва да бъде само с латински букви!")
    private String englishBusinessName;
    @Pattern(regexp = "\\d{5,}", message = "Моля въведете поне 5 цифри за Ид.Номер - ЕИК!")
    private String idNumEIK;
    private boolean hasIdNumDDS = false;
    @NotNull(message = "Моля въведете адреса!")
    private String address;
    @NotNull(message = "Моля въведете адреса на бизнеса и с английски букви!")
    @Pattern(regexp = "^[a-zA-Z0-9\s!@#$%^&*()-_=+'\"]*$", message = "Адреса на английски трябва да бъде само с латински букви!")
    private String englishAddress;
    private String isBulgarianClient = "false";
    private String mol;
    @Pattern(regexp = "^[a-zA-Z0-9\s!@#$%^&*()-_=+'\"]*$", message = "Името на МОЛ- на английски трябва да бъде само с латински букви!")
    private String englishMol;
    public boolean isBulgarianClient() {
        return Boolean.parseBoolean(isBulgarianClient);
    }

    public void setBulgarianClient(boolean bulgarianClient) {
        isBulgarianClient = String.valueOf(bulgarianClient);
    }
}
