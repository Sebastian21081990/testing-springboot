package com.springboot.test.services;

import com.springboot.test.models.Cuenta;
import com.springboot.test.repositories.CuentaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CuentaServiceImpl implements ICuentaService{

    private final CuentaRepository cuentaRepository;

    public CuentaServiceImpl(CuentaRepository cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    @Override
    public Cuenta findById(Long id) {
        return cuentaRepository.findById(id).orElseThrow();
    }

    @Override
    public Cuenta findByPerson(String person) {
        return cuentaRepository.findByPersona(person).orElseThrow();
    }

    @Override
    public List<Cuenta> findAll() {
        return cuentaRepository.findAll();
    }

    @Override
    public void update(Cuenta cuenta) {
        cuentaRepository.save(cuenta);
    }

    @Override
    public BigDecimal revisarSaldo(Long cuentaId) {
        Cuenta cuenta = findById(cuentaId);
        return cuenta.getSaldo();
    }

}
