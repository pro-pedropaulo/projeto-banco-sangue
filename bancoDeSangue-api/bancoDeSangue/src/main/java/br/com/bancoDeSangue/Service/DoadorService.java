package br.com.bancoDeSangue.Service;

import br.com.bancoDeSangue.Model.DTO.DoadorDTO;
import br.com.bancoDeSangue.Model.Doador;
import br.com.bancoDeSangue.Model.Endereco;
import br.com.bancoDeSangue.Repository.DoadorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DoadorService {

    @Autowired
    private DoadorRepository doadorRepository;

    public List<Doador> carregarDadosDoadores() throws Exception {
        File resource = new ClassPathResource("data.json").getFile();
        ObjectMapper mapper = new ObjectMapper();
        List<DoadorDTO> doadorDTOs = Arrays.asList(mapper.readValue(resource, DoadorDTO[].class));

        return doadorDTOs.stream().map(doadorDTO -> {
            Doador doador = new Doador();
            doador.setNome(doadorDTO.getNome());
            doador.setCpf(doadorDTO.getCpf());
            doador.setRg(doadorDTO.getRg());
            doador.setDataNasc(doadorDTO.getData_nasc());
            doador.setSexo(doadorDTO.getSexo());
            doador.setMae(doadorDTO.getMae());
            doador.setPai(doadorDTO.getPai());
            doador.setEmail(doadorDTO.getEmail());
            doador.setTelefoneFixo(doadorDTO.getTelefone_fixo());
            doador.setCelular(doadorDTO.getCelular());
            doador.setAltura(doadorDTO.getAltura());
            doador.setPeso(doadorDTO.getPeso());
            doador.setTipoSanguineo(doadorDTO.getTipo_sanguineo());


            Endereco endereco = new Endereco();
            endereco.setCep(doadorDTO.getCep());
            endereco.setEndereco(doadorDTO.getEndereco());
            endereco.setNumero(doadorDTO.getNumero());
            endereco.setBairro(doadorDTO.getBairro());
            endereco.setCidade(doadorDTO.getCidade());
            endereco.setEstado(doadorDTO.getEstado());


            doador.setEndereco(endereco);
            endereco.setDoador(doador);

            return doador;
        }).collect(Collectors.toList());
    }

    public Map<String, Double> mediaIdadePorTipoSanguineo() throws Exception {
        List<Doador> doadores = carregarDadosDoadores();

        Map<String, List<Doador>> doadoresPorTipoSanguineo = doadores.stream()
                .collect(Collectors.groupingBy(Doador::getTipoSanguineo));

        Map<String, Double> mediaIdadePorTipoSanguineo = new HashMap<>();
        doadoresPorTipoSanguineo.forEach((tipoSanguineo, doadoresDoTipo) -> {
            double mediaIdade = doadoresDoTipo.stream()
                    .mapToInt(doador -> Period.between(doador.getDataNasc().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()).getYears())
                    .average()
                    .orElse(0);

            mediaIdadePorTipoSanguineo.put(tipoSanguineo, mediaIdade);
        });

        return mediaIdadePorTipoSanguineo;
    }

    public Map<String, Long> contagemPorEstado() throws Exception {
        List<Doador> doadores = carregarDadosDoadores();

        Map<String, Long> doadoresPorEstado = doadores.stream()
                .collect(Collectors.groupingBy(doador -> doador.getEndereco().getEstado(), Collectors.counting()));

        return doadoresPorEstado;
    }

    public Map<String, Double> mediaImcPorFaixaEtaria() throws Exception {
        List<Doador> doadores = carregarDadosDoadores();

        Map<String, List<Doador>> doadoresPorFaixaEtaria = doadores.stream()
                .collect(Collectors.groupingBy(doador -> {
                    int idade = Period.between(doador.getDataNasc().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()).getYears();
                    return String.valueOf(idade / 10 * 10) + "-" + String.valueOf(idade / 10 * 10 + 9);
                }));

        Map<String, Double> mediaImcPorFaixaEtaria = new HashMap<>();
        doadoresPorFaixaEtaria.forEach((faixaEtaria, doadoresDaFaixa) -> {
            double mediaImc = doadoresDaFaixa.stream()
                    .mapToDouble(doador -> doador.getPeso() / Math.pow(doador.getAltura(), 2))
                    .average()
                    .orElse(0);

            mediaImcPorFaixaEtaria.put(faixaEtaria, mediaImc);
        });

        return mediaImcPorFaixaEtaria;
    }

    public Map<String, Double> percentualObesosPorSexo() throws Exception {
        List<Doador> doadores = carregarDadosDoadores();

        Map<String, List<Doador>> doadoresPorSexo = doadores.stream()
                .collect(Collectors.groupingBy(Doador::getSexo));

        Map<String, Double> percentualObesosPorSexo = new HashMap<>();
        doadoresPorSexo.forEach((sexo, doadoresDoSexo) -> {
            long totalObesos = doadoresDoSexo.stream()
                    .filter(doador -> this.calculaImc(doador.getPeso(), doador.getAltura()) > 30)
                    .count();

            double percentualObesos = (double) totalObesos / doadoresDoSexo.size() * 100;

            percentualObesosPorSexo.put(sexo, percentualObesos);
        });

        return percentualObesosPorSexo;
    }

    private double calculaImc(double peso, double altura) {
        return peso / Math.pow(altura, 2);
    }

    public Map<String, Long> possiveisDoadoresPorTipoSanguineo() throws Exception {
        List<Doador> doadores = carregarDadosDoadores();

        // Filtrando doadores elegíveis
        List<Doador> doadoresElegiveis = doadores.stream()
                .filter(doador -> {
                    int idade = Period.between(doador.getDataNasc().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()).getYears();
                    return idade >= 16 && idade <= 69 && doador.getPeso() > 50;
                })
                .collect(Collectors.toList());

        // Regras para tipos de sangue compatíveis
        Map<String, List<String>> regrasCompatibilidade = new HashMap<>();
        regrasCompatibilidade.put("A+", Arrays.asList("A+", "AB+"));
        regrasCompatibilidade.put("A-", Arrays.asList("A+", "A-", "AB+", "AB-"));
        regrasCompatibilidade.put("B+", Arrays.asList("B+", "AB+"));
        regrasCompatibilidade.put("B-", Arrays.asList("B+", "B-", "AB+", "AB-"));
        regrasCompatibilidade.put("AB+", Arrays.asList("AB+"));
        regrasCompatibilidade.put("AB-", Arrays.asList("AB+", "AB-"));
        regrasCompatibilidade.put("O+", Arrays.asList("A+", "B+", "O+", "AB+"));
        regrasCompatibilidade.put("O-", Arrays.asList("A+", "B+", "O+", "AB+", "A-", "B-", "O-", "AB-"));


        // Agrupando doadores elegíveis por tipo sanguíneo e contando o total de possíveis doadores
        Map<String, Long> possiveisDoadoresPorTipoSanguineo = new HashMap<>();
        regrasCompatibilidade.forEach((tipoDoador, tiposReceptores) -> {
            long totalDoadores = doadoresElegiveis.stream()
                    .filter(doador -> doador.getTipoSanguineo().equals(tipoDoador))
                    .count();

            for (String tipoReceptor : tiposReceptores) {
                possiveisDoadoresPorTipoSanguineo.merge(tipoReceptor, totalDoadores, Long::sum);
            }
        });

        return possiveisDoadoresPorTipoSanguineo;
    }







}
