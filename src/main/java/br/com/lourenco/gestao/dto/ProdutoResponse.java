package br.com.lourenco.gestao.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.lourenco.gestao.domain.entity.Produto;

public record ProdutoResponse(
        Long id,
        String sku,
        String nome,
        String descricao,
        BigDecimal precoCusto,
        BigDecimal precoVenda,
        int estoqueAtual,
        int estoqueMinimo,
        boolean estoqueBaixo,
        CategoriaResponse categoria,
        FornecedorResponse fornecedor,
        boolean ativo,
        LocalDateTime criadoEm
) {

    public static ProdutoResponse de(Produto p) {
        FornecedorResponse fornecedor = p.getFornecedor() != null
                ? FornecedorResponse.de(p.getFornecedor())
                : null;

        return new ProdutoResponse(
                p.getId(), p.getSku(), p.getNome(), p.getDescricao(),
                p.getPrecoCusto(), p.getPrecoVenda(),
                p.getEstoqueAtual(), p.getEstoqueMinimo(),
                p.getEstoqueAtual() < p.getEstoqueMinimo(),
                CategoriaResponse.de(p.getCategoria()),
                fornecedor,
                p.isAtivo(), p.getCriadoEm());
    }
}
