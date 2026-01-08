package GameState;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class DealWithClient implements Runnable {
    private final Socket socket;
    private final ConcurrentHashMap<String, GameState> jogos;

    public DealWithClient(Socket socket, ConcurrentHashMap<String, GameState> jogos) {
        this.socket = socket;
        this.jogos = jogos;
    }

    @Override
    public void run() {

        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

        }
    }
}