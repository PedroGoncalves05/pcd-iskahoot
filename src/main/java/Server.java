import GameState.GameState;
import GameState.Question;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static final int PORT = 12345;
    private static ConcurrentHashMap<String, GameState> jogosAtivos = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        List<Question> questions = JSonReader.getQuestions("perguntas.json");

        if (questions == null) {
            System.out.println("ERRO: Não foi possível ler perguntas.json");
            return;
        }

        GameState jogoTeste = new GameState("JOGO123", questions, 2);

        jogosAtivos.put("JOGO123", jogoTeste);

        System.out.println("Servidor iniciado na porta " + PORT);
        System.out.println("Jogo 'JOGO123' criado para equipas de 2 jogadores.");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                DealWithClient worker = new DealWithClient(clientSocket, jogosAtivos);
                new Thread(worker).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}