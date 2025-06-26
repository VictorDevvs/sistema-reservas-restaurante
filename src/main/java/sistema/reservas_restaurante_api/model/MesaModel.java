package sistema.reservas_restaurante_api.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mesas")
public class MesaModel {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Integer numero;

    @Column(nullable = false)
    private Integer capacidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MesaStatus status;

    @OneToMany(mappedBy = "mesa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservaModel> reservas = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public MesaStatus getStatus() {
        return status;
    }

    public void setStatus(MesaStatus     status) {
        this.status = status;
    }

    public List<ReservaModel> getReservas() {return reservas;}

    public void setReservas(List<ReservaModel> reservas) {this.reservas = reservas;}
}

