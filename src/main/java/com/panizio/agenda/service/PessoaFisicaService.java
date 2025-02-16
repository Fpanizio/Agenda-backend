package com.panizio.agenda.service;

import com.panizio.agenda.exception.ValidacaoException;
import com.panizio.agenda.model.PessoaFisica;
import com.panizio.agenda.repository.PessoaFisicaRepository;
import com.panizio.agenda.utils.ValidacaoUtils;
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
    return pessoaFisicaRepository.findById(limparCpf(cpf)).orElse(null);
  }

  public List<PessoaFisica> filtrarPorCpf(String prefixo) {
    return pessoaFisicaRepository.findByCpfStartingWith(prefixo);
  }

  public PessoaFisica salvarUsuario(PessoaFisica pessoaFisica) {
    pessoaFisica.setCpf(limparCpf(pessoaFisica.getCpf()));
    validarPessoaFisica(pessoaFisica, true);

    PessoaFisica savedPessoa = pessoaFisicaRepository.save(pessoaFisica);

    emailService.enviarEmailConfirmacao(savedPessoa.getNome(), savedPessoa.getEmail()); 

    return savedPessoa;
  }

  public PessoaFisica atualizarPessoaFisica(String cpf, PessoaFisica pessoaFisica) {
    pessoaFisica.setCpf(limparCpf(pessoaFisica.getCpf()));
    PessoaFisica pessoaExistente = pessoaFisicaRepository.findById(cpf)
        .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

    validarCamposUnicos(pessoaFisica, pessoaExistente);
    validarPessoaFisica(pessoaFisica, false);
    atualizarCampos(pessoaExistente, pessoaFisica);

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

  private String limparCpf(String cpf) {
    return cpf != null ? cpf.replaceAll("\\D", "") : null;
  }
}