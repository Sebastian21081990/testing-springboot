package com.springboot.test.repositories;

import com.springboot.test.models.Cuenta;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CuentaRepository {

    List<Cuenta> findAll();
    Cuenta findById(Long id);
    void update(Cuenta cuenta);

}
