package cl.condor.usuarios_api.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "usuario")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;
    private String rut;
    private String nombre;
    private String apellido;
    private String correo;
    private LocalDate fNacimiento;
    private String contrasena;
    private String token;

    @Lob
    private byte[] fotoPerfil;

    private Integer rutasRecorridas = 0;
    private BigDecimal kmRecorridos = BigDecimal.ZERO;

    private Integer idRol;
    private Integer idRegion;
    private Integer idEstado;
}
