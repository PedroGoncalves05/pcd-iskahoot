import com.google.gson.Gson;
import GameState.Question;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

public class JSonReader {

    public static List<Question> getQuestions(String fileName) {
        Gson gson = new Gson();
        Reader reader = null;
        try {
            reader = new FileReader(fileName);
            Perguntas quizFile = gson.fromJson(reader, Perguntas.class);
            List<Question> allQuestions = quizFile.getQuestions();
            return allQuestions;

        } catch (FileNotFoundException e) {
            System.err.println("Ficheiro de perguntas '" + fileName + "' não encontrado!");
            return null;
        }
    }
}
