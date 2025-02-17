package com.panizio.agenda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Objects;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panizio.agenda.utils.PointSerializer;

@Entity
public class PessoaFisica {

  @Id
  @NotBlank(message = "CPF é obrigatório")
  private String cpf;

  @NotBlank(message = "Nome é obrigatório")
  private String nome;

  @NotNull(message = "Data de nascimento é obrigatória")
  private LocalDate dataNascimento;

  @NotBlank(message = "Telefone é obrigatório")
  private String telefone;

  @NotBlank(message = "CEP é obrigatório")
  private String cep;

  @Column(unique = true)
  @NotBlank(message = "E-mail é obrigatório")
  private String email;

  @NotBlank(message = "Endereço é obrigatório")
  private String endereco;

  @JsonSerialize(using = PointSerializer.class)
  private Point coordenadas;

  public PessoaFisica() {
  }

  public PessoaFisica(
      String cpf,
      String nome,
      LocalDate dataNascimento,
      String telefone,
      String cep,
      String email,
      String endereco,
      Point coordenadas) {
    this.cpf = cpf;
    this.nome = nome;
    this.dataNascimento = dataNascimento;
    this.telefone = telefone;
    this.cep = cep;
    this.email = email;
    this.endereco = endereco;
    this.coordenadas = coordenadas;
  }

  public String getCpf() {
    return cpf;
  }

  public void setCpf(String cpf) {
    this.cpf = cpf;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }

  public LocalDate getDataNascimento() {
    return dataNascimento;
  }

  public void setDataNascimento(LocalDate dataNascimento) {
    this.dataNascimento = dataNascimento;
  }

  public String getTelefone() {
    return telefone;
  }

  public void setTelefone(String telefone) {
    this.telefone = telefone;
  }

  public String getCep() {
    return cep;
  }

  public void setCep(String cep) {
    this.cep = cep;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getEndereco() {
    return endereco;
  }

  public void setEndereco(String endereco) {
    this.endereco = endereco;
  }

  public Point getCoordenadas() {
    return coordenadas;
  }

  public void setCoordenadas(Point coordenadas) {
    this.coordenadas = coordenadas;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    PessoaFisica that = (PessoaFisica) o;
    return Objects.equals(cpf, that.cpf) &&
        Objects.equals(nome, that.nome) &&
        Objects.equals(dataNascimento, that.dataNascimento);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cpf, nome, dataNascimento);
  }

  @Override
  public String toString() {
    return "PessoaFisica{" +
        "cpf='" + cpf + '\'' +
        ", nome='" + nome + '\'' +
        ", dataNascimento=" + dataNascimento +
        '}';
  }
}