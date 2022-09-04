package com.springboot.test;

import com.springboot.test.models.Banco;
import com.springboot.test.models.Cuenta;

import java.math.BigDecimal;
import java.util.Optional;

public class Datos {

    private Datos(){
        //
    }

    public static Optional<Cuenta> crearCuenta001() {
        return Optional.of(Cuenta.builder()
                .id(1L)
                .persona("Andrés")
                .saldo(BigDecimal.valueOf(1000))
                .build());
    }

    public static Optional<Cuenta> crearCuenta002() {
        return Optional.of(Cuenta.builder()
                .id(1L)
                .persona("Wilmer")
                .saldo(BigDecimal.valueOf(2000))
                .build());
    }

    public static Optional<Banco> crearBanco() {
        return Optional.of(Banco.builder()
                .id(1L)
                .nombre("BANCO FINANCIERO")
                .totalTransferencia(0)
                .build());
    }

}
