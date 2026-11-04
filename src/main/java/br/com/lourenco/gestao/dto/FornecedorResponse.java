package br.com.lourenco.gestao.dto;

import br.com.lourenco.gestao.domain.entity.Fornecedor;

public record FornecedorResponse(
        Long id,
        String razaoSocial,
        String cnpj,
        String telefone,
        String email,
        boolean ativo
) {

    public static FornecedorResponse de(Fornecedor f) {
        return new FornecedorResponse(
                f.getId(), f.getRazaoSocial(), f.getCnpj(),
                f.getTelefone(), f.getEmail(), f.isAtivo());
    }
}
