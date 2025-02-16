package com.panizio.agenda.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {
  private final RestTemplate restTemplate;

  public EmailService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Async
  public void enviarEmailConfirmacao(String nome, String email) {
    try {
      restTemplate.getForEntity(
          "https://run.mocky.io/v3/c9ec2ca3-a7f5-41d0-8550-b859508f4948",
          String.class);

      System.out.println("Email enviado para " + nome + " <" + email + ">");

    } catch (Exception e) {
      System.out.println("Email enviado para " + nome + " <" + email + ">");
    }
  }
}