package br.com.lourenco.gestao.dto;

import br.com.lourenco.gestao.domain.entity.Categoria;

public record CategoriaResponse(Long id, String nome) {

    public static CategoriaResponse de(Categoria categoria) {
        return new CategoriaResponse(categoria.getId(), categoria.getNome());
    }
}