package br.com.lourenco.gestao.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.lourenco.gestao.domain.entity.Fornecedor;
import br.com.lourenco.gestao.dto.FornecedorRequest;
import br.com.lourenco.gestao.dto.FornecedorResponse;
import br.com.lourenco.gestao.exception.RecursoNaoEncontradoException;
import br.com.lourenco.gestao.exception.RegraNegocioException;
import br.com.lourenco.gestao.repository.FornecedorRepository;

@Service
public class FornecedorService {

    private final FornecedorRepository repository;

    public FornecedorService(FornecedorRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<FornecedorResponse> listar() {
        return repository.findByAtivoTrue().stream()
                .map(FornecedorResponse::de)
                .toList();
    }

    @Transactional(readOnly = true)
    public FornecedorResponse buscar(Long id) {
        return FornecedorResponse.de(buscarEntidade(id));
    }

    @Transactional
    public FornecedorResponse criar(FornecedorRequest request) {
        String cnpj = normalizarCnpj(request.cnpj());

        if (cnpj != null && repository.existsByCnpj(cnpj)) {
            throw new RegraNegocioException("Já existe um fornecedor com o CNPJ " + cnpj);
        }

        Fornecedor fornecedor = new Fornecedor(
                request.razaoSocial().trim(), cnpj, request.telefone(), request.email());
        return FornecedorResponse.de(repository.save(fornecedor));
    }

    @Transactional
    public FornecedorResponse atualizar(Long id, FornecedorRequest request) {
        Fornecedor fornecedor = buscarEntidade(id);
        String cnpj = normalizarCnpj(request.cnpj());

        if (cnpj != null && repository.existsByCnpjAndIdNot(cnpj, id)) {
            throw new RegraNegocioException("Já existe um fornecedor com o CNPJ " + cnpj);
        }

        fornecedor.setRazaoSocial(request.razaoSocial().trim());
        fornecedor.setCnpj(cnpj);
        fornecedor.setTelefone(request.telefone());
        fornecedor.setEmail(request.email());
        return FornecedorResponse.de(repository.save(fornecedor));
    }

    @Transactional
    public void desativar(Long id) {
        Fornecedor fornecedor = buscarEntidade(id);
        fornecedor.setAtivo(false);
        repository.save(fornecedor);
    }

    private Fornecedor buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Fornecedor não encontrado: id " + id));
    }

    private String normalizarCnpj(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) {
            return null;
        }
        return cnpj.replaceAll("\\D", "");
    }
}