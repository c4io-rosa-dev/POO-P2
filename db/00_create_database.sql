-- cria banco postgres principal do projeto
-- conectar em 'biblioteca_poo'

CREATE DATABASE biblioteca_poo
    WITH ENCODING = 'UTF8'
         LC_COLLATE = 'Portuguese_Brazil.1252'
         LC_CTYPE   = 'Portuguese_Brazil.1252'
         TEMPLATE   = template0;

COMMENT ON DATABASE biblioteca_poo IS
    'Sistema de controle de biblioteca - avaliacao final POO P2';