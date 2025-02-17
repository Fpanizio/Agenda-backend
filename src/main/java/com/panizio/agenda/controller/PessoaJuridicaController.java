package com.panizio.agenda.controller;

import com.panizio.agenda.model.PessoaJuridica;
import com.panizio.agenda.service.PessoaJuridicaService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pjuridica")
public class PessoaJuridicaController {

  @Autowired
  private PessoaJuridicaService pessoaJuridicaService;

  @GetMapping
  public List<PessoaJuridica> listarPessoasJuridicas() {
    return pessoaJuridicaService.listarPessoasJuridicas();
  }

  @GetMapping("/{cnpj}")
  public ResponseEntity<PessoaJuridica> buscarPessoaJuridicaPorCnpj(@PathVariable String cnpj) {
    PessoaJuridica pessoaJuridica = pessoaJuridicaService.buscarPessoaJuridicaPorCnpj(cnpj);
    if (pessoaJuridica != null) {
      return new ResponseEntity<>(pessoaJuridica, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/filtrar-por-cnpj")
  public ResponseEntity<List<PessoaJuridica>> filtrarPorCpf(@RequestParam String prefixo) {
    List<PessoaJuridica> pessoasJuridica = pessoaJuridicaService.filtrarPorCnpj(prefixo);
    return ResponseEntity.ok(pessoasJuridica);
  }

  @PutMapping("/{cnpj}")
  public ResponseEntity<PessoaJuridica> atualizarPessoaJuridica(
      @PathVariable String cnpj,
      @RequestBody PessoaJuridica pessoaJuridicaAtualizada) {
    PessoaJuridica pessoaJuridica = pessoaJuridicaService.atualizarPessoaJuridica(cnpj, pessoaJuridicaAtualizada);
    return new ResponseEntity<>(pessoaJuridica, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<PessoaJuridica> criarPessoaJuridica(@RequestBody PessoaJuridica pessoaJuridica) {
    PessoaJuridica novaPessoaJuridica = pessoaJuridicaService.salvarPessoaJuridica(pessoaJuridica);
    return new ResponseEntity<>(novaPessoaJuridica, HttpStatus.CREATED);
  }

  @DeleteMapping("/{cnpj}")
  public ResponseEntity<Void> excluirUsuario(@PathVariable String cnpj) {
    pessoaJuridicaService.excluirUsuario(cnpj);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}