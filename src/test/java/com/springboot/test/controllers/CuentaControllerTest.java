package com.springboot.test.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.test.Datos;
import com.springboot.test.dto.TransaccionDTO;
import com.springboot.test.models.Cuenta;
import com.springboot.test.services.IBancoService;
import com.springboot.test.services.ICuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.springboot.test.Datos.crearCuenta001;
import static com.springboot.test.Datos.crearCuenta002;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    //Inicializa todo el contexto mvc
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICuentaService cuentaService;
    @MockBean
    private IBancoService bancoService;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void testDetalle() throws Exception {

        //Given
        when(cuentaService.findById(1L))
                .thenReturn(crearCuenta001().orElseThrow());

        //When
        /**
         * perform -> ejecuta la peticion
         *  -> Se debe indicar el tipo (get, post, put, delete)
         *  -> Ruta
         *  -> Tipo de contenido (json, xml)
         * andExcpect -> se obtiene la respuesta
         *  -> status().isOk() se compara si la respuesta devuelve este tipo HttpStatus
         *  -> content().contentType(...)) se compara si devuelve la respuesta en este tipo de formato
         *  -> jsonPath("$.atributo").value(...) se compara un valor con un campo del json de la respuesta
         */
        mockMvc.perform(
                        get("/api/cuentas/1")
                                .contentType(MediaType.APPLICATION_JSON))
                //Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.persona").value("Andrés"))
                .andExpect(jsonPath("$.saldo").value("1000"));

        verify(cuentaService).findById(1L);

    }

    @Test
    void testTransferir() throws Exception {

        //Given
        TransaccionDTO transaccionDTO = TransaccionDTO.builder()
                .bancoDestinoId(1L)
                .cuentaOrigenId(1L)
                .cuentaDestinoId(2L)
                .monto(new BigDecimal("100"))
                .build();

        //When
        /**
         * content() -> Se indica el contenido que se va a enviar
         *  -> para enviar un objeto en content() se debe transformar el objeto java a json, para eso se usa ObjectMapper
         *  -> El ObjectMapper se lo debe inicializar en el método setUp()
         *
         * $ -> Se usa para indicar la raiz del json
         * content().json(...) -> Se compara la respuesta con un objeto json
         */

        String transaccionDTOJson = objectMapper.writeValueAsString(transaccionDTO);

        System.out.println(transaccionDTOJson);

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con éxito!");
        response.put("transaccion", transaccionDTO);

        String responseJson = objectMapper.writeValueAsString(response);

        System.out.println(responseJson);

        mockMvc.perform(
                        post("/api/cuentas/transferir")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(transaccionDTOJson))
                //Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.date").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.mensaje").value("Transferencia realizada con éxito!"))
                .andExpect(jsonPath("$.transaccion.cuentaOrigenId").value(transaccionDTO.getCuentaOrigenId()))
                .andExpect(content().json(responseJson));

    }

    @Test
    void testListar() throws Exception {

        //Given
        List<Cuenta> cuentaList = Arrays.asList(
                crearCuenta001().orElseThrow(),
                crearCuenta002().orElseThrow());

        when(cuentaService.findAll()).thenReturn(cuentaList);

        /**
         * hasSize(nro) -> Se valida el número de elementos del array
         *
         */

        //When
        mockMvc.perform(get("/api/cuentas")
                    .contentType(MediaType.APPLICATION_JSON))
        //Then
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].persona").value("Andrés"))
                .andExpect(jsonPath("$[1].persona").value("Wilmer"))
                .andExpect(jsonPath("$[0].saldo").value("1000"))
                .andExpect(jsonPath("$[1].saldo").value("2000"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(cuentaList)));

    }

    @Test
    void testGuardar() throws Exception {

        Cuenta cuenta = Cuenta.builder()
                .persona("Pepe")
                .saldo(BigDecimal.valueOf(3000))
                .build();

        //Given
        when(cuentaService.guardar(any(Cuenta.class)))
                .then(c -> {
                    Cuenta cuenta1 = c.getArgument(0);
                    cuenta1.setId(3L);
                    return cuenta1;
                });


        /**
         * then(() -> {...}) -> Se usa para manipular el objeto de retorno al llamar a un método
         *
         * jsonPath("$.atributo", is(valor)) -> Se usa is() para validar un atributo de json con un valor
         *
         */

        //When
        mockMvc.perform(post("/api/cuentas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(cuenta)))
        //Then
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.persona", is("Pepe")))
                .andExpect(jsonPath("$.saldo", is(3000)));

        verify(cuentaService).guardar(any());

    }

}