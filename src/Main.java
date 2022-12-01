import java.io.IOException;
import java.util.Scanner;

// главый класс игры
public class Main extends Board {
    public static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {  // игрок играет за черных, компьютер за белых
        // выбор режима игры
        System.out.println(Color.TEXT_GREEN + "Добро пожаловать в игру Реверси!" + Color.TEXT_RESET);
        int gameMode = Menu.menu(scanner);
        switch (gameMode) {
            case -1 -> EasterEgg.printHistoryOfGame();  // пасхалка
            case 1 -> playMode1(false);
            case 2 -> playMode2();
            default -> playMode3();
        }
    }

    /**
     * функция реализует режим игры с компьютером (легкий уровень)
     */
    private static void playMode1(boolean useAdvancedMode) {
        System.out.println(Color.TEXT_BLACK + "Вы играете черными, Ваш ход!" + Color.TEXT_RESET);
        fillInitialBoard(board);  // заполняем доску в начале игры

        /* глубокая копия массива - НАЧАЛО */
        String[][] boardCurrent = new String[SIZE][SIZE];
        // делаем глубокую копию массива
        for (int i = 0; i < SIZE; ++i) {
            System.arraycopy(board[i], 0, boardCurrent[i], 0, SIZE);
        }
        boardsList.add(boardCurrent);
        /* глубокая копия массива - КОНЕЦ */

        boolean[][] availableStepsTable;
        while (true) {  // условие останова это функция isFinish (в цикле ниже)
            if (isFinish(true, false)) {  // true, т.к. ходит пользователь
                System.out.println("Игра завершена!");
                break;
            }
            // показать пользователю доску с подсказками куда можно пойти
            availableStepsTable = showAllAvailableSteps("Ч", true);
            boolean isReturnToLastMove = userMove(availableStepsTable, true);  // ход пользователя
            if (isReturnToLastMove) {
                String[][] boardClone = new String[SIZE][SIZE];
                // делаем глубокую копию массива
                for (int i = 0; i < SIZE; ++i) {
                    System.arraycopy(boardsList.get(boardsList.size() - 1)[i], 0, boardClone[i], 0, SIZE);
                }
                board = boardClone;
                continue;
            }
            updateMaximumsInQuantity();  // обновляем максимумы

            if (isFinish(false, false)) {  // false , т к ходит компьютер
                System.out.println("Игра завершена!");
                break;
            }
            // шаг компьютера
            availableStepsTable = showAllAvailableSteps("Б", false);  // то, куда может пойти компьютер
            computerMove(availableStepsTable, useAdvancedMode);  // компьютер делает шаг
            updateMaximumsInQuantity();  // обновляем максимумы
            addCurrentBoardToBoardList();  // сохраняем текущее положение доски
        }
    }

    /**
     * функция реализует режим игры с компьютером (усложненный уровень)
     */
    private static void playMode2() {
        playMode1(true);
    }

    /**
     * функция реализует режим мультиплеера
     * в нем нельзя отменять ходы, так как в игре игрок 2 делает ход незамедлительно
     * после хода игрока 1, то есть в таком случае пришлось бы делать "откат" на два хода назад,
     * а на это может не согласиться другой игрок (с чего он вдруг должен заново ходить из-за
     * желания своего соперника переиграть свой последний ход) - ему это невыгодно и запрещено правилами!
     */
    private static void playMode3() {
        System.out.println(Color.TEXT_BLACK + "Режим мультиплеера активирован, делайте ходы!" + Color.TEXT_RESET);
        fillInitialBoard(board);  // заполняем доску в начале игры

        boolean[][] availableStepsTable;
        while (true) {  // условие останова это функция isFinish (в цикле ниже)
            if (isFinish(true, true)) {  // true, т.к. ходит пользователь
                System.out.println("Игра завершена!");
                break;
            }
            System.out.println(Color.TEXT_GREEN + "Ход ЧЕРНЫХ" + Color.TEXT_RESET);
            // показать пользователю доску с подсказками куда можно пойти
            availableStepsTable = showAllAvailableSteps("Ч", true);
            userMove(availableStepsTable, false);  // ход пользователя №1
            updateMaximumsInQuantity();  // обновляем максимумы

            System.out.println(Color.TEXT_GREEN + "Ход БЕЛЫХ" + Color.TEXT_RESET);
            // шаг 2-ого пользователя
            availableStepsTable = showAllAvailableSteps("Б", true);  // то куда может пойти компбютер
            userMove(availableStepsTable, false);  // ход пользователя №2
            updateMaximumsInQuantity();  // обновляем максимумы
            if (isFinish(false, true)) {  // false, т.к. ходит компьютер
                System.out.println("Игра завершена!");
                break;
            }
            addCurrentBoardToBoardList();  // сохраняем текущее положение доски
        }
    }

