package br.com.bancoDeSangue.Controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import br.com.bancoDeSangue.Model.Doador;
import br.com.bancoDeSangue.Service.DoadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doador")

public class DoadorController {

    @Autowired
    private DoadorService doadorService;

    @GetMapping("/doadores")
    public List<Doador> getDoadores() throws Exception {
        return doadorService.carregarDadosDoadores();
    }

    @GetMapping("/media-idade-por-tipo-sanguineo")
    public Map<String, Double> getMediaIdadePorTipoSanguineo() throws Exception {
        return doadorService.mediaIdadePorTipoSanguineo();
    }

    @GetMapping("/contagem-por-estado")
    public ResponseEntity<Map<String, Long>> countByState() {
        try {
            Map<String, Long> doadoresPorEstado = doadorService.contagemPorEstado();
            return ResponseEntity.ok(doadoresPorEstado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/media-imc-por-faixa-etaria")
    public ResponseEntity<Map<String, Double>> getMediaImcPorFaixaEtaria() {
        try {
            Map<String, Double> mediaImcPorFaixaEtaria = doadorService.mediaImcPorFaixaEtaria();
            return new ResponseEntity<>(mediaImcPorFaixaEtaria, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/percentual-obesos-por-sexo")
    public ResponseEntity<Map<String, Double>> getPercentualObesosPorSexo() {
        try {
            Map<String, Double> percentualObesosPorSexo = doadorService.percentualObesosPorSexo();
            return new ResponseEntity<>(percentualObesosPorSexo, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/possiveis-doadores")
    public ResponseEntity<Map<String, Long>> getPossiveisDoadoresPorTipoSanguineo() {
        try {
            Map<String, Long> doadores = doadorService.possiveisDoadoresPorTipoSanguineo();
            return new ResponseEntity<>(doadores, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }





}
