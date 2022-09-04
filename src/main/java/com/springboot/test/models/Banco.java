package com.springboot.test.models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class Banco {

    private Long id;
    private String nombre;
    private Integer totalTransferencia;

    public Banco(){
        totalTransferencia = 0;
    }

}
