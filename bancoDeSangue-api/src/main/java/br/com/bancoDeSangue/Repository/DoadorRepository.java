package br.com.bancoDeSangue.Repository;

import br.com.bancoDeSangue.Model.Doador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoadorRepository extends JpaRepository<Doador, Long> {

    List<Doador> findByTipoSanguineo(String tipoSanguineo);
}
