package br.com.bancoDeSangue.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "DOADOR")
@Getter
@Setter
public class Doador implements Serializable {

    public static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cpf;
    private String rg;
    private Date dataNasc; // Use java.util.Date ou java.time.LocalDate
    private String sexo;
    private String mae;
    private String pai;
    private String email;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private Endereco endereco;
    private String telefoneFixo;
    private String celular;
    private double altura;
    private double peso;
    private String tipoSanguineo;

}
