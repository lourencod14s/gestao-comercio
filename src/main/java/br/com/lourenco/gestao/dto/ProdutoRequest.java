package br.com.lourenco.gestao.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProdutoRequest(

        @NotBlank(message = "O SKU é obrigatório")
        @Size(max = 40, message = "O SKU deve ter no máximo 40 caracteres")
        String sku,

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 160, message = "O nome deve ter no máximo 160 caracteres")
        String nome,

        @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
        String descricao,

        @NotNull(message = "O preço de custo é obrigatório")
        @PositiveOrZero(message = "O preço de custo não pode ser negativo")
        @Digits(integer = 10, fraction = 2, message = "Preço de custo inválido (máximo 2 casas decimais)")
        BigDecimal precoCusto,

        @NotNull(message = "O preço de venda é obrigatório")
        @PositiveOrZero(message = "O preço de venda não pode ser negativo")
        @Digits(integer = 10, fraction = 2, message = "Preço de venda inválido (máximo 2 casas decimais)")
        BigDecimal precoVenda,

        @PositiveOrZero(message = "O estoque mínimo não pode ser negativo")
        Integer estoqueMinimo,

        @NotNull(message = "A categoria é obrigatória")
        Long categoriaId,

        Long fornecedorId
) {
}
