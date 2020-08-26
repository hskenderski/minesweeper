import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Minesweeper {
    private enum DifficultyLevel {
        BEGINNER(9, 10),
        INTERMEDIATE(16, 40),
        ADVANCED(24, 99);
        private final int size;
        private final int mines;

        DifficultyLevel(int size, int mines) {
            this.size = size;
            this.mines = mines;
        }

        public int getSize() {
            return size;
        }

        public int getMines() {
            return mines;
        }
    }

    private static final char MINE_SYMBOL = '*';
    private static final char UNREVEALED_SYMBOL = '-';

    private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        difficultyLevel = getDifficulty();
        startGame();
    }

    private static DifficultyLevel getDifficulty() {
        int difficulty = -1;
        final DifficultyLevel[] difficultyLevels = DifficultyLevel.values();
        while (difficulty < 0 || difficulty > 2)
            try {
                System.out.println("Enter the Difficulty Level");
                for (int i = 0; i < difficultyLevels.length; i++) {
                    final DifficultyLevel level = difficultyLevels[i];
                    System.out.println("Press " + i + " for " + level.name() +
                            " (" + level.getSize() + " * " + level.getSize() + " Cells and " +
                            level.getMines() + " Mines)");
                }

                difficulty = Integer.parseInt(READER.readLine());
            } catch (IOException ignored) {
            }
        return DifficultyLevel.values()[difficulty];
    }

    private static DifficultyLevel difficultyLevel;
    private static char[][] gameField;
    private static int freeFieldsLeft = 0;

    private static void startGame() {
        gameField = new char[difficultyLevel.getSize()][difficultyLevel.getSize()];
        for (int i = 0; i < difficultyLevel.getSize(); i++)
            for (int j = 0; j < difficultyLevel.getSize(); j++)
                gameField[i][j] = UNREVEALED_SYMBOL;

        freeFieldsLeft = difficultyLevel.getSize() * difficultyLevel.getSize() - difficultyLevel.getMines();

        while (enterMove() && freeFieldsLeft > 0) printField(false);

        printField(true);
        if (freeFieldsLeft == 0) System.out.println("You won!");
        else System.out.println("You lost!");
    }

    private static boolean minesGenerated = false;

    private static void generateMines(int rowClick, int colClick) {
        if (minesGenerated) return;
        minesGenerated = true;

        int minesSet = 0;
        final Random random = new Random();

        while (minesSet < difficultyLevel.getMines()) {
            final int row = random.nextInt(difficultyLevel.getSize());
            final int col = random.nextInt(difficultyLevel.getSize());
            if (gameField[row][col] == MINE_SYMBOL || (row == rowClick && col == colClick)) continue;
            minesSet++;
            gameField[row][col] = MINE_SYMBOL;
        }
    }

    private static boolean enterMove() {
        System.out.println("Enter your move, (row, column)\n-> ");
        final int[] input = readInput();
        generateMines(input[0], input[1]);
        return clickField(input[0], input[1]);
    }

    private static boolean clickField(int row, int col) {
        final char box = gameField[row][col];
        if (box == MINE_SYMBOL) return false;
        if (box != UNREVEALED_SYMBOL) return true;
        int minesAround = calculateMinesAround(row, col);

        gameField[row][col] = (char) ('0' + minesAround);
        freeFieldsLeft--;

        if (minesAround == 0)
            for (int i = -1; i <= 1; i++)
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) continue;
                    if (isInBounds(row + i, col + j)) clickField(row + i, col + j);
                }
        return true;
    }

    private static int calculateMinesAround(int row, int col) {
        int minesAround = 0;
        for (int i = -1; i <= 1; i++)
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                if (isInBounds(row +i, col + j) &&
                        gameField[row + i][col + j] == MINE_SYMBOL) minesAround++;
            }

        return minesAround;
    }

    private static boolean isInBounds(int row, int col) {
        return row >= 0 && row < difficultyLevel.getSize() && col >= 0 && col < difficultyLevel.getSize();
    }

    private static int[] readInput() {
        try {
            final int[] input = Arrays.stream(READER.readLine().split(" ")).mapToInt(Integer::parseInt).toArray();
            if (input.length == 2 && isInBounds(input[0], input[1])) return input;
        } catch (Exception e) {
        }
        System.out.println("Invalid input!");
        return readInput();
    }

    private static void printField(boolean revealMines) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Current Status of Board :\n    ");
        for (int i = 0; i < gameField.length; i++){
            sb.append(" ").append(i);
            if (i <= 9) sb.append(" ");
        }
        sb.append("\n");

        for (int row = 0; row < gameField.length; row++) {
            sb.append(row).append(" ");
            if (row <= 9) sb.append(" ");
            for (int col = 0; col < gameField.length; col++) {
                char symbol = gameField[row][col];
                if (!revealMines && symbol == MINE_SYMBOL) symbol = UNREVEALED_SYMBOL;
                sb.append("  ").append(symbol);
            }

            sb.append("\n");
        }
        System.out.println(sb.toString());
    }
}
