import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class Minesweeper {
    private class MineTile extends JButton {
        int r;
        int c;

        public MineTile(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    int tileSize = 70;
    int numRows = 8;
    int numCols = numRows;
    int boardWidth = numCols * tileSize;
    int boardHeight = numRows * tileSize;

    JFrame frame = new JFrame("Minesweeper");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    int mineCount=10;
    MineTile[][] board = new MineTile[numRows][numCols];
    ArrayList<MineTile> mineList;
    Random random = new Random();

    int tilesClicked = 0; // goal is to click all tiles except the ones containing mines
    boolean gameOver = false;
    int score = 0;

    private Timer timer;
    private int elapsedTime = 0;
    private JLabel timeLabel = new JLabel();

    Minesweeper() {
        // frame.setVisible(true);
        int numRows = getDimension("Enter the number of rows:");
        int numCols = getDimension("Enter the number of columns:");

        this.numRows = numRows;
        this.numCols = numCols;
        this.boardWidth = numCols * tileSize;
        this.boardHeight = numRows * tileSize;

        

        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.BOLD, 25));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Minesweeper: " + Integer.toString(mineCount));
        textLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 25));
        timeLabel.setHorizontalAlignment(JLabel.CENTER);
        timeLabel.setText("Time: 0");
        timeLabel.setOpaque(true);

        textPanel.add(timeLabel, BorderLayout.SOUTH);

        

        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(numRows, numCols)); // 8x8
        // boardPanel.setBackground(Color.green);
        frame.add(boardPanel);
        board = new MineTile[numRows][numCols];

        for (int r = 0; r < numRows; r++) {
            for (int c = 0; c < numCols; c++) {
                MineTile tile = new MineTile(r, c);
                board[r][c] = tile;

                tile.setFocusable(false);
                tile.setMargin(new Insets(0, 0, 0, 0));
                tile.setFont(new Font("Arial Unicode MS", Font.PLAIN, 45));
                // tile.setText("💣");
                tile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (gameOver) {
                            return;
                        }
                        MineTile tile = (MineTile) e.getSource();

                        // left click
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (tile.getText() == "") {
                                if (mineList.contains(tile)) {
                                    revealMines();
                                } else {
                                    checkMine(tile.r, tile.c);
                                }
                            }
                        }
                        // right click
                        else if (e.getButton() == MouseEvent.BUTTON3) {
                            if (tile.getText() == "" && tile.isEnabled()) {
                                tile.setText("🚩");
                                checkWinCondition();
                            } else if (tile.getText() == "🚩") {
                                tile.setText("");
                                checkWinCondition();
                            }
                        }
                    }
                });

                boardPanel.add(tile);

            }
        }

        frame.setVisible(true);

        setMines();
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsedTime++;
                timeLabel.setText("Time: " + elapsedTime);
            }
        });
        timer.start();

       
    }
    

    int getDimension(String message) {
        String input = JOptionPane.showInputDialog(null, message, "Enter Dimension", JOptionPane.QUESTION_MESSAGE);
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            // Handle invalid input (non-integer or cancel)
            JOptionPane.showMessageDialog(null, "Invalid input. Using default dimension.", "Error", JOptionPane.ERROR_MESSAGE);
            return 8; // Default dimension
        }
    }

    void setMines() {
        mineList = new ArrayList<MineTile>();

        int mineLeft = mineCount;
        while (mineLeft > 0) {
            int r = random.nextInt(numRows);
            int c = random.nextInt(numCols);

            MineTile tile = board[r][c];
            if (!mineList.contains(tile)) {
                mineList.add(tile);
                mineLeft -= 1;
            }
        }
    }

    void revealMines() {
        for (int i = 0; i < mineList.size(); i++) {
            MineTile tile = mineList.get(i);
            tile.setText("💣");
        }

        gameOver = true;
        timer.stop();
        textLabel.setText("Game Over! Your Score: " + score);
    }

    void checkMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return;
        }

        MineTile tile = board[r][c];
        if (!tile.isEnabled()) {
            return;
        }
        tile.setEnabled(false);
        tilesClicked += 1;

        int minesFound = 0;

        // top 3
        minesFound += countMine(r - 1, c - 1); // top left
        minesFound += countMine(r - 1, c); // top
        minesFound += countMine(r - 1, c + 1); // top right

        // left and right
        minesFound += countMine(r, c - 1); // left
        minesFound += countMine(r, c + 1); // right

        // bottom 3
        minesFound += countMine(r + 1, c - 1); // bottom left
        minesFound += countMine(r + 1, c); // bottom
        minesFound += countMine(r + 1, c + 1); // bottom right

        if (minesFound > 0) {
            tile.setText(Integer.toString(minesFound));
        } else {
            tile.setText("");

            // top 3
            checkMine(r - 1, c - 1); // top left
            checkMine(r - 1, c); // top
            checkMine(r - 1, c + 1); // top right

            // left and right
            checkMine(r, c - 1); // left
            checkMine(r, c + 1); // right

            // bottom 3
            checkMine(r + 1, c - 1); // bottom left
            checkMine(r + 1, c); // bottom
            checkMine(r + 1, c + 1); // bottom right
        }

        if (tilesClicked == numRows * numCols - mineList.size()) {
            gameOver = true;
            score += 10; // You get a bonus for clearing the entire board without hitting a mine
            timer.stop();
            textLabel.setText("Mines Cleared! Your Score: " + score);
        }
    }

    int countMine(int r, int c) {
        if (r < 0 || r >= numRows || c < 0 || c >= numCols) {
            return 0;
        }
        if (mineList.contains(board[r][c])) {
            return 1;
        }
        return 0;
    }

    void checkWinCondition() {
        int flaggedMines = 0;
        for (MineTile mine : mineList) {
            if (mine.getText() == "🚩") {
                flaggedMines++;
            }
        }

        if (flaggedMines == mineCount) {
            gameOver = true;
            score += 10; // You get a bonus for correctly flagging all mines
            timer.stop();
            textLabel.setText("You Win! Your Score: " + score);
        }
    }

    public static void main(String[] args) {
        new Minesweeper();
    }
}



