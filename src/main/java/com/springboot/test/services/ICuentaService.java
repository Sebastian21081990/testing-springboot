package com.springboot.test.services;

import com.springboot.test.models.Cuenta;

import java.math.BigDecimal;
import java.util.List;

public interface ICuentaService {

    Cuenta findById(Long id);

    Cuenta findByPerson(String person);
    List<Cuenta> findAll();
    void update(Cuenta cuenta);
    BigDecimal revisarSaldo(Long cuentaId);

}
