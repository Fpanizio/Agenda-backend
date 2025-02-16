package com.panizio.agenda.repository;

import com.panizio.agenda.model.PessoaFisica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PessoaFisicaRepository extends JpaRepository<PessoaFisica, String> {
  @Query("SELECT pf FROM PessoaFisica pf WHERE pf.cpf LIKE :prefixo%")
  List<PessoaFisica> findByCpfStartingWith(@Param("prefixo") String prefixo);

  Optional<PessoaFisica> findByEmail(String email);
}