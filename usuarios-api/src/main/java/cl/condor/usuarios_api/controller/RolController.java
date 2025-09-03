package cl.condor.usuarios_api.controller;

import cl.condor.usuarios_api.model.Rol;
import cl.condor.usuarios_api.service.RolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping
    public ResponseEntity<List<Rol>> getAll() {
        List<Rol> lista = rolService.findAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rol> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(rolService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Rol> create(@RequestBody Rol rol) {
        return ResponseEntity.status(201).body(rolService.save(rol));
    }
}
