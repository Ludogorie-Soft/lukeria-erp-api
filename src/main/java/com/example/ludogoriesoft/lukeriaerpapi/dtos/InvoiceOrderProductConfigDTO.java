package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import lombok.Data;

import java.util.List;

@Data
public class InvoiceOrderProductConfigDTO {
    private List<Long> orderProductIds;
    private Long invoiceId;
}
