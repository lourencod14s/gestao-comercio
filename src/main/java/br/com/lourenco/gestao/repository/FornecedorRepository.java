package br.com.lourenco.gestao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lourenco.gestao.domain.entity.Fornecedor;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    List<Fornecedor> findByAtivoTrue();

    boolean existsByCnpj(String cnpj);

    boolean existsByCnpjAndIdNot(String cnpj, Long id);
}