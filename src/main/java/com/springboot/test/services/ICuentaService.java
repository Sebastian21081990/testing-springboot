package com.springboot.test.services;

import com.springboot.test.models.Cuenta;

import java.math.BigDecimal;

public interface ICuentaService {

    Cuenta findById(Long id);
    Integer revisarTotalTransferencias(Long bancoId);
    BigDecimal revisarSaldo(Long cuentaId);
    void transferir(Long banccoId, Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto);

}
