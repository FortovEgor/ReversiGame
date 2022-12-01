public class Print extends Basic {
    /**
     * Функция печатает МАКС. количества белых и черных фишек, которые были в этой партии
     */
    public static void printMaxCounts() {
        System.out.println("MAX у Черных: " + maxBlackScores + " | MAX Белых: " + maxWhiteScores);
    }

    /**
     * функция выводит на экран все возможные следующие ходы игрока
     * @param boardAvailable - таблица, каждый элемент которой либо true (в клетку можно пойти),
     *                         либо false (в клетку нельзя пойти)
     */
    public static void printAllAvailableNextSteps(boolean[][] boardAvailable) {
        System.out.print(Color.TEXT_YELLOW + "Ваши возможные ходы: "+ Color.TEXT_RESET);
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                if (boardAvailable[i][j]) {
                    System.out.print("(" + (i+1) + ", " + (j+1) + "), ");
                }
            }
        }
    }
}
