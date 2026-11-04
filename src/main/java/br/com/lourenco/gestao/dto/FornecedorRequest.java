package br.com.lourenco.gestao.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record FornecedorRequest(

        @NotBlank(message = "A razão social é obrigatória")
        @Size(max = 160, message = "A razão social deve ter no máximo 160 caracteres")
        String razaoSocial,

        @Pattern(regexp = "^(\\d{14}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2})$",
                 message = "CNPJ inválido. Use 14 dígitos ou o formato 00.000.000/0000-00")
        String cnpj,

        @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres")
        String telefone,

        @Email(message = "E-mail inválido")
        @Size(max = 160, message = "O e-mail deve ter no máximo 160 caracteres")
        String email
) {
}