import java.util.Scanner;

public class Menu {
    /**
     * функция печатает меню игры и принимает на вход режим игры
     * @return - функция возвращает номер выбранного режима игры
     */
    public static int menu(Scanner scanner) {
        while (true) {
            System.out.println("Выберите режим игры:");
            System.out.println("1 - игра с компьютером (легкий уровень)");
            System.out.println("2 - игра с компьютером (усложненный уровень)");
            System.out.println("3 - игра двух пользователей");
            System.out.println("Примечание. Чтобы процессе игры отменить ход, в поле для ввода напишите r.");
            System.out.println("Примечание к примечанию. Отменять ход можно ТОЛЬКО при режиме игры с компьютером!");
            int mode = Integer.parseInt(scanner.next());
            if (!(mode == -1 || mode == 1 || mode == 2 || mode == 3)) {
                System.out.println("Вы ввели несуществующий режим! Попробуйте снова!");
            } else {
                return mode;
            }
        }
    }
}
