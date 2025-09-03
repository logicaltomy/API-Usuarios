package cl.condor.usuarios_api.controller;

import cl.condor.usuarios_api.model.Region;
import cl.condor.usuarios_api.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/regiones")
public class RegionController {

    @Autowired
    private RegionService regionService;

    @GetMapping
    public ResponseEntity<List<Region>> getAll() {
        List<Region> lista = regionService.findAll();
        if (lista.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Region> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(regionService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Region> create(@RequestBody Region region) {
        return ResponseEntity.status(201).body(regionService.save(region));
    }
}
