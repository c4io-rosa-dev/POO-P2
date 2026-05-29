package br.com.biblioteca.util;

import br.com.biblioteca.exception.BibliotecaException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class ConnectionFactory {


    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USUARIO = "postgres";
    private static final String SENHA = "root"; 

    private ConnectionFactory() {
        // classe utilitária: não instanciável
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (SQLException e) {
            throw new BibliotecaException(
                    "Não foi possível conectar ao banco de dados. "
                    + "Verifique se o PostgreSQL está rodando e se a senha em "
                    + "ConnectionFactory está correta.", e);
        }
    }
}