    /**
     * функция возвращает true при выполнении хотя бы одного из условий:
     * 1) доска заполнена
     * 2) на доске фишки только одного цвета
     * 3) ни один из игроков не может сделать ход
     * @param userStep - если true => сейчас ход пользователя, если false - компьютера
     * @param isMultiPlayer - true, если игра сейчас находится в режиме мультиплеера
     * @return - true, если игра завершена, false в противном случае
     */
    private static boolean isFinish(boolean userStep, boolean isMultiPlayer) {
        // Случай №1: доска заполнена
        int counter = 0;
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                if (board[i][j].equals("Ч") || board[i][j].equals("Б")) {
                    ++counter;
                }
            }
        }
        if (counter == SIZE * SIZE) {
            System.out.println("Доска заполнена.");
            printBoard(board);
            countWhiteBlackAndPrintResult(isMultiPlayer);  // выносим ВЕРДИКТ
            Print.printMaxCounts();
            return true;
        }

        // Случай №2: на доске фишки одного цвета
        int counterBlack = 0, counterWhite = 0;
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                if (board[i][j].equals("Ч")) {
                    ++counterBlack;
                } else if (board[i][j].equals("Б")) {
                    ++counterWhite;
                }
            }
        }
        if (counterBlack == 0 || counterWhite == 0) {
            System.out.println("На доске фишки одного цвета.");
            countWhiteBlackAndPrintResult(isMultiPlayer);  // выносим ВЕРДИКТ
            Print.printMaxCounts();
            return true;
        }
        // Случай №3: ни один из игроков не может сделать ход
        // проверка возможности пользовательского хода (Ч)
        boolean canUserMakeMove = true, canComputerMakeMove = true;
        boolean[][] availableSteps = showAllAvailableSteps("Ч", false);
        if (!haveAnyAvailableSteps(availableSteps)) {
            System.out.println("Пользователь не может пойти. Ход компьютера.");
            canUserMakeMove = false;
        }

        // проверка возможности хода компьютера (Б)
        availableSteps = showAllAvailableSteps("Б", false);
        if (!haveAnyAvailableSteps(availableSteps)) {
            System.out.println("Компьютер не может пойти. Ход пользователя.");
            canComputerMakeMove = false;
        }

        // Доп. проверки:
        if (!canUserMakeMove && !canComputerMakeMove) {  // ни пользователь, ни компьютер не может сделать ход
            System.out.println("НИКТО НЕ МОЖЕТ ПОЙТИ -> ЗАВЕРШЕНИЕ ИГРЫ");
            countWhiteBlackAndPrintResult(isMultiPlayer);  // выносим ВЕРДИКТ
            Print.printMaxCounts();
            return true;
        }

        if (userStep && canUserMakeMove) {  // ход пользователя и он может пойти
            return false;
        }

        if ((!userStep) && canComputerMakeMove) {  // ход компьютера и он может пойти
            return false;
        }

        // игра продолжается
        return false;
    }

    /**
     * функция определяет, может ли текущий игрок сделать ход
     * @param arr - массив, каждое поле которого содержит либо true (сюда можно поставить фишку),
     *             либо false (сюда нельзя поставить фишку)
     * @return - true, если возможен хотя бы один ход, false в противном случае
     */
    public static boolean haveAnyAvailableSteps(boolean[][] arr) {
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                if (arr[i][j]) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * функция определяет, является ли строка числом
     * @param str - строка
     * @return - true, если строка может быть приведена к числу
     */
    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    /**
     *
     * @param availableSteps - двумерный массив с элементами либо true (в эту клетку можно пойти),
     *                       либо false (в эту клетку нельзя пойти)
     * @param canRemakeThePrompts - true, если пользователь может отменять ходы (при игре с компьютером),
     *                            false - нельзя отменять ходы (режим мультиплеера)
     * @return - true, если была вызвана отмена последнего хода, false иначе
     */
    private static boolean userMove(boolean[][] availableSteps, boolean canRemakeThePrompts) {
        if (!haveAnyAvailableSteps(availableSteps)) {  // пользователь не может никак пойти
            System.out.println("У Вас нет возможных ходов! Ходит компьютер (след.игрок).");
            return false;
        }

        // Главная проверка, можем ли мы такой ход сделать
        int x, y;
        while (true) {
            System.out.println("\nФормат хода: X Y (БЕЗ запятой, только 2 цифры)");
            String input = scanner.next();
            if (input.equals("r") && canRemakeThePrompts) {  // пользователь хочет отменить свой ход
                cancelLastMove();
                return true;
            }
            while (!isNumeric(input)) {
                System.out.println("Введите число!");
                input = scanner.next();
            }
            x = Integer.parseInt(input);
            input = scanner.next();
            while (!isNumeric(input)) {
                System.out.println("Введите число!");
                input = scanner.next();
            }
            y = Integer.parseInt(input);
            // обновление доски
            if (x < 1 || x > 8 || y < 1 || y > 8) {
                System.out.println("Некорректная клетка! Попробуйте ещё раз!");
                continue;
            }
            if (availableSteps[x-1][y-1]) {  // все ок
                board[x-1][y-1] = "Ч";
                R(x-1, y-1, true, "Ч");  // перекрашиваем только 1 раз!
                break;
            } else {
                System.out.println("Вы не можете так пойти! Попытайтесь ещё раз!");
            }
        }
        return false;
    }

    /**
     * функция, которая делает ход компьютера
     * @param availableSteps - двумерный массив с элементами либо true (в эту клетку можно пойти),
     *                         либо false (в эту клетку нельзя пойти)
     * @param useAdvancedFormulae - true, если выбран режим №2 (продвинутый компьютер),
     *                            false, если выбран режим №1 (базовый уровень)
     */
    private static void computerMove(boolean[][] availableSteps, boolean useAdvancedFormulae) {
        if (!haveAnyAvailableSteps(availableSteps)) {  // компьютер не может пойти!
            System.out.println("Компьютер вынужденно пропускает ход. Ваш ход!");
            return;
        }
        // для каждой available клетки считаем R, выбираем макс и ходим туда
        int x = -1, y = -1;
        double maxR = -1000;  // такой нижней оценки точно хватит, т к
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                double tempR;
                tempR = R(i, j, false, "Б");
                if (useAdvancedFormulae) {  // считаем по продвинутой формуле
                    tempR = advancedR(tempR, i, j);
                }
                if (availableSteps[i][j] && tempR > maxR) {
                    x = i;
                    y = j;
                    maxR = tempR;
                }
            }
        }
        // идеальный ход - это ход в клетку с координатами (x, y) ([0, 7])
        // обновление доски
        board[x][y] = "Б";
        R(x, y, true, "Б");  // перекрашиваем только 1 раз!
        System.out.println("Ход противника - x: " + (x+1) + " y: " + (y+1));
        System.out.println("Ваш ход!");
    }

    /**
     * функция определяет, можно ли в данную клетку поставить фишку
     * @param i - 1-ая координата рассматриваемой клетки
     * @param j - 2-ая координата рассматриваемой клетки
     * @return - функция возвращает true, если позиция (i, j) валидна и свободна
     */
    public static boolean isCellFree(int i, int j) {
        return i < SIZE && i >= 0 && j < SIZE && j >= 0 && board[i][j].equals("·");
    }

    /**
     * функция определяет ценность замыкаемой клетки
     * @param i - 1-ая координата рассматриваемой клетки
     * @param j - 2-ая координата рассматриваемой клетки
     * @return - ценность клетки
     */
    public static int cellWeight(int i, int j) {
        if (i == SIZE - 1 || j == SIZE - 1) {  // кромочная клетка
            return 2;
        }
        return 1;
    }

    /**
     * функция вычисляет ценность клетки, на которую совершается ход
     * @param i - 1-ая координата рассматриваемой клетки
     * @param j - 2-ая координата рассматриваемой клетки
     * @return - ценность клетки
     */
    public static double cellWeightSS(int i, int j) {
        if (i == SIZE - 1 && j == SIZE - 1) {  // кромочная клетка
            return 0.8;
        } else if (i == SIZE - 1 || j == SIZE - 1) {
            return 0.4;
        }
        return 0;
    }

    // KEY POINT: x, y принадлежат [0, 7] !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    /**
     * функция считает значение функции R по базовой формуле
     * R(x, y) считает вес, который несет постановка фишки в клетку (x, y)
     * @param x - 1-ая координата рассматриваемой клетки  ||| x, y принадлежат [0, 7] !!!!!!!!!
     * @param y - 2-ая координата рассматриваемой клетки  ||| x, y принадлежат [0, 7] !!!!!!!!!
     * @param needToRepaint - true, если нам надо перекрасить клетку
     * @param color - цвет фишки, для которой вычисляется значение (Ч для режима игры с компьютером)
     * если color = "Ч", то это ход игрока №1 (черные фишки)
     * если color = "Б", то это ход игрока №2 (белые фишки)
     * @return - значение функции R (базовая формула)
     */
    private static double R(int x, int y, boolean needToRepaint, String color) {
        // R(x, y) считает вес, который несет постановка фишки в клетку (x, y)
        double Si = 0;  // суммирование ценностей замыкаемых клеток
        String color_2;
        if (color.equals("Ч")) {
            color_2 = "Б";
        } else {
            color_2 = "Ч";
        }
        // ищем замыкания в верхней вертикали нашей клетки
        boolean metBlack = false;
        {  // замыкания выше клетки (вертикаль.)
            for (int i = 0; i < x; ++i) {
                if (!metBlack && board[i][y].equals(color)) {
                    metBlack = true;
                } else if (metBlack && board[i][y].equals(color_2)) {
                    // ЗАМЫКАНИЕ
                    if (needToRepaint) {
                        board[i][y] = color;
                    }
                    Si += cellWeight(i, y);
                }

            }
            metBlack = false;
        }

        {  // замыкания ниже клетки (вертикаль.)
            for (int i = SIZE - 1; i > x; --i) {
                if (!metBlack && board[i][y].equals(color)) {
                    metBlack = true;
                } else if (metBlack && board[i][y].equals(color_2)) {
                    // ЗАМЫКАНИЕ
                    if (needToRepaint) {
                        board[i][y] = color;
                    }
                    Si += cellWeight(i, y);
                }
            }
            metBlack = false;
        }

        {  // замыкания левее клетки (горизонт.)
            for (int j = 0; j < y; ++j) {
                if (!metBlack && board[x][j].equals(color)) {
                    metBlack = true;
                } else if (metBlack && board[x][j].equals(color_2)) {
                    // ЗАМЫКАНИЕ
                    if (needToRepaint) {
                        board[x][j] = color;
                    }
                    Si += cellWeight(x, j);
                }

            }
            metBlack = false;
        }

        {  // замыкания правее клетки (горизонт.)
            for (int j = SIZE - 1; j > y; --j) {
                if (!metBlack && board[x][j].equals(color)) {
                    metBlack = true;
                } else if (metBlack && board[x][j].equals(color_2)) {
                    // ЗАМЫКАНИЕ
                    if (needToRepaint) {
                        board[x][j] = color;
                    }
                    Si += cellWeight(x, j);
                }

            }
            metBlack = false;
        }

        {  // замыкание справа по диагонали выше
            // найдем самую верхнюю правую диагональную клетку
            int offset = 0;
            while ((x-offset) < SIZE && (x-offset) >= 0 && (y-offset) < SIZE  && (y-offset) >= 0) {
                ++offset;
            }
            --offset;
            int xBorder = x - offset, yBorder = y + offset;

            for (int i = xBorder, j = yBorder; i < x && j > y && j < SIZE; ++i, --j) {
                if (!metBlack && board[i][j].equals(color)) {
                    metBlack = true;
                } else if (metBlack && board[i][j].equals(color_2)) {
                    // ЗАМЫКАНИЕ
                    if (needToRepaint) {
                        board[i][j] = color;
                    }
                    Si += cellWeight(i, j);
                }
            }
        }

        {  // замыкание слева по диагонали ниже
            // найдем самую верхнюю левую диагональную клетку
            int offset = 0;
            while ((x-offset) < SIZE && (x-offset) >= 0 && (y-offset) < SIZE  && (y-offset) >= 0) {
                ++offset;
            }
            --offset;
            int xBorder = x - offset, yBorder = y - offset;
            for (int i = xBorder, j = yBorder; i < x && j < y; ++i, ++j) {
                if (!metBlack && board[i][j].equals(color)) {
                    metBlack = true;
                } else if (metBlack && board[i][j].equals(color_2)) {
                    // ЗАМЫКАНИЕ
                    if (needToRepaint) {
                        board[i][j] = color;
                    }
                    Si += cellWeight(i, j);
                }
            }
        }

        {  // замыкание слева по диагонали ниже
            // найдем самую нижнюю левую диагональную клетку
            int offset = 0;
            while ((x + offset) < SIZE && (x + offset) >= 0 && (y-offset) < SIZE  && (y-offset) >= 0) {
                ++offset;
            }
            --offset;
            int xBorder = x + offset, yBorder = y - offset;
            for (int i = xBorder, j = yBorder; i > x && j < y; --i, ++j) {
                if (!metBlack && board[i][j].equals(color)) {
                    metBlack = true;
                } else if (metBlack && board[i][j].equals(color_2)) {
                    // ЗАМЫКАНИЕ
                    if (needToRepaint) {
                        board[i][j] = color;
                    }
                    Si += cellWeight(i, j);
                }
            }
        }

        {  // замыкание справа по диагонали ниже
            // найдем самую нижнюю правую диагональную клетку
            int offset = 0;
            while ((x + offset) < SIZE && (x + offset) >= 0 && (y+offset) < SIZE  && (y+offset) >= 0) {
                ++offset;
            }
            --offset;
            int xBorder = x + offset, yBorder = y + offset;
            for (int i = xBorder, j = yBorder; i > x && j > y; --i, --j) {
                if (!metBlack && board[i][j].equals(color)) {
                    metBlack = true;
                } else if (metBlack && board[i][j].equals(color_2)) {
                    // ЗАМЫКАНИЕ
                    if (needToRepaint) {
                        board[i][j] = color;
                    }
                    Si += cellWeight(i, j);
                }
            }
        }

        Si += cellWeightSS(x, y);
        return Si;
    }

    /**
     * функция вычисляет значение R по продвинутой формуле (анализируя возможные ходы противника - человека)
     * @param x - 1-ая координата рассматриваемой клетки
     * @param y - 2-ая координата рассматриваемой клетки
     * @return - значение R(x, y) по продвинутой формуле (анализируя возможные ходы противника - человека)
     */
    private static double advancedR(double resultR, int x, int y) {
        // рассчитаем все возможные значения R, которые противник может получить
        // при своем ходе после нашего хода в клетку(x, y)
        // и возьмем из них максимум
        double maxR = -1000;
        String tempChange = board[x][y];
        board[x][y] = "Б";  // покрасим текущую клетку в белую (типо компьютер уже сходил туда)А
        boolean[][] availableStepsTable = showAllAvailableSteps("Ч", false);  // смотрим куда может пойти игрок  // TODO: change false to true back!!!!!!
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                if (availableStepsTable[i][j]) {
                    double tempR = R(i, j, false, "Ч");  // ход игрока
                    if (tempR > maxR) {
                        maxR = tempR;  // обновляем максимум
                    }
                }
            }
        }
        board[x][y] = tempChange;  // перекрашиваем обратно
        return resultR - maxR;
    }

    /**
     * Функция выводит массив из элементов (x, y),
     * где (x, y) - это клетка, в которую можно сделать ход
     * @param color - color = "Ч" => showAllAvailableSteps for player;
     *                color = "Б" => showAllAvailableSteps for Computer
     * @param needToPrint - если true, то печатаем таблицу, показывающую все возможные ходы
     * @return - массив, каждый элемент которого - либо true (если в эту клетку можно сходить),
     *           либо false (в эту клетку сходить нельзя)
     */
    public static boolean[][] showAllAvailableSteps(String color, boolean needToPrint) {
        String color_2;
        if (color.equals("Ч")) {
            color_2 = "Б";
        } else {
            color_2 = "Ч";
        }

        // если в массиве стоит true, то в эту клетку можно ходить
        // если false, нельзя
        boolean[][] boardAvailable = new boolean[SIZE][SIZE];
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                boardAvailable[i][j] = false;
            }
        }

        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                if (board[i][j].equals(color_2)) {  // проверяем соседние 8 клеток
                    {  // нижние 3 клетки
                        if (isCellFree(i - 1, j - 1) && !boardAvailable[i - 1][j - 1]) {
                            boardAvailable[i-1][j-1] = true;
                        }
                        if (isCellFree(i, j - 1) && !boardAvailable[i][j - 1]) {
                            boardAvailable[i][j-1] = true;
                        }
                        if (isCellFree(i + 1, j - 1) && !boardAvailable[i + 1][j - 1]) {
                            boardAvailable[i+1][j-1] = true;
                        }
                    }
                    {  // средние 2 клетки
                        if (isCellFree(i - 1, j) && !boardAvailable[i - 1][j]) {
                            boardAvailable[i-1][j] = true;
                        }
                        if (isCellFree(i + 1, j) && !boardAvailable[i + 1][j]) {
                            boardAvailable[i+1][j] = true;
                        }
                    }
                    {  // верхние две клетки
                        if (isCellFree(i - 1, j+1) && !boardAvailable[i - 1][j+1]) {
                            boardAvailable[i-1][j+1] = true;
                        }
                        if (isCellFree(i, j+1) && !boardAvailable[i][j+1]) {
                            boardAvailable[i][j+1] = true;
                        }
                        if (isCellFree(i+1, j+1) && !boardAvailable[i+1][j+1]) {
                            boardAvailable[i+1][j+1] = true;
                        }
                    }

                }
            }
        }

        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                int myInt = boardAvailable[i][j] ? 1 : 0;
                if (R(i, j, false, color) >= 1 && myInt == 1) {
                    myInt = 1;
                } else {
                    myInt = 0;
                }
                boardAvailable[i][j] = myInt == 1;
            }
        }

        // выводим таблицу с возможными ходами игрока и их список ТОЛЬКО для пользователя
        if (needToPrint) {
            printBoardWithAllAvailableSteps(board, boardAvailable);
            Print.printAllAvailableNextSteps(boardAvailable);
        }
        return boardAvailable;
    }

    /**
     * Функция обновляет число макс. набранных очков для белых и черных на текущем шаге
     */
    private static void updateMaximumsInQuantity() {
        int whiteScoresCurrent = 0;
        int blackScoresCurrent = 0;
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                if (board[i][j].equals("Б")) {
                    ++whiteScoresCurrent;
                } else if (board[i][j].equals("Ч")) {
                    ++blackScoresCurrent;
                }
            }
        }
        if (maxWhiteScores < whiteScoresCurrent) {  // обновляем максимум белых фишек
            maxWhiteScores = whiteScoresCurrent;
        }
        if (maxBlackScores < blackScoresCurrent) {  // обновляем максимум черных фишек
            maxBlackScores = blackScoresCurrent;
        }
    }

    /**
     * функция подсчитывает число белых и черных фишек и выводит вердикт
     * @param isMultiPlayer - true, если игра идет в режиме мультиплеера, false иначе
     */
    public static void countWhiteBlackAndPrintResult(boolean isMultiPlayer) {
        int black = 0, white = 0;
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < SIZE; ++j) {
                if (board[i][j].equals("Б")) {
                    ++white;
                } else if (board[i][j].equals("Ч")) {
                    ++black;
                }
            }
        }
        System.out.println("Черные фишки: " + black + "| Белые фишки: " + white);
        if (black > white) {
            System.out.println(isMultiPlayer ? "Победили черные!" : "Ура! Вы победили!");
        } else if (black == white) {
            System.out.println("Ничья!");
        } else {
            System.out.println(isMultiPlayer ? "Победили белые!" : "Увы! Вы проиграли!");
        }
    }
}