package com.panizio.agenda.model;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.panizio.agenda.utils.PointSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

@Entity
public class PessoaJuridica {

  @Id
  @NotBlank(message = "CNPJ é obrigatório")
  private String cnpj;

  @NotBlank(message = "Razão Social é obrigatório")
  private String razaoSocial;

  @NotBlank(message = "Nome Fantasia é obrigatório")
  private String nomeFantasia;

  @NotBlank(message = "Telefone é obrigatório")
  private String telefone;

  @NotBlank(message = "Email é obrigatório")
  @Column(unique = true)
  private String email;

  @NotBlank(message = "Endereço é obrigatório")
  private String endereco;

  @NotBlank(message = "CEP é obrigatório")
  private String cep;

  @JsonSerialize(using = PointSerializer.class)
  private Point coordenadas;

  public PessoaJuridica() {
  }

  public PessoaJuridica(
      String cnpj, String razaoSocial, String nomeFantasia, String telefone, String email,
      String endereco, String cep) {
    this.cnpj = cnpj;
    this.razaoSocial = razaoSocial;
    this.nomeFantasia = nomeFantasia;
    this.telefone = telefone;
    this.email = email;
    this.endereco = endereco;
    this.cep = cep;

  }

  // Getters e Setters
  public String getCnpj() {
    return cnpj;
  }

  public void setCnpj(String cnpj) {
    this.cnpj = cnpj;
  }

  public String getRazaoSocial() {
    return razaoSocial;
  }

  public void setRazaoSocial(String razaoSocial) {
    this.razaoSocial = razaoSocial;
  }

  public String getNomeFantasia() {
    return nomeFantasia;
  }

  public void setNomeFantasia(String nomeFantasia) {
    this.nomeFantasia = nomeFantasia;
  }

  public String getTelefone() {
    return telefone;
  }

  public void setTelefone(String telefone) {
    this.telefone = telefone;
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

  public String getCep() {
    return cep;
  }

  public void setCep(String cep) {
    this.cep = cep;
  }

  public Point getCoordenadas() {
    return coordenadas;
  }

  public void setCoordenadas(Point coordenadas) {
    this.coordenadas = coordenadas;
  }
}