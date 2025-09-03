package cl.condor.usuarios_api.controller;

import cl.condor.usuarios_api.model.Estado;
import cl.condor.usuarios_api.service.EstadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/estados")
public class EstadoController {

    @Autowired
    private EstadoService estadoService;

    @GetMapping
    public ResponseEntity<List<Estado>> getAll() {
        List<Estado> lista = estadoService.findAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Estado> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(estadoService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Estado> create(@RequestBody Estado estado) {
        return ResponseEntity.status(201).body(estadoService.save(estado));
    }
}
