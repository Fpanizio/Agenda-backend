package com.panizio.agenda.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.panizio.agenda.model.PessoaJuridica;

public interface PessoaJuridicaRepository extends JpaRepository<PessoaJuridica, String> {
  @Query("SELECT pj FROM PessoaJuridica pj WHERE pj.cnpj LIKE :prefixo%")
  List<PessoaJuridica> findByCnpjStartingWith(@Param("prefixo") String prefixo);

  Optional<PessoaJuridica> findByEmail(String email);

}