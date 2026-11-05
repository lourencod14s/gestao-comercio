package br.com.lourenco.gestao.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.lourenco.gestao.domain.entity.Categoria;
import br.com.lourenco.gestao.domain.entity.Fornecedor;
import br.com.lourenco.gestao.domain.entity.Produto;
import br.com.lourenco.gestao.dto.ProdutoRequest;
import br.com.lourenco.gestao.dto.ProdutoResponse;
import br.com.lourenco.gestao.exception.RecursoNaoEncontradoException;
import br.com.lourenco.gestao.exception.RegraNegocioException;
import br.com.lourenco.gestao.repository.CategoriaRepository;
import br.com.lourenco.gestao.repository.FornecedorRepository;
import br.com.lourenco.gestao.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import br.com.lourenco.gestao.dto.PaginaResponse;
import br.com.lourenco.gestao.repository.ProdutoSpecs;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FornecedorRepository fornecedorRepository;

    private static final List<String> CAMPOS_ORDENAVEIS = List.of(
        "id", "nome", "sku", "precoCusto", "precoVenda",
        "estoqueAtual", "estoqueMinimo", "criadoEm");

    public ProdutoService(ProdutoRepository produtoRepository,
                          CategoriaRepository categoriaRepository,
                          FornecedorRepository fornecedorRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
        this.fornecedorRepository = fornecedorRepository;
    }

    @Transactional(readOnly = true)
    public PaginaResponse<ProdutoResponse> listar(String nome, Long categoriaId,
                                                  boolean somenteEstoqueBaixo,
                                                  Pageable pageable) {
        validarOrdenacao(pageable);

        Specification<Produto> spec = ProdutoSpecs.ativo();

        if (nome != null && !nome.isBlank()) {
            spec = spec.and(ProdutoSpecs.nomeContem(nome.trim()));
        }
        if (categoriaId != null) {
            spec = spec.and(ProdutoSpecs.daCategoria(categoriaId));
        }
        if (somenteEstoqueBaixo) {
            spec = spec.and(ProdutoSpecs.estoqueBaixo());
        }

        Page<ProdutoResponse> pagina = produtoRepository.findAll(spec, pageable)
                .map(ProdutoResponse::de);
        return PaginaResponse.de(pagina);
    }

    private void validarOrdenacao(Pageable pageable) {
        pageable.getSort().forEach(ordem -> {
            if (!CAMPOS_ORDENAVEIS.contains(ordem.getProperty())) {
                throw new RegraNegocioException("Campo de ordenação inválido: '"
                     + ordem.getProperty() + "'. Use um destes: " + CAMPOS_ORDENAVEIS);
            }
        });
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscar(Long id) {
        return ProdutoResponse.de(buscarEntidade(id));
    }

    @Transactional
    public ProdutoResponse criar(ProdutoRequest request) {
        String sku = normalizarSku(request.sku());

        if (produtoRepository.existsBySku(sku)) {
            throw new RegraNegocioException("Já existe um produto com o SKU " + sku);
        }

        Categoria categoria = buscarCategoria(request.categoriaId());
        Fornecedor fornecedor = buscarFornecedor(request.fornecedorId());

        Produto produto = new Produto(sku, request.nome().trim(),
                request.precoCusto(), request.precoVenda(), categoria);
        preencherCamposOpcionais(produto, request, fornecedor);

        return ProdutoResponse.de(produtoRepository.save(produto));
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest request) {
        Produto produto = buscarEntidade(id);
        String sku = normalizarSku(request.sku());

        if (produtoRepository.existsBySkuAndIdNot(sku, id)) {
            throw new RegraNegocioException("Já existe um produto com o SKU " + sku);
        }

        produto.setSku(sku);
        produto.setNome(request.nome().trim());
        produto.setPrecoCusto(request.precoCusto());
        produto.setPrecoVenda(request.precoVenda());
        produto.setCategoria(buscarCategoria(request.categoriaId()));
        preencherCamposOpcionais(produto, request, buscarFornecedor(request.fornecedorId()));

        return ProdutoResponse.de(produtoRepository.save(produto));
    }

    @Transactional
    public void desativar(Long id) {
        Produto produto = buscarEntidade(id);
        produto.setAtivo(false);
        produtoRepository.save(produto);
    }

    private void preencherCamposOpcionais(Produto produto, ProdutoRequest request,
                                          Fornecedor fornecedor) {
        produto.setDescricao(request.descricao());
        produto.setEstoqueMinimo(request.estoqueMinimo() != null ? request.estoqueMinimo() : 0);
        produto.setFornecedor(fornecedor);
    }

    private Produto buscarEntidade(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Produto não encontrado: id " + id));
    }

    private Categoria buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException(
                        "A categoria informada não existe: id " + id));
    }

    private Fornecedor buscarFornecedor(Long id) {
        if (id == null) {
            return null;
        }
        Fornecedor fornecedor = fornecedorRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException(
                        "O fornecedor informado não existe: id " + id));
        if (!fornecedor.isAtivo()) {
            throw new RegraNegocioException("O fornecedor informado está inativo: id " + id);
        }
        return fornecedor;
    }

    private String normalizarSku(String sku) {
        return sku.trim().toUpperCase();
    }
}