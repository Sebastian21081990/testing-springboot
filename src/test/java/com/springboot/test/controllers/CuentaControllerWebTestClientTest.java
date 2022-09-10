package com.springboot.test.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.test.dto.TransaccionDTO;
import com.springboot.test.models.Cuenta;
import com.springboot.test.services.IBancoService;
import com.springboot.test.services.ICuentaService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Primero se debe iniciar el proyecto y luego ejecutar las pruebas
 * se agrega el atributo  webEnvironment para configurar el ambiente web
 * -> SpringBootTest.WebEnvironment.RANDOM_PORT -> Levanta el servidor en un puerto disponible
 * -> @TestMethodOrder(MethodOrderer.OrderAnnotation.class) -> Se usa para indicar que se va ha establecer el orden de ejecución de las pruebas
 * -> @Order(nro) -> Se indica que en orden se va ha ejecutar
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CuentaControllerWebTestClientTest {

    /**
     * WebTestClient -> Es el cliente para consultar los servicios en un ambiente de pruebas
     */

    @Autowired
    private WebTestClient client;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @Order(1)
    void testTransferir() throws JsonProcessingException {

        TransaccionDTO transaccionDTO = TransaccionDTO.builder()
                .bancoDestinoId(1L)
                .cuentaOrigenId(1L)
                .cuentaDestinoId(2L)
                .monto(BigDecimal.valueOf(100))
                .build();

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con éxito!");
        response.put("transaccion", transaccionDTO);

        /**
         * post() -> Es el tipo de petición http
         * uri(url) -> Se usa para enviar la url de la petición http
         * bodyValue(object) -> Se usa para enviar un  objeto
         * exchange() -> Se usa para enviar la petición
         *
         * expectBody() -> Se usa para obtener la respuesta
         *  -> jsonPath() Se usa para validar un atributo json con un valor
         *  -> json() Se usa para comparar el json de respuesta con otro objeto
         *  -> consumeWith Se usa para manipular la respuesta directamente, se convierte la respuesta en un JsonNode para manipular el json directamente
         */
        String mensajeRespuesta = "Transferencia realizada con éxito!";

        //When
        client.post()
                .uri("http://localhost:8080/api/cuentas/transferir")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transaccionDTO)
                .exchange()
                //Then
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(respuesta -> {
                    try {
                        JsonNode json = objectMapper.readTree(respuesta.getResponseBody());
                        assertEquals(mensajeRespuesta, json.path("mensaje").asText());
                        assertEquals(1, json.path("transaccion").path("cuentaOrigenId").asLong());
                        assertEquals(LocalDate.now().toString(), json.path("date").asText());
                        assertEquals("100", json.path("transaccion").path("monto").asText());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .jsonPath("$.mensaje")
                .isNotEmpty()
                .jsonPath("$.mensaje")
                .value(is(mensajeRespuesta))
                .jsonPath("$.mensaje")
                .value(valor -> assertEquals(mensajeRespuesta, valor))
                .jsonPath("$.mensaje").isEqualTo(mensajeRespuesta)
                .jsonPath("$.transaccion.cuentaOrigenId").isEqualTo(transaccionDTO.getCuentaOrigenId())
                .jsonPath("$.date").isEqualTo(LocalDate.now().toString())
                .json(objectMapper.writeValueAsString(response));

    }

    @Test
    @Order(2)
    void testDetalle() throws JsonProcessingException {

        Cuenta cuenta = Cuenta.builder()
                .id(1L)
                .persona("Andrés")
                .saldo(new BigDecimal("1000"))
                .build();

        client.get()
                .uri("/api/cuentas/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.persona").isEqualTo("Andrés")
                .jsonPath("$.saldo").isEqualTo(1000)
                .json(objectMapper.writeValueAsString(cuenta));

    }

    @Test
    @Order(3)
    void testDetalle2() {

        client.get()
                .uri("/api/cuentas/2")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(respuesta -> {
                    Cuenta cuenta = respuesta.getResponseBody();
                    assertEquals("John", cuenta.getPersona());
                    assertEquals("2000.00", cuenta.getSaldo().toPlainString());
                });

    }

    @Test
    @Order(4)
    void testListar() {

        client.get()
                .uri("/api/cuentas")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.[0].id").isEqualTo(1)
                .jsonPath("$.[0].persona").isEqualTo("Andrés")
                .jsonPath("$.[0].saldo").isEqualTo(1000)
                .jsonPath("$.[1].id").isEqualTo(2)
                .jsonPath("$.[1].persona").isEqualTo("John")
                .jsonPath("$.[1].saldo").isEqualTo(2000)
                .jsonPath("$").isArray()
                .jsonPath("$").value(hasSize(2));

    }

    @Test
    @Order(5)
    void testListar2() {

        client.get()
                .uri("/api/cuentas")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Cuenta.class)
                .consumeWith(respuesta -> {
                    List<Cuenta> cuentaList = respuesta.getResponseBody();
                    assertNotNull(cuentaList);
                    assertEquals(2, cuentaList.size());
                    assertEquals(1L, cuentaList.get(0).getId());
                    assertEquals("Andrés", cuentaList.get(0).getPersona());
                    assertEquals("1000.0", cuentaList.get(0).getSaldo().toPlainString());
                    assertEquals(2L, cuentaList.get(1).getId());
                    assertEquals("John", cuentaList.get(1).getPersona());
                    assertEquals("2000.0", cuentaList.get(1).getSaldo().toPlainString());
                })
                .hasSize(2)
                .value(hasSize(2));

    }

    @Test
    @Order(6)
    void testGuardar() {

        //Given
        Cuenta cuenta = Cuenta.builder()
                .persona("Pepe")
                .saldo(BigDecimal.valueOf(3000))
                .build();


        client.post()
                .uri("/api/cuentas/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.persona").isEqualTo("Pepe")
                .jsonPath("$.persona").value(is("Pepe"))
                .jsonPath("$.saldo").isEqualTo(3000);

    }

    @Test
    @Order(7)
    void testGuardar1() {

        //Given
        Cuenta cuenta = Cuenta.builder()
                .persona("Pepa")
                .saldo(BigDecimal.valueOf(3000))
                .build();


        client.post().uri("/api/cuentas/")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Cuenta.class)
                .consumeWith(respuesta -> {
                    Cuenta c = respuesta.getResponseBody();
                    assertNotNull(c);
                    assertEquals(4L, c.getId());
                    assertEquals("Pepa", c.getPersona());
                    assertEquals("3000", c.getSaldo().toPlainString());
                });

    }

    @Test
    @Order(8)
    void testEliminar() {

        client.get().uri("/api/cuentas")
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBodyList(Cuenta.class)
                .hasSize(4);

        client.delete().uri("/api/cuentas/3")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody().isEmpty();

        client.get().uri("/api/cuentas")
                .exchange()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBodyList(Cuenta.class)
                .hasSize(3);

        /*client.get().uri("/api/cuentas/3")
                .exchange()
                .expectStatus().is5xxServerError();*/

        client.get().uri("/api/cuentas/3")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();

    }

}