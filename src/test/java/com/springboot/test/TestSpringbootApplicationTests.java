package com.springboot.test;

import com.springboot.test.exceptions.DineroInsuficienteException;
import com.springboot.test.models.Banco;
import com.springboot.test.models.Cuenta;
import com.springboot.test.repositories.BancoRepository;
import com.springboot.test.repositories.CuentaRepository;
import com.springboot.test.services.IBancoService;
import com.springboot.test.services.ICuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static com.springboot.test.Datos.crearCuenta001;
import static com.springboot.test.Datos.crearCuenta002;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class TestSpringbootApplicationTests {

    /**
     * Para instanciar clases con mockito se usa las anotaciones
     *
     * @Mock para los repository
     * @InjectMocks para las implementaciones de los services,
     * se debe usar la implementacion y no la interfaz
     *
     * Ejemplo
     * @Mock CuentaRepository cuentaRepository;
     * @Mock BancoRepository bancoRepository;
     * @InjectMocks CuentaServiceImpl cuentaService;
     */

    /**
     * Para instanciar clases con spring se usa las anotaciones
     *
     * @MockBean para los repository
     * @Autowired para las implementaciones de los services,
     * se debe usar la interfaz
     *
     * Ejemplo
     * @MockBean CuentaRepository cuentaRepository;
     * @MockBean BancoRepository bancoRepository;
     * @Autowired ICuentaService cuentaService;
     */

    @MockBean
    CuentaRepository cuentaRepository;
    @MockBean
    BancoRepository bancoRepository;

    @Autowired
    ICuentaService cuentaService;
    @Autowired
    IBancoService bancoService;

    /*
     * Se usa para inicializar los objetos
     * */
    @BeforeEach
    void setUp() {

        /*cuentaRepository = mock(CuentaRepository.class);
        bancoRepository = mock(BancoRepository.class);
        cuentaService = new CuentaServiceImpl(cuentaRepository, bancoRepository);*/

        /*Datos.CUENTA_001.setSaldo(new BigDecimal("1000"));
        Datos.CUENTA_002.setSaldo(new BigDecimal("2000"));
        Datos.crearBanco().setTotalTransferencia(0);*/

    }

    @Test
    void TEST_METHOD_REVISAR_SALDO_WITH_COUNT_1() {

        /*
         * Se usa para cargar datos cuando se use findById
         **/
        when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());

        BigDecimal saldo = cuentaService.revisarSaldo(1L);

        /*
         * assertEquals(String esperado, String actual) -> Se usa para comparar dos cadenas de texto
         **/
        assertEquals("1000", saldo.toPlainString());

        /*
         * verify(clase, times(nro_vecces).metodo(); -> Se usa para verificar cuantas veces se uso el método findById
         * any(Class.class) -> Se usa cuando se debe enviar objeto de una clase
         * */
        verify(cuentaRepository, times(1)).findById(1L);

    }

    @Test
    void TEST_METHOD_REVISAR_SALDO_WITH_COUNT_2() {

        when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta002());

        BigDecimal saldo = cuentaService.revisarSaldo(2L);

        assertEquals("2000", saldo.toPlainString());

        verify(cuentaRepository, times(1)).findById(2L);

    }

    @Test
    void TEST_METHOD_TRANSFER() {

        when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
        when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta002());
        when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());

        BigDecimal monto = BigDecimal.valueOf(100);

        bancoService.transferir(1L, 1L, 2L, monto);

        BigDecimal saldoOrigen = cuentaService.revisarSaldo(1L);
        BigDecimal saldoDestino = cuentaService.revisarSaldo(2L);

        //Valida los valores
        assertEquals("900", saldoOrigen.toPlainString());
        assertEquals("2100", saldoDestino.toPlainString());

        //Valida el nro de transferencias
        int total = bancoService.revisarTotalTransferencias(1L);
        assertEquals(1, total);

        //Verificar el nro de veces que es llamada el método
        verify(cuentaRepository, times(2)).findById(1L);
        verify(cuentaRepository, times(2)).findById(2L);
        verify(cuentaRepository, times(2)).save(any(Cuenta.class));

        verify(bancoRepository, times(2)).findById(1L);
        verify(bancoRepository).save(any(Banco.class));

    }

    @Test
    void TEST_EXCEPTION_WHEN_BALANCE_IS_INSUFFICIENT() {

        /*
         * Se usa para cargar datos cuando se use findById
         **/
        when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
        when(cuentaRepository.findById(2L)).thenReturn(Datos.crearCuenta002());
        when(bancoRepository.findById(1L)).thenReturn(Datos.crearBanco());

        BigDecimal amount = BigDecimal.valueOf(1200);

        //Lanza la excepcion
        assertThrows(DineroInsuficienteException.class, () -> {
            bancoService.transferir(1L, 1L, 2L, amount);
        });

        BigDecimal saldoOrigen = cuentaService.revisarSaldo(1L);
        BigDecimal saldoDestino = cuentaService.revisarSaldo(2L);

        assertEquals("1000", saldoOrigen.toPlainString());
        assertEquals("2000", saldoDestino.toPlainString());

        int total = bancoService.revisarTotalTransferencias(1L);
        assertEquals(0, total);

        verify(cuentaRepository, times(2)).findById(1L);
        verify(cuentaRepository, times(1)).findById(2L);
        verify(cuentaRepository, never()).save(any(Cuenta.class));

        verify(bancoRepository, times(1)).findById(1L);
        verify(bancoRepository, never()).save(any(Banco.class));

        //Verificar que se llama 6 veces findById con cualquier id  de tipo long
        verify(cuentaRepository, times(3)).findById(anyLong());
        verify(cuentaRepository, never()).findAll();

    }

    @Test
    void VERIFIED_THAT_CALL_SAME_OBJECT() {

        when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());

        Cuenta cuenta1 = cuentaRepository.findById(1L).get();
        Cuenta cuenta2 = cuentaRepository.findById(1L).get();

        //assertSame y assertTrue hacen lo mismo
        assertSame(cuenta1, cuenta2);
        assertTrue(cuenta1 == cuenta2);

        assertEquals("Andrés", cuenta1.getPersona());
        assertEquals("Andrés", cuenta2.getPersona());

        verify(cuentaRepository, times(2)).findById(1L);

    }

    @Test
    void testFindAll() {

        List<Cuenta> datos = Arrays.asList(
                crearCuenta001().orElseThrow(),
                crearCuenta002().orElseThrow());

        when(cuentaRepository.findAll()).thenReturn(datos);

        List<Cuenta> cuentas = cuentaService.findAll();

        assertFalse(cuentas.isEmpty());
        assertEquals(2, cuentas.size());
        assertTrue(cuentas.contains(crearCuenta002().orElseThrow()));

        verify(cuentaRepository).findAll();

    }

    @Test
    void testSave() {

        Cuenta cuentaPepe = Cuenta.builder()
                .persona("Pepe")
                .saldo(BigDecimal.valueOf(3000))
                .build();

        when(cuentaRepository.save(any()))
                .then(c -> {
                    Cuenta cuenta1 = c.getArgument(0);
                    cuenta1.setId(3L);
                    return cuenta1;
                });

        Cuenta cuenta = cuentaService.guardar(cuentaPepe);

        assertEquals(3L, cuenta.getId());
        assertEquals(cuentaPepe.getPersona(), cuenta.getPersona());
        assertEquals(cuentaPepe.getSaldo(), cuenta.getSaldo());

        verify(cuentaRepository).save(any());

    }

}
