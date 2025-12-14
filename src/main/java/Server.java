import GameState.GameState;
import GameState.Question;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final int PORT = 12345;
    // Mapa de jogos ativos: Código do Jogo -> Objeto GameState
    private static ConcurrentHashMap<String, GameState> jogosAtivos = new ConcurrentHashMap<>();
    private static List<Question> todasPerguntas;

    public static void main(String[] args) {
        // 1. Carregar perguntas do JSON
        todasPerguntas = JSonReader.getQuestions("perguntas.json");
        if (todasPerguntas == null) {
            System.out.println("ERRO: 'perguntas.json' não encontrado ou inválido.");
            return;
        }

        System.out.println("=== SERVIDOR ISKAHOOT ===");
        System.out.println("A escutar na porta " + PORT);

        // 2. Iniciar a Thread que aceita conexões de clientes
        // (Fazemos isto numa thread separada para o 'main' ficar livre para a TUI)
        new Thread(Server::aceitarClientes).start();

        // 3. Criar um jogo de teste automaticamente (para facilitar o teu debug)
        criarJogo("JOGO123", 2, 2, 5);
        System.out.println("(Jogo de teste 'JOGO123' criado automaticamente)");

        // 4. Iniciar a TUI (Interface de Texto) para comandos do administrador
        executarTUI();
    }

    private static void aceitarClientes() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // Bloqueia aqui à espera de um cliente...
                Socket clientSocket = serverSocket.accept();

                // Quando chega, lança uma thread 'DealWithClient' para tratar dele
                DealWithClient worker = new DealWithClient(clientSocket, jogosAtivos);
                new Thread(worker).start();
            }
        } catch (IOException e) {
            System.err.println("Erro no Socket do Servidor: " + e.getMessage());
        }
    }

    private static void executarTUI() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nComandos disponíveis:");
        System.out.println(" -> new <numEquipas> <jogadoresPorEquipa> <numPerguntas>");
        System.out.println(" -> list (ver jogos ativos)");
        System.out.println(" -> exit");

        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) break;

            String linha = scanner.nextLine();
            String[] partes = linha.split(" ");
            String comando = partes[0].toLowerCase();

            switch (comando) {
                case "new":
                    if (partes.length == 4) {
                        try {
                            int nEquipas = Integer.parseInt(partes[1]); // Não usado na lógica atual, mas pedido no enunciado
                            int nJogadores = Integer.parseInt(partes[2]);
                            int nPerguntas = Integer.parseInt(partes[3]);

                            // Gera um código aleatório simples (ex: JOGO + Hora)
                            String codigoJogo = "GAME" + (System.currentTimeMillis() % 1000);

                            criarJogo(codigoJogo, nJogadores, nEquipas, nPerguntas);
                        } catch (NumberFormatException e) {
                            System.out.println("Erro: Use números inteiros.");
                        }
                    } else {
                        System.out.println("Uso: new <nEquipas> <nJogadores> <nPerguntas>");
                    }
                    break;

                case "list":
                    System.out.println("--- Jogos Ativos ---");
                    if (jogosAtivos.isEmpty()) System.out.println("Nenhum jogo ativo.");
                    jogosAtivos.forEach((k, v) ->
                            System.out.println("Code: " + k + " | Jogadores: " + v.getNumJogadoresTotal())
                    );
                    break;

                case "exit":
                    System.out.println("A encerrar servidor...");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Comando desconhecido.");
            }
        }
    }

    private static void criarJogo(String codigo, int jogadoresPorEquipa, int numEquipas, int numPerguntas) {
        // Seleciona um subconjunto de perguntas (ou todas se pedirem muitas)
        List<Question> perguntasDoJogo = todasPerguntas;
        if (numPerguntas < todasPerguntas.size()) {
            perguntasDoJogo = todasPerguntas.subList(0, numPerguntas);
        }

        GameState novoJogo = new GameState(codigo, perguntasDoJogo, jogadoresPorEquipa);
        jogosAtivos.put(codigo, novoJogo);

        System.out.println("Novo jogo criado!");
        System.out.println(" Código: " + codigo);
        System.out.println(" Config: " + numEquipas + " equipas de " + jogadoresPorEquipa + " jogadores.");
    }
}