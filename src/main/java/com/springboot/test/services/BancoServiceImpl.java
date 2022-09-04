package com.springboot.test.services;

import com.springboot.test.models.Banco;
import com.springboot.test.models.Cuenta;
import com.springboot.test.repositories.BancoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BancoServiceImpl implements IBancoService{

    private final BancoRepository bancoRepository;
    private final ICuentaService cuentaService;

    public BancoServiceImpl(BancoRepository bancoRepository, ICuentaService cuentaService) {
        this.bancoRepository = bancoRepository;
        this.cuentaService = cuentaService;
    }

    @Override
    public Banco findById(Long id) {
        return bancoRepository.findById(id).orElseThrow();
    }

    @Override
    public void update(Banco banco) {
        bancoRepository.save(banco);
    }

    @Override
    public Integer revisarTotalTransferencias(Long bancoId) {
        Banco banco = bancoRepository.findById(bancoId).orElseThrow();
        return banco.getTotalTransferencia();
    }

    @Override
    public void transferir(Long bancoId, Long numCuentaOrigen, Long numCuentaDestino, BigDecimal monto) {

        Cuenta cuentaOrigen = cuentaService.findById(numCuentaOrigen);
        cuentaOrigen.debito(monto);
        cuentaService.update(cuentaOrigen);

        Cuenta cuentaDestino = cuentaService.findById(numCuentaDestino);
        cuentaDestino.credito(monto);
        cuentaService.update(cuentaDestino);

        Banco banco = findById(bancoId);
        Integer totalTransferencias = banco.getTotalTransferencia();
        banco.setTotalTransferencia(++totalTransferencias);
        bancoRepository.save(banco);

    }

}
