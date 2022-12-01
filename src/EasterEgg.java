import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Пасхалочка (как же без неё!)
 */
public class EasterEgg {
    public static void printHistoryOfGame() throws IOException {
        String userDirectory = System.getProperty("user.dir");  // получаем текущую директорию
        List<String> history = Files.readAllLines(Paths.get(userDirectory + "\\src\\history.txt"));
        printStringsFromArray(history);
    }

    public static void printStringsFromArray(List<String> arr) {
        for (int i = 0; i < arr.size(); ++i) {
            System.out.println(Color.TEXT_CYAN + arr.get(i) + Color.TEXT_RESET);
        }
    }
}
