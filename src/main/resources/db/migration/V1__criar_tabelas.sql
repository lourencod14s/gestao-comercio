CREATE TABLE usuario (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(120) NOT NULL,
    email       VARCHAR(160) NOT NULL UNIQUE,
    senha_hash  VARCHAR(255) NOT NULL,
    perfil      VARCHAR(20)  NOT NULL CHECK (perfil IN ('GERENTE','VENDEDOR')),
    ativo       BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em   TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE categoria (
    id    BIGSERIAL PRIMARY KEY,
    nome  VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE fornecedor (
    id            BIGSERIAL PRIMARY KEY,
    razao_social  VARCHAR(160) NOT NULL,
    cnpj          VARCHAR(18)  UNIQUE,
    telefone      VARCHAR(20),
    email         VARCHAR(160),
    ativo         BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE produto (
    id              BIGSERIAL PRIMARY KEY,
    sku             VARCHAR(40)  NOT NULL UNIQUE,
    nome            VARCHAR(160) NOT NULL,
    descricao       TEXT,
    preco_custo     NUMERIC(12,2) NOT NULL CHECK (preco_custo >= 0),
    preco_venda     NUMERIC(12,2) NOT NULL CHECK (preco_venda >= 0),
    estoque_atual   INTEGER NOT NULL DEFAULT 0 CHECK (estoque_atual >= 0),
    estoque_minimo  INTEGER NOT NULL DEFAULT 0,
    categoria_id    BIGINT NOT NULL REFERENCES categoria(id),
    fornecedor_id   BIGINT REFERENCES fornecedor(id),
    ativo           BOOLEAN NOT NULL DEFAULT TRUE,
    criado_em       TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE venda (
    id               BIGSERIAL PRIMARY KEY,
    usuario_id       BIGINT NOT NULL REFERENCES usuario(id),
    data_hora        TIMESTAMP NOT NULL DEFAULT NOW(),
    valor_total      NUMERIC(12,2) NOT NULL DEFAULT 0,
    forma_pagamento  VARCHAR(20) NOT NULL
        CHECK (forma_pagamento IN ('DINHEIRO','PIX','DEBITO','CREDITO')),
    status           VARCHAR(20) NOT NULL DEFAULT 'CONCLUIDA'
        CHECK (status IN ('CONCLUIDA','CANCELADA'))
);

CREATE TABLE item_venda (
    id              BIGSERIAL PRIMARY KEY,
    venda_id        BIGINT NOT NULL REFERENCES venda(id) ON DELETE CASCADE,
    produto_id      BIGINT NOT NULL REFERENCES produto(id),
    quantidade      INTEGER NOT NULL CHECK (quantidade > 0),
    preco_unitario  NUMERIC(12,2) NOT NULL
);

CREATE TABLE movimentacao_estoque (
    id          BIGSERIAL PRIMARY KEY,
    produto_id  BIGINT NOT NULL REFERENCES produto(id),
    usuario_id  BIGINT NOT NULL REFERENCES usuario(id),
    venda_id    BIGINT REFERENCES venda(id),
    tipo        VARCHAR(10) NOT NULL CHECK (tipo IN ('ENTRADA','SAIDA','AJUSTE')),
    quantidade  INTEGER NOT NULL,
    motivo      VARCHAR(200),
    data_hora   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_produto_categoria ON produto(categoria_id);
CREATE INDEX idx_venda_data        ON venda(data_hora);
CREATE INDEX idx_item_venda_prod   ON item_venda(produto_id);
CREATE INDEX idx_mov_produto       ON movimentacao_estoque(produto_id);