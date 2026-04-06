package com.example.dao;

import com.example.connection.ConnectionFactory;
import com.example.model.Endereco;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnderecoDAO {
    private static final String SQL_INSERT = "INSERT INTO endereco(logradouro, numero, cidade, estado, pais) VALUES(?, ?, ?, ?, ?);";
    private static final String SQL_SEARCH_ALL = "SELECT id, logradouro, numero, cidade, estado, pais FROM endereco ORDER BY id;";
    private static final String SQL_SEARCH_LOGRADOURO = "SELECT id, logradouro, numero, cidade, estado, pais FROM endereco WHERE logradouro like ?;";
    private static final String SQL_SEARCH_ESTADO = "SELECT id, logradouro, numero, cidade, estado, pais FROM endereco WHERE estado like ?;";
    private static final String SQL_SEARCH_CIDADE = "SELECT id, logradouro, numero, cidade, estado, pais FROM endereco WHERE cidade like ?;";
    private static final String SQL_SEARCH_ID = "SELECT id, logradouro, numero, cidade, estado, pais FROM endereco WHERE id = ?;";
    private static final String SQL_UPDATE = "UPDATE endereco SET logradouro = ?, numero = ?, cidade = ?, estado = ?, pais = ? WHERE id = ?;";
    private static final String SQL_UPDATE_PESSOAS = "UPDATE pessoa SET endereco = NULL WHERE endereco = ?;";
    private static final String SQL_DELETE = "DELETE FROM endereco WHERE id = ?;";

    public Endereco insert(Endereco endereco){
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)
        ){
            stmt.setString(1, endereco.getLogradouro());
            stmt.setInt(2, endereco.getNumero());
            stmt.setString(3, endereco.getCidade());
            stmt.setString(4, endereco.getEstado());
            stmt.setString(5, endereco.getPais());

            int linhasAfetadas = stmt.executeUpdate();
            if (linhasAfetadas > 0){
                try (ResultSet rs = stmt.getGeneratedKeys()){
                    if (rs.next()){
                        endereco.setId(rs.getInt(1));
                    }
                }
            }
            System.out.println("Endereço inserido com sucesso: "+endereco);
            return endereco;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao inserir endereço: "+e.getMessage(), e);
        }
    }

    public Endereco mapear(ResultSet rs) throws SQLException{
        return new Endereco(
                rs.getInt("id"),
                rs.getString("logradouro"),
                rs.getInt("numero"),
                rs.getString("cidade"),
                rs.getString("estado"),
                rs.getString("pais")
        );
    }

    public List<Endereco> searchAll(){
        List<Endereco> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SQL_SEARCH_ALL, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.executeQuery()
        ){
            while (rs.next()){
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar endereços: "+e.getMessage(), e);
        }
        return lista;
    }

    public List<Endereco> searchByLogradouro(String logradouro) {
        List<Endereco> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SEARCH_LOGRADOURO)
        ){
            stmt.setString(1, "%" + logradouro + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar endereço com logradouro: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Endereco> searchByEstado(String estado) {
        List<Endereco> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SEARCH_ESTADO)
        ){
            stmt.setString(1, "%" + estado + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar endereço com estado: " + e.getMessage(), e);
        }
        return lista;
    }

    public List<Endereco> searchByCidade(String cidade) {
        List<Endereco> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_SEARCH_CIDADE)
        ){
            stmt.setString(1, "%" + cidade + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar endereço com cidade: " + e.getMessage(), e);
        }
        return lista;
    }

    public Optional<Endereco> searchById(int id){
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
            throw new RuntimeException("Erro ao buscar endereço com o id: "+e.getMessage(), e);
        }
        return Optional.empty();
    }

    public boolean update(Endereco endereco){
        try (Connection conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)
        ){
            stmt.setString(1, endereco.getLogradouro());
            stmt.setInt(2, endereco.getNumero());
            stmt.setString(3, endereco.getCidade());
            stmt.setString(4, endereco.getEstado());
            stmt.setString(5, endereco.getPais());
            stmt.setInt(6, endereco.getId());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar endereço: "+e.getMessage(), e);
        }
    }

    public boolean delete(Integer id) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            try (PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_PESSOAS)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao excluir endereço: " + e.getMessage());
            return false;
        }
    }

    public boolean existsById(int id){
        String SQL_VERIFICA = "SELECT 1 FROM endereco WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_VERIFICA)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()){
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar endereço"+ e.getMessage(), e);
        }
    }

    public Endereco buscarEndereco(int id) {
        return searchById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
    }

}
