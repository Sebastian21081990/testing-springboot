package com.springboot.test.services;

import com.springboot.test.models.Cuenta;
import com.springboot.test.repositories.CuentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CuentaServiceImpl implements ICuentaService{

    private final CuentaRepository cuentaRepository;

    public CuentaServiceImpl(CuentaRepository cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public Cuenta findById(Long id) {
        return cuentaRepository.findById(id).orElseThrow();
    }

    @Transactional(readOnly = true)
    @Override
    public Cuenta findByPerson(String person) {
        return cuentaRepository.findByPersona(person).orElseThrow();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Cuenta> findAll() {
        return cuentaRepository.findAll();
    }

    @Transactional
    @Override
    public Cuenta guardar(Cuenta cuenta) {
        return cuentaRepository.save(cuenta);
    }

    @Transactional
    @Override
    public void update(Cuenta cuenta) {
        cuentaRepository.save(cuenta);
    }

    @Transactional(readOnly = true)
    @Override
    public BigDecimal revisarSaldo(Long cuentaId) {
        Cuenta cuenta = findById(cuentaId);
        return cuenta.getSaldo();
    }

}
