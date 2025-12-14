import GameState.GameState;
import GameState.Question;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final int PORT = 12345;
    private static final ConcurrentHashMap<String, GameState> jogosAtivos = new ConcurrentHashMap<>();
    private static List<Question> todasPerguntas;

    public static void main(String[] args) {
        todasPerguntas = JSonReader.getQuestions("perguntas.json");
        if (todasPerguntas == null) {
            System.out.println("ERRO: 'perguntas.json' não encontrado ou inválido.");
            return;
        }

        System.out.println("=== SERVIDOR ISKAHOOT ===");
        System.out.println("A escutar na porta " + PORT);

        new Thread(Server::aceitarClientes).start();

        criarJogo("JOGO123", 2, 2, 5);
        System.out.println("(Jogo de teste 'JOGO123' criado automaticamente)");

        executarTUI();
    }

    private static void aceitarClientes() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                DealWithClient worker = new DealWithClient(clientSocket, jogosAtivos);
                new Thread(worker).start();
            }
        } catch (IOException e) {
            System.err.println("Erro no Socket do Servidor: " + e.getMessage());
        }
    }

    private static void executarTUI() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Comandos: new <nEquipas> <jog/equipa> <nPerguntas> | list | exit");

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
                            int nEquipas = Integer.parseInt(partes[1]);
                            int nJogadores = Integer.parseInt(partes[2]);
                            int nPerguntas = Integer.parseInt(partes[3]);

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
        List<Question> perguntasDoJogo = todasPerguntas;
        if (numPerguntas < todasPerguntas.size()) {
            perguntasDoJogo = todasPerguntas.subList(0, numPerguntas);
        }

        GameState novoJogo = new GameState(codigo, perguntasDoJogo, jogadoresPorEquipa);
        jogosAtivos.put(codigo, novoJogo);

        System.out.println("Novo jogo criado: " + codigo);
    }
}