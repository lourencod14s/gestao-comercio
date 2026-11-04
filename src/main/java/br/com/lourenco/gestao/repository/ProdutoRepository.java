package br.com.lourenco.gestao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lourenco.gestao.domain.entity.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @EntityGraph(attributePaths = {"categoria", "fornecedor"})
    List<Produto> findByAtivoTrue();

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    boolean existsByCategoriaId(Long categoriaId);
}
