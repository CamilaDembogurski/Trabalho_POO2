package com.example.teste;

import com.example.dao.EnderecoDAO;
import com.example.dao.PessoaDAO;
import com.example.model.Endereco;
import com.example.model.Pessoa;

import java.text.SimpleDateFormat;
import java.util.*;

public class Principal {

    public static void main(String[] args) {
        menu();
    }

    public static void menu() {
        Scanner sc = new Scanner(System.in);

        EnderecoDAO enderecoDAO = new EnderecoDAO();
        PessoaDAO pessoaDAO = new PessoaDAO();

        int opcao = -1;

        while (opcao != 0) {
            System.out.println("\n===== MENU =====");
            System.out.println("1 - Inserir endereço");
            System.out.println("2 - Editar endereço");
            System.out.println("3 - Deletar endereço");
            System.out.println("4 - Listar todos os endereços");
            System.out.println("5 - Listar endereços por id");
            System.out.println("6 - Listar endereços pelo logradouro");
            System.out.println("7 - Listar endereços pelo estado");
            System.out.println("8 - Listar endereços pela cidade\n");
            System.out.println("9 - Inserir pessoa");
            System.out.println("10 - Editar pessoa");
            System.out.println("11 - Deletar pessoa");
            System.out.println("12 - Listar todas as pessoas");
            System.out.println("13 - Listar pessoa por id");
            System.out.println("14 - Listar pessoas pelo nome");
            System.out.println("15 - Listar pessoas pela idade");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            opcao = lerInt(sc);
            switch (opcao) {
                case 1: { // inserir endereço
                    Endereco e = new Endereco(
                            lerTexto(sc, "Logradouro: "),
                            lerIntPositivo(sc, "Número: "),
                            lerTexto(sc, "Cidade: "),
                            lerTexto(sc, "Estado: "),
                            lerTexto(sc, "País: ")
                    );
                    enderecoDAO.insert(e);
                    break;
                }
                case 2: { // editar endereço
                    int id = lerIntPositivo(sc, "ID: ");

                    Endereco e = new Endereco();
                    e.setId(id);

                    e.setLogradouro(lerTexto(sc, "Logradouro: "));
                    e.setNumero(lerIntPositivo(sc, "Número: "));
                    e.setCidade(lerTexto(sc, "Cidade: "));
                    e.setEstado(lerTexto(sc, "Estado: "));
                    e.setPais(lerTexto(sc, "País: "));

                    System.out.println(enderecoDAO.update(e) ? "Atualizado!" : "Não encontrado!");
                    break;
                }

                case 3: { // deletar endereço
                    int id = lerIntPositivo(sc, "ID: ");
                    System.out.println(enderecoDAO.delete(id) ? "Deletado!" : "Não encontrado!");
                    break;
                }

                case 4: { // listar endereços
                    enderecoDAO.searchAll().forEach(System.out::println);
                    break;
                }

                case 5: { // listar endereço pelo id
                    int id = lerIntPositivo(sc, "ID: ");
                    enderecoDAO.searchById(id)
                            .ifPresentOrElse(
                                    System.out::println,
                                    () -> System.out.println("Não encontrado")
                            );
                    break;
                }

                case 6: { // listar endereço pelo logradouro
                    String log = lerTexto(sc, "Logradouro: ");
                    enderecoDAO.searchByLogradouro(log).forEach(System.out::println);
                    break;
                }

                case 7: { // listar endereço pelo estado
                    String est = lerTexto(sc, "Estado: ");
                    enderecoDAO.searchByEstado(est).forEach(System.out::println);
                    break;
                }

                case 8: { // listar endereço pela cidade
                    String cid = lerTexto(sc, "Cidade: ");
                    enderecoDAO.searchByCidade(cid).forEach(System.out::println);
                    break;
                }

                case 9: { // inserir pessoa
                    Pessoa p = new Pessoa();

                    p.setNome(lerTexto(sc, "Nome: "));
                    p.setSobrenome(lerTexto(sc, "Sobrenome: "));
                    p.setCpf(lerTexto(sc, "CPF: "));
                    p.setDataNascimento(lerData(sc));

                    int idEndereco = lerIntPositivo(sc, "ID endereço: ");
                    Endereco e = enderecoDAO.buscarEndereco(idEndereco);

                    if (e != null) {
                        p.setEndereco(e);
                    } else {
                        System.out.println("Endereço não encontrado!");
                    }

                    pessoaDAO.insert(p);
                    break;
                }

                case 10: { // editar pessoa
                    int id = lerIntPositivo(sc, "ID pessoa: ");

                    Pessoa p = new Pessoa();
                    p.setId(id);

                    p.setNome(lerTexto(sc, "Nome: "));
                    p.setSobrenome(lerTexto(sc, "Sobrenome: "));
                    p.setCpf(lerTexto(sc, "CPF: "));
                    p.setDataNascimento(lerData(sc));

                    int idEndereco = lerIntPositivo(sc, "ID endereço: ");
                    Endereco e = enderecoDAO.buscarEndereco(idEndereco);

                    if (e != null) {
                        p.setEndereco(e);
                    } else {
                        System.out.println("Endereço não encontrado!");
                    }

                    System.out.println(pessoaDAO.update(p) ? "Atualizado!" : "Não encontrado!");
                    break;
                }

                case 11: { // deletar pessoa
                    int id = lerIntPositivo(sc, "ID pessoa: ");
                    System.out.println(pessoaDAO.delete(id) ? "Deletado!" : "Não encontrado!");
                    break;
                }

                case 12: { // listar todas as pessoas
                    pessoaDAO.searchAll().forEach(System.out::println);
                    break;
                }

                case 13: { // listar pessoa pelo id
                    int id = lerIntPositivo(sc, "ID pessoa: ");
                    pessoaDAO.searchById(id)
                            .ifPresentOrElse(
                                    System.out::println,
                                    () -> System.out.println("Não encontrado")
                            );
                    break;
                }

                case 14: { // listar pessoa pelo nome
                    String nome = lerTexto(sc, "Nome: ");
                    pessoaDAO.searchByNome(nome).forEach(System.out::println);
                    break;
                }

                case 15: { // listar pessoa pelo idade
                    int idade = lerIntPositivo(sc, "Idade: ");
                    pessoaDAO.searchByIdade(idade).forEach(System.out::println);
                    break;
                }

                case 0:
                    System.out.println("Saindo...");
                    break;

                default:
                    System.out.println("Opção inválida!");
            }
        }

        sc.close();
    }

    public static String lerTexto(Scanner sc, String msg) {
        while (true) {
            System.out.print(msg);
            String valor = sc.nextLine();
            if (!valor.trim().isEmpty()) return valor;
            System.out.println("Obrigatório!");
        }
    }

    public static int lerInt(Scanner sc) {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.print("Número inválido: ");
            }
        }
    }

    public static int lerIntPositivo(Scanner sc, String msg) {
        while (true) {
            System.out.print(msg);
            try {
                int valor = Integer.parseInt(sc.nextLine());
                if (valor > 0) return valor;
                System.out.println("Tem que ser > 0");
            } catch (Exception e) {
                System.out.println("Inválido");
            }
        }
    }

    public static Date lerData(Scanner sc) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        while (true) {
            try {
                System.out.print("Data (YYYY-MM-DD): ");
                return sdf.parse(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Data inválida!");
            }
        }
    }
}