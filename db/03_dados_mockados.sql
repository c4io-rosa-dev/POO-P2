-- dados mockados para demonstração
-- referenciado por cpf, codigo_tombo, isbn

-- PESSOAS + papeis (heranca)

-- funcionários admin
INSERT INTO pessoa (nome, cpf, email, telefone, data_nascimento) VALUES
    ('Caio Rosa',          '11111111111', 'caio@biblioteca.local',  '(11) 90000-0001', '2006-09-06'),
    ('Vinicius Kenji',     '22222222222', 'kenji@biblioteca.local',  '(11) 90000-0002', '2004-06-22');

INSERT INTO funcionario (id, matricula, cargo, salario, senha_hash) VALUES
    ((SELECT id FROM pessoa WHERE cpf = '11111111111'), 'F001', 'Administrador', 4500.00, 'caio123'),
    ((SELECT id FROM pessoa WHERE cpf = '22222222222'), 'F002', 'Administrador',     2800.00, 'kenji123');

-- leitores
INSERT INTO pessoa (nome, cpf, email, telefone, data_nascimento) VALUES
    ('Ana Souza',        '33333333333', 'ana.souza@email.com',    '(11) 91111-1111', '2002-05-18'),
    ('Bruno Lima',       '44444444444', 'bruno.lima@email.com',   '(11) 92222-2222', '1999-11-30'),
    ('Carla Mendes',     '55555555555', 'carla.mendes@email.com', '(11) 93333-3333', '2005-01-09'),
    ('Daniel Oliveira',  '66666666666', 'daniel.o@email.com',     '(11) 94444-4444', '1992-09-25');

INSERT INTO leitor (id, matricula) VALUES
    ((SELECT id FROM pessoa WHERE cpf = '33333333333'), 'L001'),
    ((SELECT id FROM pessoa WHERE cpf = '44444444444'), 'L002'),
    ((SELECT id FROM pessoa WHERE cpf = '55555555555'), 'L003'),
    ((SELECT id FROM pessoa WHERE cpf = '66666666666'), 'L004');

-- acervo: obras + copias

INSERT INTO obra (titulo, autor, editora, ano_publicacao, isbn, categoria, tipo) VALUES
    ('Dom Casmurro',                 'Machado de Assis',   'Globo',         1899, '978-85-2500-001-1', 'Romance',     'LIVRO'),
    ('O Cortico',                    'Aluisio Azevedo',    'Martin Claret', 1890, '978-85-2500-002-2', 'Romance',     'LIVRO'),
    ('Clean Code',                   'Robert C. Martin',   'Alta Books',    2008, '978-85-7608-003-3', 'Tecnologia',  'LIVRO'),
    ('Effective Java',               'Joshua Bloch',       'Alta Books',    2017, '978-85-7608-004-4', 'Tecnologia',  'LIVRO'),
    ('Superinteressante - Ed. 450',  'Editora Abril',      'Abril',         2024, '977-19-8132-005-5', 'Divulgacao',  'REVISTA'),
    ('Revista Pesquisa FAPESP',      'Varios',             'FAPESP',        2024, '977-19-8132-006-6', 'Ciencia',     'PERIODICO');

INSERT INTO copia (obra_id, codigo_tombo, estado, observacoes) VALUES
    ((SELECT id FROM obra WHERE isbn = '978-85-2500-001-1'), 'TBO-0001', 'DISPONIVEL',  NULL),
    ((SELECT id FROM obra WHERE isbn = '978-85-2500-001-1'), 'TBO-0002', 'DISPONIVEL',  NULL),
    ((SELECT id FROM obra WHERE isbn = '978-85-2500-002-2'), 'TBO-0003', 'DISPONIVEL',  NULL),
    ((SELECT id FROM obra WHERE isbn = '978-85-7608-003-3'), 'TBO-0004', 'EMPRESTADA',  NULL),
    ((SELECT id FROM obra WHERE isbn = '978-85-7608-003-3'), 'TBO-0005', 'DISPONIVEL',  NULL),
    ((SELECT id FROM obra WHERE isbn = '978-85-7608-004-4'), 'TBO-0006', 'EMPRESTADA',  NULL),
    ((SELECT id FROM obra WHERE isbn = '978-85-7608-004-4'), 'TBO-0007', 'RESERVADA',   NULL),
    ((SELECT id FROM obra WHERE isbn = '977-19-8132-005-5'), 'TBO-0008', 'DISPONIVEL',  'Edicao especial'),
    ((SELECT id FROM obra WHERE isbn = '977-19-8132-006-6'), 'TBO-0009', 'DISPONIVEL',  NULL),
    ((SELECT id FROM obra WHERE isbn = '978-85-2500-002-2'), 'TBO-0010', 'DANIFICADA',  'Capa rasgada');


-- operações: empréstimos + reservas

-- 2 empréstimos ABERTOS
INSERT INTO emprestimo (leitor_id, copia_id, funcionario_id, data_emprestimo, data_prevista_devolucao, status) VALUES
    ((SELECT id FROM pessoa      WHERE cpf = '33333333333'),
     (SELECT id FROM copia       WHERE codigo_tombo = 'TBO-0004'),
     (SELECT id FROM pessoa      WHERE cpf = '11111111111'),
     CURRENT_DATE - INTERVAL '5 days',
     CURRENT_DATE + INTERVAL '9 days',
     'ABERTO'),
    ((SELECT id FROM pessoa      WHERE cpf = '44444444444'),
     (SELECT id FROM copia       WHERE codigo_tombo = 'TBO-0006'),
     (SELECT id FROM pessoa      WHERE cpf = '22222222222'),
     CURRENT_DATE - INTERVAL '20 days',
     CURRENT_DATE - INTERVAL '6 days',
     'ABERTO');

-- 1 emprestimo DEVOLVIDO
INSERT INTO emprestimo (leitor_id, copia_id, funcionario_id, data_emprestimo, data_prevista_devolucao, data_devolucao, status) VALUES
    ((SELECT id FROM pessoa      WHERE cpf = '55555555555'),
     (SELECT id FROM copia       WHERE codigo_tombo = 'TBO-0001'),
     (SELECT id FROM pessoa      WHERE cpf = '11111111111'),
     CURRENT_DATE - INTERVAL '40 days',
     CURRENT_DATE - INTERVAL '26 days',
     CURRENT_DATE - INTERVAL '28 days',
     'DEVOLVIDO');

-- 1 reserva ATIVA: leitor Carla quer Effective Java
INSERT INTO reserva (leitor_id, obra_id, data_expiracao, status) VALUES
    ((SELECT id FROM pessoa WHERE cpf = '55555555555'),
     (SELECT id FROM obra   WHERE isbn = '978-85-7608-004-4'),
     NOW() + INTERVAL '7 days',
     'ATIVA');
