# Testing - SprinBoot

## Instanciar clases con Mockito
Para instanciar clases se usa `@Mock`
```
@Mock 
CuentaRepository cuentaRepository;
```

## Inyectar dependencias con Mockito
Para inyectar una dependencia se usa `@InjectMocks` y siempre se usa la implementación de la interfaz
```
@InjectMocks 
CuentaServiceImpl cuentaService;
```

## Instanciar clases con SpringBoot
Para instanciar una clase se usa `@MockBean`
```
@MockBean 
CuentaRepository cuentaRepository;
```

## Inyectar dependencias con SpringBoot
Para inyectar dependencias se usa `@Autowired`
```
@Autowired 
ICuentaService cuentaService;
```

## Método de setup()
Es un método que se usa para inicializar los componentes necesarios para generar las pruebas
Se agrega la anotación `@BeforeEach` para que se ejecute al realizar cada caso de prueba
```
@BeforeEach
void setUp() {

    //Inicializa las clases
    cuentaRepository = mock(CuentaRepository.class);
    bancoRepository = mock(BancoRepository.class);
    cuentaService = new CuentaServiceImpl(cuentaRepository, bancoRepository);

    //Setea campos de objetos de datos
    Datos.CUENTA_001.setSaldo(new BigDecimal("1000"));
    Datos.CUENTA_002.setSaldo(new BigDecimal("2000"));
    Datos.crearBanco().setTotalTransferencia(0);

}
```

## Uso de when y thenReturn
`when` simula la llamada a un método y `thenReturn` le asigna un valor especifico
```
when(cuentaRepository.findById(1L)).thenReturn(Datos.crearCuenta001());
```

## Uso de assertEquals(value expected, value result)
Se usa para comparar dos valores, un es el valor esperado y el otro el resultado
Para convertir un valor numérico a `String` se usa `toPlainString()`
```
int total = cuentaService.revisarTotalTransferencias(1L);
assertEquals(1, total);

BigDecimal saldoOrigen = cuentaService.revisarSaldo(1L);
assertEquals("1000", saldoOrigen.toPlainString());
```

## Uso de verify(objeto, times(nro_veces_ejecutado)).metodo();
Se usa para verificar el número de veces que fue usado el método
Si se verifica que el método nunca fue llamado, se puede reemplazar `times()` por `never()`
```
verify(bancoRepository, times(2)).findById(1L);
verify(cuentaRepository, never()).findAll();
```

## Uso de any(Class.class)
Se usa para simular cuando un método recibe como parámetro un objeto
```
verify(bancoRepository).update(any(Banco.class));
```

## Uso de assertThrows(Exception.class, () -> {...})
Se usa para simular cuando debe ejecutarse una excepción
```
assertThrows(DineroInsuficienteException.class, () -> {
            cuentaService.transferir(1L, 1L, 2L, amount);
        });
```

## Uso de assertSame y AssertTrue
Se usan para validar que dos objetos sean iguales en valor
```
Cuenta cuenta1 = cuentaRepository.findById(1L);
Cuenta cuenta2 = cuentaRepository.findById(1L);

assertSame(cuenta1, cuenta2);
assertTrue(cuenta1 == cuenta2);
```