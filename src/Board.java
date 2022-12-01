import java.util.ArrayList;

public class Board extends Basic {
    public static String[][] board = new String[SIZE][SIZE];

    // здесь будем хранить состояния доски (нужно для отмены хода)
    public static ArrayList<String[][]> boardsList = new ArrayList<>();

    public static void addCurrentBoardToBoardList() {
        String[][] board_current_1 = new String[SIZE][SIZE];
        // делаем глубокую копию массива
        for (int i = 0; i < SIZE; ++i) {
            System.arraycopy(board[i], 0, board_current_1[i], 0, SIZE);
        }
        boardsList.add(board_current_1);
    }

    /**
     * функция отменяет последний ход пользователя, который ее вызвал
     */
    public static void cancelLastMove() {
        int lastIndex = boardsList.size() - 1;
        if (lastIndex <= 0) {
            System.out.println("Вы не можете отменить нулевой ход!");
            return;
        }
        System.out.println("Ваш последний ход отменён. Ваш ход!");

        boardsList.remove(lastIndex);
        --lastIndex;
        board = boardsList.get(lastIndex);
    }

    public static void fillInitialBoard(String[][] board) {
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                if (i == 3 && j == 3 || i == 4 && j == 4) {
                    board[i][j] = "Б";  // белая фишка
                } else if (i == 3 && j == 4 || i == 4 && j == 3) {
                    board[i][j] = "Ч";  // черная фишка
                } else {  // пустое поле
                    board[i][j] = "·";
                }
            }
        }
    }

    /**
     отображение доски в консоли с данным местоположением фигур (массив board)
     */
    public static void printBoard(String[][] board) {
        System.out.println(Color.TEXT_CYAN + " \\" + Color.TEXT_PURPLE + "  ̲1  ̲2  ̲3  ̲4  ̲5  ̲6  ̲7  ̲8 " + Color.TEXT_CYAN + "Y" + Color.TEXT_RESET);
        for (int i = 0; i < SIZE; ++i) {
            System.out.print(Color.TEXT_PURPLE + (i+1) + "| " + Color.TEXT_RESET);
            for (int j = 0; j < SIZE; ++j) {
                if (board[i][j].equals("Б")) {
                    System.out.print(" " + Color.TEXT_WHITE + board[i][j] + Color.TEXT_RESET + " ");
                } else if (board[i][j].equals("Ч")) {
                    System.out.print(" " + Color.TEXT_BLACK + board[i][j] + Color.TEXT_RESET + " ");
                } else {
                    System.out.print(" " + board[i][j] + " ");
                }
            }
            System.out.println();
        }
        System.out.println(Color.TEXT_CYAN + "X" + Color.TEXT_RESET);
    }

    /**
     функция выводит на экран доску с подсказками по возможным ходам
     */
    public static void printBoardWithAllAvailableSteps(String[][] board, boolean[][] boardAvailable) {
        System.out.println(Color.TEXT_CYAN + " \\" + Color.TEXT_PURPLE + "  ̲1  ̲2  ̲3  ̲4  ̲5  ̲6  ̲7  ̲8 " + Color.TEXT_CYAN + "Y" + Color.TEXT_RESET);
        for (int i = 0; i < SIZE; ++i) {
            System.out.print(Color.TEXT_PURPLE + (i+1) + "| " + Color.TEXT_RESET);
            for (int j = 0; j < SIZE; ++j) {
                if (boardAvailable[i][j]) {
                    System.out.print(" " + Color.TEXT_YELLOW + "*" + Color.TEXT_RESET + " ");
                    continue;
                }
                if (board[i][j].equals("Б")) {
                    System.out.print(" " + Color.TEXT_WHITE + board[i][j] + Color.TEXT_RESET + " ");
                } else if (board[i][j].equals("Ч")) {
                    System.out.print(" " + Color.TEXT_BLACK + board[i][j] + Color.TEXT_RESET + " ");
                } else {
                    System.out.print(" " + board[i][j] + " ");
                }
            }
            System.out.println();
        }
        System.out.println(Color.TEXT_CYAN + "X" + Color.TEXT_RESET);
    }
}
