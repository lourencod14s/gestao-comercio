package br.com.lourenco.gestao.repository;

import org.springframework.data.jpa.domain.Specification;

import br.com.lourenco.gestao.domain.entity.Produto;

public final class ProdutoSpecs {

    private ProdutoSpecs() {
    }

    public static Specification<Produto> ativo() {
        return (root, query, cb) -> cb.isTrue(root.<Boolean>get("ativo"));
    }

    public static Specification<Produto> nomeContem(String nome) {
        String padrao = "%" + escapar(nome.toLowerCase()) + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.<String>get("nome")), padrao, '\\');
    }

    public static Specification<Produto> daCategoria(Long categoriaId) {
        return (root, query, cb) -> cb.equal(root.get("categoria").<Long>get("id"), categoriaId);
    }

    public static Specification<Produto> estoqueBaixo() {
        return (root, query, cb) ->
                cb.lessThan(root.<Integer>get("estoqueAtual"), root.<Integer>get("estoqueMinimo"));
    }

    private static String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
