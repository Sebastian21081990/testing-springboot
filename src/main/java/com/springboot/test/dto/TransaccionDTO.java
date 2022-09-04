package com.springboot.test.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransaccionDTO {

    private Long cuentaOrigenId;
    private Long cuentaDestinoId;
    private Long bancoDestinoId;
    private BigDecimal monto;

}
