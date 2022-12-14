package com.springboot.test.controllers;

import com.springboot.test.dto.TransaccionDTO;
import com.springboot.test.models.Cuenta;
import com.springboot.test.services.IBancoService;
import com.springboot.test.services.ICuentaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    private final ICuentaService cuentaService;
    private final IBancoService bancoService;

    public CuentaController(ICuentaService cuentaService, IBancoService bancoService) {
        this.cuentaService = cuentaService;
        this.bancoService = bancoService;
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Cuenta guardar(@RequestBody Cuenta cuenta){
        return cuentaService.guardar(cuenta);
    }

    @GetMapping
    @ResponseStatus(OK)
    public List<Cuenta> listar(){
        return cuentaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id){

        Cuenta cuenta = null;
        try{
            cuenta = cuentaService.findById(id);
        }catch (NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cuenta);
    }

    @PostMapping("/transferir")
    public ResponseEntity<Map<String, Object>> transferir(@RequestBody TransaccionDTO transaccionDTO){

        bancoService.transferir(transaccionDTO.getBancoDestinoId(),
                transaccionDTO.getCuentaOrigenId(),
                transaccionDTO.getCuentaDestinoId(),
                transaccionDTO.getMonto());

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con ??xito!");
        response.put("transaccion", transaccionDTO);

        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void eliminar(@PathVariable Long id){
        cuentaService.deleteByid(id);
    }

}
