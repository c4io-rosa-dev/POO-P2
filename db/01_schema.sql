-- schema sistema biblioteca

CREATE TABLE pessoa (
    id              SERIAL       PRIMARY KEY,
    nome            VARCHAR(150) NOT NULL,
    cpf             CHAR(11)     NOT NULL UNIQUE,
    email           VARCHAR(150) UNIQUE,
    telefone        VARCHAR(20),
    data_nascimento DATE,
    criado_em       TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE leitor (
    id           INTEGER     PRIMARY KEY REFERENCES pessoa(id) ON DELETE CASCADE,
    matricula    VARCHAR(20) NOT NULL UNIQUE,
    ativo        BOOLEAN     NOT NULL DEFAULT TRUE,
    inscrito_em  DATE        NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE funcionario (
    id           INTEGER       PRIMARY KEY REFERENCES pessoa(id) ON DELETE CASCADE,
    matricula    VARCHAR(20)   NOT NULL UNIQUE,
    cargo        VARCHAR(50)   NOT NULL,
    salario      NUMERIC(10,2) CHECK (salario IS NULL OR salario >= 0),
    admitido_em  DATE          NOT NULL DEFAULT CURRENT_DATE,
    senha   VARCHAR(255)  NOT NULL
);

-- obra e cópia física da mesma

CREATE TABLE obra (
    id              SERIAL       PRIMARY KEY,
    titulo          VARCHAR(255) NOT NULL,
    autor           VARCHAR(255) NOT NULL,
    editora         VARCHAR(150),
    ano_publicacao  INTEGER      CHECK (ano_publicacao IS NULL OR ano_publicacao BETWEEN 1000 AND 2100),
    isbn            VARCHAR(20)  UNIQUE,
    categoria       VARCHAR(80),
    tipo            VARCHAR(20)  NOT NULL DEFAULT 'LIVRO'
                                 CHECK (tipo IN ('LIVRO','REVISTA','PERIODICO'))
);

CREATE TABLE copia (
    id            SERIAL       PRIMARY KEY,
    obra_id       INTEGER      NOT NULL REFERENCES obra(id) ON DELETE CASCADE,
    codigo_tombo  VARCHAR(30)  NOT NULL UNIQUE,
    estado        VARCHAR(20)  NOT NULL DEFAULT 'DISPONIVEL'
                               CHECK (estado IN ('DISPONIVEL','EMPRESTADA','RESERVADA','DANIFICADA','PERDIDA')),
    adquirida_em  DATE         NOT NULL DEFAULT CURRENT_DATE,
    observacoes   TEXT
);

-- operações: empréstimo e reserva

CREATE TABLE emprestimo (
    id                       SERIAL      PRIMARY KEY,
    leitor_id                INTEGER     NOT NULL REFERENCES leitor(id),
    copia_id                 INTEGER     NOT NULL REFERENCES copia(id),
    funcionario_id           INTEGER     NOT NULL REFERENCES funcionario(id),
    data_emprestimo          DATE        NOT NULL DEFAULT CURRENT_DATE,
    data_prevista_devolucao  DATE        NOT NULL,
    data_devolucao           DATE,
    status                   VARCHAR(20) NOT NULL DEFAULT 'ABERTO'
                                         CHECK (status IN ('ABERTO','DEVOLVIDO','ATRASADO')),
    CONSTRAINT ck_emprestimo_datas
        CHECK (data_prevista_devolucao >= data_emprestimo),
    CONSTRAINT ck_emprestimo_devolucao_coerente
        CHECK (
            (status = 'DEVOLVIDO' AND data_devolucao IS NOT NULL)
            OR (status <> 'DEVOLVIDO' AND data_devolucao IS NULL)
        )
);

CREATE TABLE reserva (
    id              SERIAL      PRIMARY KEY,
    leitor_id       INTEGER     NOT NULL REFERENCES leitor(id),
    obra_id         INTEGER     NOT NULL REFERENCES obra(id),
    data_reserva    TIMESTAMP   NOT NULL DEFAULT NOW(),
    data_expiracao  TIMESTAMP   NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'ATIVA'
                                CHECK (status IN ('ATIVA','ATENDIDA','CANCELADA','EXPIRADA')),
    CONSTRAINT ck_reserva_datas
        CHECK (data_expiracao > data_reserva)
);

-- comentários

COMMENT ON TABLE pessoa       IS 'Classe base da herança';
COMMENT ON TABLE leitor       IS 'Herança de pessoa PK compartilhada';
COMMENT ON TABLE funcionario  IS 'Herança de pessoa PK compartilhada';
COMMENT ON TABLE obra         IS 'Título/obra abstrata';
COMMENT ON TABLE copia        IS 'Cada cópia física de uma obra';
COMMENT ON TABLE emprestimo   IS 'Associação ternária leitor, cópia e funcionário';
COMMENT ON TABLE reserva      IS 'Associação leitor e obra, reserva título';
