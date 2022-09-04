package com.springboot.test;

import com.springboot.test.models.Banco;
import com.springboot.test.models.Cuenta;

import java.math.BigDecimal;

public class Datos {

    private Datos(){
        //
    }

    public static Cuenta crearCuenta001() {
        return Cuenta.builder()
                .id(1L)
                .persona("Andrés")
                .saldo(BigDecimal.valueOf(1000))
                .build();
    }

    public static Cuenta crearCuenta002() {
        return Cuenta.builder()
                .id(1L)
                .persona("Wilmer")
                .saldo(BigDecimal.valueOf(2000))
                .build();
    }

    public static Banco crearBanco() {
        return Banco.builder()
                .id(1L)
                .nombre("BANCO FINANCIERO")
                .totalTransferencia(0)
                .build();
    }

}
