package sistema.reservas_restaurante_api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
public class ReservaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioModel usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesa_id", nullable = false)
    private MesaModel mesa;

    @Column(nullable = false)
    private Integer numeroPessoas;

    @Column(nullable = false)
    private LocalDateTime dataHoraReserva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservaStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioModel getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioModel usuario) {
        this.usuario = usuario;
    }

    public MesaModel getMesa() {
        return mesa;
    }

    public void setMesa(MesaModel mesa) {
        this.mesa = mesa;
    }

    public Integer getNumeroPessoas() {
        return numeroPessoas;
    }

    public void setNumeroPessoas(Integer numeroPessoas) {
        this.numeroPessoas = numeroPessoas;
    }

    public LocalDateTime getDataHoraReserva() {
        return dataHoraReserva;
    }

    public void setDataHoraReserva(LocalDateTime dataHoraReserva) {
        this.dataHoraReserva = dataHoraReserva;
    }

    public ReservaStatus getStatus() {
        return status;
    }

    public void setStatus(ReservaStatus status) {
        this.status = status;
    }
}

