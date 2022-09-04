package com.springboot.test.services;

import com.springboot.test.models.Banco;

import java.math.BigDecimal;

public interface IBancoService {

    Banco findById(Long id);
    void update(Banco banco);
    Integer revisarTotalTransferencias(Long bancoId);
    void transferir(Long bancoId, Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto);

}
