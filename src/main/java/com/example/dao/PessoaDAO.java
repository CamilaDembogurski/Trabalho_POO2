package com.example.dao;

import com.example.connection.ConnectionFactory;
import com.example.model.Endereco;
import com.example.model.Pessoa;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PessoaDAO {
    private static final String SQL_INSERT = "INSERT INTO pessoa(nome, sobrenome, data_nascimento, cpf, endereco) VALUES(?, ?, ?, ?, ?)";
    private static final String SQL_SEARCH_ALL = "SELECT id, nome, sobrenome, data_nascimento, cpf, endereco FROM pessoa ORDER BY id";
    private static final String SQL_SEARCH_NOME = "SELECT id, nome, sobrenome, data_nascimento, cpf, endereco FROM pessoa WHERE nome LIKE ?";
    private static final String SQL_SEARCH_ID = "SELECT id, nome, sobrenome, data_nascimento, cpf, endereco FROM pessoa WHERE id = ?";
    private static final String SQL_SEARCH_AGE = "SELECT p.*, e.* FROM pessoa p LEFT JOIN endereco e ON p.endereco = e.id WHERE DATE_PART('year', AGE(p.data_nascimento)) = ?";
    private static final String SQL_UPDATE = "UPDATE pessoa set nome = ?, sobrenome = ?, data_nascimento = ?, cpf = ?, endereco = ? WHERE id = ?";
    private static final String SQL_DELETE = "DELETE FROM pessoa WHERE id = ?";

    public Pessoa insert(Pessoa pessoa){
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)
        ){
            stmt.setString(1, pessoa.getNome());
            stmt.setString(2, pessoa.getSobrenome());
            stmt.setDate(3, new java.sql.Date(pessoa.getDataNascimento().getTime()));
            stmt.setString(4, pessoa.getCpf());

            if (pessoa.getEndereco() == null || pessoa.getEndereco().getId() == null) {
                throw new RuntimeException("Pessoa deve ter um endereço válido.");
            }

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        pessoa.setId(rs.getInt(1));
                    }
                }
            }
            System.out.println("Pessoa inserida com sucesso: " + pessoa);
            return pessoa;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inserir pessoa: " + e.getMessage(), e);
        }
    }

    public Pessoa mapear(ResultSet rs) throws SQLException {
        Optional<Endereco> endereco = Optional.empty();
        int enderecoId = rs.getInt("endereco");

        if (!rs.wasNull()) {
            EnderecoDAO enderecoDAO = new EnderecoDAO();
            endereco = enderecoDAO.searchById(enderecoId);
        }

        return new Pessoa(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("sobrenome"),
                rs.getDate("data_nascimento"),
                rs.getString("cpf"),
                endereco.orElse(null)
        );
    }

    public List<Pessoa> searchAll(){
        List<Pessoa> lista = new ArrayList<Pessoa>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SEARCH_ALL, Statement.RETURN_GENERATED_KEYS);
             ResultSet rs = stmt.executeQuery()
        ){
            while (rs.next()){
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pessoas: "+e.getMessage(), e);
        }
        return lista;
    }

    public List<Pessoa> searchByNome(String nome) {
        List<Pessoa> lista = new ArrayList<Pessoa>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SEARCH_NOME)
        ){
            stmt.setString(1, "%" + nome + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar pessoas com nome: " + e.getMessage(), e);
        }
        return lista;
    }

    public Optional<Pessoa> searchById(int id){
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SEARCH_ID)
        ){
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    return Optional.of(mapear(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar pessoa com o id: "+e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Pessoa> searchByIdade(int idade) {
        List<Pessoa> pessoas = new ArrayList<Pessoa>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SEARCH_AGE)
        ) {
            stmt.setInt(1, idade);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pessoas.add(mapear(rs));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar pessoas pela idade: "+e.getMessage());
        }
        return pessoas;
    }

    public boolean update(Pessoa pessoa){
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)
        ){
            stmt.setString(1, pessoa.getNome());
            stmt.setString(2, pessoa.getSobrenome());
            stmt.setDate(3, new java.sql.Date(pessoa.getDataNascimento().getTime()));
            stmt.setString(4, pessoa.getCpf());

            if (pessoa.getEndereco() == null || pessoa.getEndereco().getId() == null) {
                throw new RuntimeException("Pessoa deve possuir endereço para atualização.");
            }

            stmt.setInt(5, pessoa.getEndereco().getId());

            stmt.setInt(6, pessoa.getId());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar pessoa: "+e.getMessage(), e);
        }
    }

    public boolean delete(int id){
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)
        ){
            stmt.setInt(1, id);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar a pessoa: "+e.getMessage(), e);
        }
    }

}
