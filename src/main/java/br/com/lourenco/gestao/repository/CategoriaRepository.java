package br.com.lourenco.gestao.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lourenco.gestao.domain.entity.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNomeIgnoreCase(String nome);
}