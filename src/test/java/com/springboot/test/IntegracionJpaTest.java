package com.springboot.test;

import com.springboot.test.models.Cuenta;
import com.springboot.test.repositories.CuentaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.springboot.test.Datos.crearCuenta001;
import static com.springboot.test.Datos.crearCuenta002;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

//Habilita el contexto de persistencia
@DataJpaTest
class IntegracionJpaTest {

    @Autowired
    CuentaRepository cuentaRepository;

    @Test
    void testFindById(){

        Optional<Cuenta> cuenta = cuentaRepository.findById(1L);
        assertTrue(cuenta.isPresent());
        assertEquals("Andrés", cuenta.orElseThrow().getPersona());

    }

    @Test
    void testFindByPerson(){

        Optional<Cuenta> cuenta = cuentaRepository.findByPersona("Andrés");
        assertTrue(cuenta.isPresent());
        assertEquals("Andrés", cuenta.orElseThrow().getPersona());
        assertEquals("1000.00", cuenta.orElseThrow().getSaldo().toPlainString());

    }

    @Test
    void testFindByPersonThrowException(){

        /*assertThrows(NoSuchElementException.class, () -> {
            cuentaRepository.findByPersona("Yuno").orElseThrow();
        });*/

        assertThrows(NoSuchElementException.class,
                cuentaRepository.findByPersona("Yuno")::orElseThrow);

    }

    @Test
    void testFindAll(){

        List<Cuenta> cuentaList = cuentaRepository.findAll();
        assertFalse(cuentaList.isEmpty());
        assertEquals(2, cuentaList.size());

    }

    @Test
    void testSave(){

        Cuenta cuenta = Cuenta.builder()
                .persona("Pepe")
                .saldo(BigDecimal.valueOf(3000))
                .build();

        cuentaRepository.save(cuenta);

        Cuenta cuentaRepositoryByPersona = cuentaRepository.findByPersona(cuenta.getPersona()).orElseThrow();

        assertEquals(cuentaRepositoryByPersona.getPersona(), cuenta.getPersona());
        assertEquals(cuentaRepositoryByPersona.getSaldo(), cuenta.getSaldo());

    }

    @Test
    void testUpdate(){

        Cuenta cuenta = cuentaRepository.findByPersona("Andrés").orElseThrow();
        cuenta.setSaldo(BigDecimal.valueOf(4500));

        cuentaRepository.save(cuenta);

        Cuenta cuentaRepositoryByPersona = cuentaRepository.findByPersona(cuenta.getPersona()).orElseThrow();

        assertEquals(cuentaRepositoryByPersona.getSaldo(), cuenta.getSaldo());

    }

    @Test
    void testDelete(){

        Cuenta cuenta = cuentaRepository.findByPersona("Andrés").orElseThrow();

        cuentaRepository.delete(cuenta);

        assertThrows(NoSuchElementException.class,
                cuentaRepository.findByPersona(cuenta.getPersona())::orElseThrow);

    }

}
