package br.com.lourenco.gestao.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.lourenco.gestao.domain.entity.Produto;

public interface ProdutoRepository
        extends JpaRepository<Produto, Long>, JpaSpecificationExecutor<Produto> {

    @Override
    @EntityGraph(attributePaths = {"categoria", "fornecedor"})
    Page<Produto> findAll(Specification<Produto> spec, Pageable pageable);

    boolean existsBySku(String sku);

    boolean existsBySkuAndIdNot(String sku, Long id);

    boolean existsByCategoriaId(Long categoriaId);
}