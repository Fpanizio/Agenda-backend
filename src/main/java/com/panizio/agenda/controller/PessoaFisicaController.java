package com.panizio.agenda.controller;

import com.panizio.agenda.exception.ValidacaoException;
import com.panizio.agenda.model.PessoaFisica;
import com.panizio.agenda.service.PessoaFisicaService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pfisica")
public class PessoaFisicaController {

    @Autowired
    private PessoaFisicaService pessoaFisicaService;

    @GetMapping
    public List<PessoaFisica> listarUsuarios() {
        return pessoaFisicaService.listarUsuarios();
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<PessoaFisica> buscarUsuarioPorCpf(@Valid @PathVariable String cpf) {
        PessoaFisica PessoaFisica = pessoaFisicaService.buscarUsuarioPorCpf(cpf);
        if (PessoaFisica != null) {
            return new ResponseEntity<>(PessoaFisica, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/filtrar-por-cpf")
    public ResponseEntity<List<PessoaFisica>> filtrarPorCpf(@Valid @RequestParam String prefixo) {
        List<PessoaFisica> pessoasFisicas = pessoaFisicaService.filtrarPorCpf(prefixo);
        return ResponseEntity.ok(pessoasFisicas);
    }

    @PutMapping("/{cpf}")
    public ResponseEntity<?> atualizarPessoaFisica(
            @PathVariable String cpf,
            @RequestBody PessoaFisica pessoaFisicaAtualizada) {
        try {
            PessoaFisica pessoaFisica = pessoaFisicaService.atualizarPessoaFisica(cpf, pessoaFisicaAtualizada);
            return new ResponseEntity<>(pessoaFisica, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, String> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            return new ResponseEntity<>(erro, HttpStatus.BAD_REQUEST);
        } catch (ValidacaoException e) {
            return new ResponseEntity<>(e.getErros(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<PessoaFisica> criarUsuario(@Valid @RequestBody PessoaFisica PessoaFisica) {
        PessoaFisica novoUsuario = pessoaFisicaService.salvarUsuario(PessoaFisica);
        return new ResponseEntity<>(novoUsuario, HttpStatus.CREATED);
    }

    @DeleteMapping("/{cpf}")
    public ResponseEntity<Void> excluirUsuario(@Valid @PathVariable String cpf) {
        pessoaFisicaService.excluirUsuario(cpf);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}