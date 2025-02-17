package com.panizio.agenda.service;

import com.panizio.agenda.exception.ValidacaoException;
import com.panizio.agenda.model.PessoaFisica;
import com.panizio.agenda.repository.PessoaFisicaRepository;
import com.panizio.agenda.utils.ValidacaoUtils;

import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@Service
public class PessoaFisicaService {

  @Autowired
  private PessoaFisicaRepository pessoaFisicaRepository;
  private final EmailService emailService;

  public PessoaFisicaService(
      PessoaFisicaRepository pessoaFisicaRepository,
      EmailService emailService) {
    this.pessoaFisicaRepository = pessoaFisicaRepository;
    this.emailService = emailService;
  }

  public List<PessoaFisica> listarUsuarios() {
    return pessoaFisicaRepository.findAll();
  }

  public PessoaFisica buscarUsuarioPorCpf(String cpf) {
    return pessoaFisicaRepository.findById(limpar(cpf)).orElse(null);
  }

  public List<PessoaFisica> filtrarPorCpf(String prefixo) {
    return pessoaFisicaRepository.findByCpfStartingWith(prefixo);
  }

  public PessoaFisica salvarUsuario(PessoaFisica pessoaFisica) {
    pessoaFisica.setCpf(limpar(pessoaFisica.getCpf()));

    // Validação e busca de coordenadas
    if (!ValidacaoUtils.validarCEP(pessoaFisica.getCep())) {
      throw new ValidacaoException(Map.of("cep", "CEP inválido"));
    }

    try {
      Point coordenadas = ValidacaoUtils.buscarCoordenadasPorCEP(pessoaFisica.getCep());
      if (coordenadas != null) {
        pessoaFisica.setCoordenadas(coordenadas);
        System.out.println("TEST DE COORDENADA -> " + coordenadas);
      }
    } catch (ValidacaoException e) {
      throw e;
    }

    validarPessoaFisica(pessoaFisica, true);

    PessoaFisica savedPessoa = pessoaFisicaRepository.save(pessoaFisica);

    emailService.enviarEmailConfirmacao(savedPessoa.getNome(), savedPessoa.getEmail());

    return savedPessoa;
  }

  public PessoaFisica atualizarPessoaFisica(String cpf, PessoaFisica novosDados) {
    novosDados.setCpf(limpar(novosDados.getCpf()));
    PessoaFisica pessoaExistente = pessoaFisicaRepository.findById(cpf)
        .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

    // Atualiza coordenadas se o CEP mudar
    if (novosDados.getCep() != null && !novosDados.getCep().equals(pessoaExistente.getCep())) {
      if (!ValidacaoUtils.validarCEP(novosDados.getCep())) {
        throw new ValidacaoException(Map.of("cep", "CEP inválido"));
      }

      try {
        Point coordenadas = ValidacaoUtils.buscarCoordenadasPorCEP(novosDados.getCep());
        if (coordenadas != null) {
          novosDados.setCoordenadas(coordenadas);
        }
      } catch (ValidacaoException e) {
        throw e;
      }
    }

    validarCamposUnicos(novosDados, pessoaExistente);
    validarPessoaFisica(novosDados, false);
    atualizarCampos(pessoaExistente, novosDados);

    return pessoaFisicaRepository.save(pessoaExistente);
  }

  public void excluirUsuario(String cpf) {
    pessoaFisicaRepository.deleteById(cpf);
  }

  private void validarPessoaFisica(PessoaFisica pessoaFisica, boolean isNovo) {
    Map<String, String> erros = new HashMap<>();

    validarCampo(pessoaFisica.getCpf(), ValidacaoUtils::validarCPF, "cpf", "CPF inválido", erros);
    validarCampo(pessoaFisica.getEmail(), ValidacaoUtils::validarEmail, "email", "E-mail inválido", erros);
    validarCampo(pessoaFisica.getDataNascimento() != null ? pessoaFisica.getDataNascimento().toString() : null,
        ValidacaoUtils::validarDataNascimento, "dataNascimento", "Data inválida", erros);
    validarCampo(pessoaFisica.getCep(), ValidacaoUtils::validarCEP, "cep", "CEP inválido", erros);
    validarCampo(pessoaFisica.getTelefone(), ValidacaoUtils::validarTelefone, "telefone", "Telefone inválido", erros);
    validarCampo(pessoaFisica.getEndereco(), ValidacaoUtils::validarEndereco, "endereco", "Endereço inválido", erros);
    validarCampo(pessoaFisica.getNome(), ValidacaoUtils::validarNome, "nome", "Nome inválido", erros);

    if (isNovo) {
      validarExistenciaCampo(pessoaFisica.getCpf(), "cpf", "CPF já cadastrado");
      validarExistenciaCampo(pessoaFisica.getEmail(), "email", "E-mail já cadastrado");
    }

    if (!erros.isEmpty()) {
      throw new ValidacaoException(erros);
    }
  }

  private void validarCamposUnicos(PessoaFisica novaPessoa, PessoaFisica pessoaExistente) {
    if (novaPessoa.getCpf() != null && !novaPessoa.getCpf().equals(pessoaExistente.getCpf())) {
      validarExistenciaCampo(novaPessoa.getCpf(), "cpf", "CPF já cadastrado.");
    }
    if (novaPessoa.getEmail() != null && !novaPessoa.getEmail().equals(pessoaExistente.getEmail())) {
      validarExistenciaCampo(novaPessoa.getEmail(), "email", "E-mail já cadastrado.");
    }
  }

  private <T> void validarCampo(T value, Predicate<T> validator, String field, String message,
      Map<String, String> errors) {
    if (value != null && !validator.test(value)) {
      errors.put(field, message);
    }
  }

  private void validarExistenciaCampo(String value, String field, String message) {
    if (value != null) {
      boolean exists = field.equals("cpf")
          ? pessoaFisicaRepository.existsById(value)
          : pessoaFisicaRepository.findByEmail(value).isPresent();

      if (exists) {
        Map<String, String> error = new HashMap<>();
        error.put(field, message);
        throw new ValidacaoException(error);
      }
    }
  }

  private void atualizarCampos(PessoaFisica existente, PessoaFisica novosDados) {
    if (novosDados.getNome() != null) {
      existente.setNome(novosDados.getNome());
    }
    if (novosDados.getDataNascimento() != null) {
      existente.setDataNascimento(novosDados.getDataNascimento());
    }
    if (novosDados.getTelefone() != null) {
      existente.setTelefone(novosDados.getTelefone());
    }
    if (novosDados.getEmail() != null) {
      existente.setEmail(novosDados.getEmail());
    }
    if (novosDados.getEndereco() != null) {
      existente.setEndereco(novosDados.getEndereco());
    }
    if (novosDados.getCep() != null) {
      existente.setCep(novosDados.getCep());
    }
  }

  private String limpar(String data) {
    return data != null ? data.replaceAll("\\D", "") : null;
  }
}