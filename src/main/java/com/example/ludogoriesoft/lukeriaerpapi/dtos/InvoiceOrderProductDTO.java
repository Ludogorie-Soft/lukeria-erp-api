package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceOrderProductDTO {

    private Long id;
    private Long invoiceId;
    private Long orderProductId;
    private boolean deleted;
}
