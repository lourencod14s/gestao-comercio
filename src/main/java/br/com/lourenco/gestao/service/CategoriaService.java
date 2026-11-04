package br.com.lourenco.gestao.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.lourenco.gestao.domain.entity.Categoria;
import br.com.lourenco.gestao.dto.CategoriaRequest;
import br.com.lourenco.gestao.dto.CategoriaResponse;
import br.com.lourenco.gestao.exception.RecursoNaoEncontradoException;
import br.com.lourenco.gestao.exception.RegraNegocioException;
import br.com.lourenco.gestao.repository.CategoriaRepository;
import br.com.lourenco.gestao.repository.ProdutoRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;
    private final ProdutoRepository produtoRepository;

    public CategoriaService(CategoriaRepository repository, ProdutoRepository produtoRepository) {
        this.repository = repository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return repository.findAll().stream()
                .map(CategoriaResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoriaResponse buscar(Long id) {
        return CategoriaResponse.de(buscarEntidade(id));
    }

    @Transactional
    public CategoriaResponse criar(CategoriaRequest request) {
        String nome = request.nome().trim();

        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new RegraNegocioException("Já existe uma categoria com o nome '" + nome + "'");
        }

        Categoria salva = repository.save(new Categoria(nome));
        return CategoriaResponse.de(salva);
    }

    @Transactional
    public CategoriaResponse atualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscarEntidade(id);
        String nome = request.nome().trim();

        boolean mudouDeNome = !categoria.getNome().equalsIgnoreCase(nome);
        if (mudouDeNome && repository.existsByNomeIgnoreCase(nome)) {
            throw new RegraNegocioException("Já existe uma categoria com o nome '" + nome + "'");
        }

        categoria.setNome(nome);
        return CategoriaResponse.de(repository.save(categoria));
    }

        @Transactional
    public void excluir(Long id) {
        Categoria categoria = buscarEntidade(id);

        if (produtoRepository.existsByCategoriaId(id)) {
            throw new RegraNegocioException(
                    "Não é possível excluir a categoria: existem produtos vinculados a ela");
        }

        repository.delete(categoria);
    }

    private Categoria buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Categoria não encontrada: id " + id));
    }
}