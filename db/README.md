# Banco de dados — Biblioteca POO-P2

Schema PostgreSQL do projeto final de POO (sistema de controle de biblioteca).

## Pré-requisitos

- PostgreSQL 18 instalado (`C:\Program Files\PostgreSQL\18`)
- pgAdmin 4 com uma conexão configurada ao servidor local (porta 5432)
- A senha do usuário `postgres` definida na instalação

## Como aplicar (passo a passo no pgAdmin)

1. Abra o pgAdmin e conecte no servidor local.
2. Clique com o botão direito em `Databases` → `Query Tool` (vai abrir conectado ao banco `postgres`).
3. Cole o conteúdo de `00_create_database.sql` e execute (F5). Isso cria o banco `biblioteca_poo`.
4. **Mude a conexão** do Query Tool pro banco recém-criado: feche essa aba e abra um novo Query Tool clicando com botão direito no banco `biblioteca_poo` → `Query Tool`.
5. Cole e execute, **nessa ordem**:
   - `01_schema.sql` — cria as 7 tabelas, FKs e CHECKs
   - `02_indices.sql` — cria os índices (inclusive os de unicidade parcial)
   - `03_dados_mockados.sql` — popula dados de teste
6. Pra resetar do zero a qualquer momento: rodar `99_drop_all.sql` no banco `biblioteca_poo` e voltar pro passo 5.

## Conexão JDBC (para a dupla configurar no Java)

```
URL:      jdbc:postgresql://localhost:5432/biblioteca_poo
Usuario:  postgres
Senha:    (a que voce definiu na instalacao do Postgres)
Driver:   org.postgresql.Driver
Maven:    org.postgresql:postgresql:42.7.4
```

## Diagrama ER

```
                +------------------+
                |     pessoa       |
                | id PK            |
                | nome, cpf UQ     |
                | email UQ         |
                | matricula UQ     |
                +--------+---------+
                         |
              +----------+-----------+
              |                      |
       +------v------+        +------v---------+
       |   leitor    |        |  funcionario   |
       | id PK,FK    |        | id PK,FK       |
       | ativo       |        | cargo, salario |
       | inscrito_em |        | senha          |
       +-----+-------+        +-------+--------+
             |                        |
             |                        |
             |  +-----------+         |
             |  |   obra    |         |
             |  | id PK     |         |
             |  | titulo    |         |
             |  | tipo CK   |         |
             |  +-----+-----+         |
             |        |               |
             |        | 1..N          |
             |  +-----v-----+         |
             |  |   copia   |         |
             |  | id PK     |         |
             |  | obra_id   |         |
             |  | estado CK |         |
             |  +-----+-----+         |
             |        |               |
             |        |               |
       +-----v--------v---------------v-----+
       |            emprestimo              |
       | leitor_id  copia_id  funcionario_id|
       | status: ABERTO/DEVOLVIDO/ATRASADO  |
       +------------------------------------+

       +-----------------+
       |     reserva     |  leitor_id + obra_id
       | status ATIVA... |  (reserva a obra, nao a copia)
       +-----------------+
```

## Mapeamento dos conceitos POO no schema

| Conceito POO     | Implementação no banco                                                                       |
| ---------------- | -------------------------------------------------------------------------------------------- |
| **Herança**      | `pessoa` é a tabela-pai. `leitor` e `funcionario` têm PK que é também FK para `pessoa.id` (Class Table Inheritance). Em Java: `class Leitor extends Pessoa` e `class Funcionario extends Pessoa`. |
| **Polimorfismo** | `obra.tipo` discrimina LIVRO / REVISTA / PERIODICO. O Java pode ter `class Obra` abstrata com subclasses `Livro`, `Revista`, `Periodico` e o DAO escolhe a subclasse com base no `tipo`. |
| **Associação**   | `emprestimo` é associação ternária entre `leitor`, `copia` e `funcionario`. `reserva` é associação binária entre `leitor` e `obra`. |
| **Agregação**    | `obra` (1) → `copia` (N): cópias pertencem a uma obra, mas têm identidade própria (PK e estado). Se a obra for removida (CASCADE), as cópias vão junto — coerente com agregação forte / composição. |

## Regras de integridade que o banco aplica

Tem várias regras que **não dependem do código Java** — o banco já garante. Útil mencionar na defesa:

- CPF e ISBN únicos (`UNIQUE`)
- Estado de cópia restrito a 5 valores (`CHECK`)
- Status de empréstimo restrito a 3 valores, com checagem de coerência entre `status='DEVOLVIDO'` e `data_devolucao IS NOT NULL`
- `data_prevista_devolucao >= data_emprestimo`
- **Apenas 1 empréstimo ABERTO por cópia ao mesmo tempo** (índice único parcial)
- **1 leitor não pode ter 2 reservas ATIVAS pra mesma obra** (índice único parcial)
- Deleção de pessoa em CASCADE pra leitor/funcionario (filhos da herança)
- Deleção de obra em CASCADE pra cópias

## Queries de teste (rodar no Query Tool depois do seed)

```sql
-- 1. Lista todos os emprestimos abertos com dados completos (demonstra heranca + agregacao)
SELECT p.nome AS leitor, p.matricula, o.titulo, c.codigo_tombo, e.data_emprestimo, e.status
  FROM emprestimo e
  JOIN leitor l  ON l.id = e.leitor_id
  JOIN pessoa p  ON p.id = l.id
  JOIN copia  c  ON c.id = e.copia_id
  JOIN obra   o  ON o.id = c.obra_id
 WHERE e.status = 'ABERTO';

-- 2. Quantas copias disponiveis por obra
SELECT o.titulo, COUNT(*) AS copias_disponiveis
  FROM obra o
  JOIN copia c ON c.obra_id = o.id
 WHERE c.estado = 'DISPONIVEL'
 GROUP BY o.titulo
 ORDER BY copias_disponiveis DESC;

-- 3. Reservas ativas com dados de leitor e obra
SELECT p.nome AS leitor, o.titulo, r.data_reserva, r.data_expiracao
  FROM reserva r
  JOIN leitor l ON l.id = r.leitor_id
  JOIN pessoa p ON p.id = l.id
  JOIN obra   o ON o.id = r.obra_id
 WHERE r.status = 'ATIVA';
```

## Credenciais de teste (vindas do seed)

| Tipo         | Nome             | CPF          | Matrícula | Senha     |
| ------------ | ---------------- | ------------ | --------- | --------- |
| Funcionário  | Caio Rosa        | 11111111111  | F001      | caio123   |
| Funcionário  | Vinicius Kenji   | 22222222222  | F002      | kenji123  |
| Leitor       | Ana Souza        | 33333333333  | L001      | —         |
| Leitor       | Bruno Lima       | 44444444444  | L002      | —         |
| Leitor       | Carla Mendes     | 55555555555  | L003      | —         |
| Leitor       | Daniel Oliveira  | 66666666666  | L004      | —         |

> Senhas estão em texto puro no seed pra simplificar testes iniciais. Quando a dupla implementar o login na UI Swing, ela pode trocar pra hash (bcrypt/SHA-256). O campo `senha` aceita até 255 chars.

## Opcional: usar o `psql` no terminal

O CLI não está no PATH por padrão. Se quiserem usar:

1. Adicionar `C:\Program Files\PostgreSQL\18\bin` ao PATH do Windows.
2. Reabrir o terminal.
3. `psql -U postgres -d biblioteca_poo -f db/01_schema.sql`

Mas o pgAdmin Query Tool resolve tudo, então isso é só conveniência.
