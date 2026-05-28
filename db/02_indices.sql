-- indexes e constraints de unicidade parcial.
-- regra de negócio: uma cópia so pode ter um emprestimo aberto por vez
-- devolvidos e atrasados antigos ficam de fora do índice
CREATE UNIQUE INDEX uq_copia_emprestimo_aberto
    ON emprestimo (copia_id)
    WHERE status = 'ABERTO';

-- um leitor nao pode ter 2 reservas ATIVAS pra mesma obra
CREATE UNIQUE INDEX uq_reserva_ativa_leitor_obra
    ON reserva (leitor_id, obra_id)
    WHERE status = 'ATIVA';

-- indexes leitura
CREATE INDEX idx_emprestimo_leitor      ON emprestimo (leitor_id);
CREATE INDEX idx_emprestimo_status      ON emprestimo (status);
CREATE INDEX idx_emprestimo_copia       ON emprestimo (copia_id);
CREATE INDEX idx_copia_obra             ON copia      (obra_id);
CREATE INDEX idx_copia_estado           ON copia      (estado);
CREATE INDEX idx_reserva_status         ON reserva    (status);
CREATE INDEX idx_reserva_leitor         ON reserva    (leitor_id);
CREATE INDEX idx_obra_titulo            ON obra       (titulo);
