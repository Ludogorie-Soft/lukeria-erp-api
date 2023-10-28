package com.example.ludogoriesoft.lukeriaerpapi.dtos.auth;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserDTO extends PublicUserDTO {
    private String lastname;
    private boolean deleted;
    private String address;
}
