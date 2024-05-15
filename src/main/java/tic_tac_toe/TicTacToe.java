package tic_tac_toe;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.TimerTask;

public class TicTacToe extends JFrame {
    private JButton[] gameButtons;
    private JLabel leftPlayerLabel, mainLabel, rightPlayerLabel;
    private final Listener listener;
    private String playerSymbol, botSymbol;

    public TicTacToe() {
        listener = new Listener();

        JPanel startGameButtonPanel = new JPanel();
        startGameButtonPanel.setBackground(Color.WHITE);
        JButton startGameButton = new JButton("Start Game");

        initStartButton(startGameButton);
        startGameButtonPanel.add(startGameButton);

        add(startGameButtonPanel);

        initFrame();
    }

    private void initGameElements() {
        JPanel northPanel = new JPanel(new GridLayout(1, 3));
        JPanel mainPanel = new JPanel(new GridLayout(3, 3));
        JPanel southPanel = new JPanel(new FlowLayout());
        southPanel.setBackground(Color.WHITE);
        JButton restartButton = new JButton("Restart Game");
        mainLabel = new JLabel();
        leftPlayerLabel = new JLabel();
        rightPlayerLabel = new JLabel();

        initRestartButton(restartButton);
        initGameButtons(mainPanel);
        styleLabel(leftPlayerLabel, Color.BLACK);
        styleLabel(mainLabel, Color.RED);
        styleLabel(rightPlayerLabel, Color.BLACK);

        northPanel.add(leftPlayerLabel);
        northPanel.add(mainLabel);
        northPanel.add(rightPlayerLabel);
        southPanel.add(restartButton);
        add(mainPanel);
        add(northPanel, BorderLayout.NORTH);
        add(southPanel, BorderLayout.SOUTH);

        initGame();
    }

    private void initGame() {
        // 0 player starts | 1 BOT starts
        int whoStarts = (int) (Math.random() * 2);
        playerSymbol = (whoStarts == 0) ? "X" : "O";
        botSymbol = (whoStarts == 1) ? "X" : "O";
        String whoStartsLabel = (whoStarts == 0) ? "<html><label>You<br>" + playerSymbol + "</label></html>" : "<html><label>BOT<br>" + botSymbol + "</label></html>";
        leftPlayerLabel.setText(whoStartsLabel);
        rightPlayerLabel.setText((whoStartsLabel.contains("You")) ? "<html><label>BOT<br>" + botSymbol + "</label></html>" : "<html><label>You<br>" + playerSymbol + "</label></html>");
        mainLabel.setText((whoStarts == 0) ? "Your Turn" : "BOT Turn");
        if (whoStarts == 1) botTurn();
    }

    private void styleLabel(JLabel label, Color fontColor) {
        label.setForeground(fontColor);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 30));
        label.setOpaque(true);
        label.setBackground(Color.WHITE);
    }

    private void initStartButton(JButton startButton) {
        startButton.setFocusable(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.setBackground(Color.RED);
        startButton.setForeground(Color.WHITE);
        startButton.setFont(new Font("Arial", Font.BOLD, 25));
        startButton.addActionListener(listener);
    }

    private void initRestartButton(JButton restartButton) {
        restartButton.setFocusable(false);
        restartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        restartButton.setBackground(Color.GREEN);
        restartButton.setForeground(Color.RED);
        restartButton.addActionListener(listener);
    }

    private void initGameButtons(JPanel mainPanel) {
        gameButtons = new JButton[9];
        for (int i = 0; i < gameButtons.length; i++) {
            gameButtons[i] = new JButton();
            gameButtons[i].setUI(new BasicButtonUI());
            gameButtons[i].setBorder(new LineBorder(Color.BLACK, 2));
            gameButtons[i].setFont(new Font("Arial", Font.BOLD, 70));
            gameButtons[i].setBackground(Color.WHITE);
            mainPanel.add(gameButtons[i]);
            gameButtons[i].addActionListener(listener);
            gameButtons[i].addMouseListener(listener);
            gameButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            gameButtons[i].setFocusable(false);
            gameButtons[i].setActionCommand("gameButton");
        }
    }

    private void initFrame() {
        setTitle("Tic Tac Toe");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void enableGameButtons(boolean enable) {
        for (JButton gameButton : gameButtons) {
            gameButton.setBackground(Color.WHITE);
            changeGameButtonState(gameButton, enable);
        }
    }

    private void changeGameButtonState(JButton gameButton, boolean state) {
        if(state) {
            gameButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            gameButton.addMouseListener(listener);
            gameButton.addActionListener(listener);
        }
        else {
            gameButton.setCursor(null);
            gameButton.removeMouseListener(listener);
            gameButton.removeActionListener(listener);
        }
    }

    private void resetGameButtons() {
        for (JButton gameButton : gameButtons) {
            gameButton.setText("");
            gameButton.setBackground(Color.WHITE);
        }
    }

    private void endGame(Object[] places, String description) {
        enableGameButtons(false);
        mainLabel.setText(description);
        if (places[0] != null) {
            for (Object place : places) {
                ((JButton) place).setBackground(Color.gray);
            }
        }
    }

    public static void main(String[] args) {
        new TicTacToe();
    }

    private void botTurn() {
        this.setEnabled(false);
        mainLabel.setText("BOT Turn");
        if (isWin(botSymbol)[0] == null && isWin(playerSymbol)[0] == null && !isTie()) {
            int randomPos = botIntelligence();
            if (randomPos == -1) {
                java.util.List<Integer> possiblePlaces = getPossiblePlaces();
                randomPos = possiblePlaces.get((int) (Math.random() * possiblePlaces.size()));
            }
            Timer timer = createTimerForBOT(randomPos);
            timer.start();
        }
    }

    private java.util.List<Integer> getPossiblePlaces() {
        java.util.List<Integer> possiblePlaces = new ArrayList<>();
        for (int i = 0; i < gameButtons.length; i++) {
            if (gameButtons[i].getText().isEmpty()) possiblePlaces.add(i);
        }
        return possiblePlaces;
    }

    private Timer createTimerForBOT(int randomPos) {
        final Timer timer = new Timer(2000, e -> {
            gameButtons[randomPos].setText(botSymbol);
            gameButtons[randomPos].setForeground(Color.RED);
            changeGameButtonState(gameButtons[randomPos], false);
            Object[] places = isWin(botSymbol);
            if (places[0] != null) endGame(places, "BOT Wins");
            else if (isTie()) endGame(places, "TIE!");
            else mainLabel.setText("Your Turn");
        });
        timer.setRepeats(false);
        TimerTask task = new TimerTask() {
            public void run() {
                TicTacToe.this.setEnabled(true);
            }
        };
        new java.util.Timer().schedule(task, timer.getDelay());
        return timer;
    }

    private boolean isTie() {
        boolean ifTie = true;
        for (JButton jButton : gameButtons) {
            if (jButton.getText().isEmpty()) ifTie = false;
        }
        return ifTie && isWin(botSymbol)[0] == null && isWin(playerSymbol)[0] == null;
    }

    private Object[] isWin(String symbol) {
        Object[] response = new Object[3];
        if (gameButtons[0].getText().equals(symbol) && gameButtons[1].getText().equals(symbol) && gameButtons[2].getText().equals(symbol)) {
            response[0] = gameButtons[0];
            response[1] = gameButtons[1];
            response[2] = gameButtons[2];
        } else if (gameButtons[3].getText().equals(symbol) && gameButtons[4].getText().equals(symbol) && gameButtons[5].getText().equals(symbol)) {
            response[0] = gameButtons[3];
            response[1] = gameButtons[4];
            response[2] = gameButtons[5];
        } else if (gameButtons[6].getText().equals(symbol) && gameButtons[7].getText().equals(symbol) && gameButtons[8].getText().equals(symbol)) {
            response[0] = gameButtons[6];
            response[1] = gameButtons[7];
            response[2] = gameButtons[8];
        } else if (gameButtons[0].getText().equals(symbol) && gameButtons[3].getText().equals(symbol) && gameButtons[6].getText().equals(symbol)) {
            response[0] = gameButtons[0];
            response[1] = gameButtons[3];
            response[2] = gameButtons[6];
        } else if (gameButtons[1].getText().equals(symbol) && gameButtons[4].getText().equals(symbol) && gameButtons[7].getText().equals(symbol)) {
            response[0] = gameButtons[1];
            response[1] = gameButtons[4];
            response[2] = gameButtons[7];
        } else if (gameButtons[2].getText().equals(symbol) && gameButtons[5].getText().equals(symbol) && gameButtons[8].getText().equals(symbol)) {
            response[0] = gameButtons[2];
            response[1] = gameButtons[5];
            response[2] = gameButtons[8];
        } else if (gameButtons[0].getText().equals(symbol) && gameButtons[4].getText().equals(symbol) && gameButtons[8].getText().equals(symbol)) {
            response[0] = gameButtons[0];
            response[1] = gameButtons[4];
            response[2] = gameButtons[8];
        } else if (gameButtons[2].getText().equals(symbol) && gameButtons[4].getText().equals(symbol) && gameButtons[6].getText().equals(symbol)) {
            response[0] = gameButtons[2];
            response[1] = gameButtons[4];
            response[2] = gameButtons[6];
        }
        return response;
    }

    private class Listener implements ActionListener, MouseListener {

        public void actionPerformed(ActionEvent event) {
            JButton clickedButton = (JButton) event.getSource();
            if(clickedButton.getActionCommand().equals("Start Game")) {
                SwingUtilities.invokeLater(() -> {
                    TicTacToe.this.getContentPane().removeAll();
                    TicTacToe.this.getContentPane().revalidate();
                    TicTacToe.this.getContentPane().repaint();
                    initGameElements();
                });
            }
            else if (clickedButton.getActionCommand().equals("gameButton")) {
                clickedButton.setText(playerSymbol);
                clickedButton.setForeground(Color.BLUE);
                clickedButton.setBackground(Color.WHITE);
                changeGameButtonState(clickedButton, false);

                Object[] places = isWin(playerSymbol);
                if (places[0] != null) endGame(places, "You Win");
                else if (isTie()) endGame(places, "TIE!");
                else botTurn();
            } else if (clickedButton.getActionCommand().equals("Restart Game")) {
                resetGameButtons();
                initGame();
                enableGameButtons(true);
            }
        }

        public void mouseClicked(MouseEvent event) {
        }

        public void mousePressed(MouseEvent event) {
        }

        public void mouseReleased(MouseEvent event) {
        }

        public void mouseEntered(MouseEvent event) {
            JButton button = (JButton) event.getSource();
            button.setBackground(Color.YELLOW);
        }

        public void mouseExited(MouseEvent event) {
            JButton button = (JButton) event.getSource();
            button.setBackground(Color.WHITE);
        }
    }

    private int botIntelligence() {
        //Index 0
        if ((gameButtons[0].getText().isEmpty()) && ((gameButtons[1].getText().equals(botSymbol) && gameButtons[2].getText().equals(botSymbol)) || (gameButtons[3].getText().equals(botSymbol) && gameButtons[6].getText().equals(botSymbol)) || (gameButtons[4].getText().equals(botSymbol) && gameButtons[8].getText().equals(botSymbol)))) {
            return 0;
        }
        //Index 1
        else if ((gameButtons[1].getText().isEmpty()) && ((gameButtons[0].getText().equals(botSymbol) && gameButtons[2].getText().equals(botSymbol)) || (gameButtons[4].getText().equals(botSymbol) && gameButtons[7].getText().equals(botSymbol)))) {
            return 1;
        }
        //Index 2
        else if ((gameButtons[2].getText().isEmpty()) && ((gameButtons[0].getText().equals(botSymbol) && gameButtons[1].getText().equals(botSymbol)) || (gameButtons[5].getText().equals(botSymbol) && gameButtons[8].getText().equals(botSymbol)) || (gameButtons[4].getText().equals(botSymbol) && gameButtons[6].getText().equals(botSymbol)))) {
            return 2;
        }
        //Index 3
        else if ((gameButtons[3].getText().isEmpty()) && ((gameButtons[0].getText().equals(botSymbol) && gameButtons[6].getText().equals(botSymbol)) || (gameButtons[4].getText().equals(botSymbol) && gameButtons[5].getText().equals(botSymbol)))) {
            return 3;
        }
        //Index 4
        else if ((gameButtons[4].getText().isEmpty()) && ((gameButtons[1].getText().equals(botSymbol) && gameButtons[7].getText().equals(botSymbol)) || (gameButtons[3].getText().equals(botSymbol) && gameButtons[5].getText().equals(botSymbol)) || (gameButtons[2].getText().equals(botSymbol) && gameButtons[6].getText().equals(botSymbol)) || (gameButtons[0].getText().equals(botSymbol) && gameButtons[8].getText().equals(botSymbol)))) {
            return 4;
        }
        //Index 5
        else if ((gameButtons[5].getText().isEmpty()) && ((gameButtons[2].getText().equals(botSymbol) && gameButtons[8].getText().equals(botSymbol)) || (gameButtons[3].getText().equals(botSymbol) && gameButtons[4].getText().equals(botSymbol)))) {
            return 5;
        }
        //Index 6
        else if ((gameButtons[6].getText().isEmpty()) && ((gameButtons[0].getText().equals(botSymbol) && gameButtons[3].getText().equals(botSymbol)) || (gameButtons[7].getText().equals(botSymbol) && gameButtons[8].getText().equals(botSymbol)) || (gameButtons[2].getText().equals(botSymbol) && gameButtons[4].getText().equals(botSymbol)))) {
            return 6;
        }
        //Index 7
        else if ((gameButtons[7].getText().isEmpty()) && ((gameButtons[1].getText().equals(botSymbol) && gameButtons[4].getText().equals(botSymbol)) || (gameButtons[6].getText().equals(botSymbol) && gameButtons[8].getText().equals(botSymbol)))) {
            return 7;
        }
        //Index 8
        else if ((gameButtons[8].getText().isEmpty()) && ((gameButtons[2].getText().equals(botSymbol) && gameButtons[5].getText().equals(botSymbol)) || (gameButtons[0].getText().equals(botSymbol) && gameButtons[4].getText().equals(botSymbol)) || (gameButtons[6].getText().equals(botSymbol) && gameButtons[7].getText().equals(botSymbol)))) {
            return 8;
        }
        //Index 0
        else if ((gameButtons[0].getText().isEmpty()) && ((gameButtons[1].getText().equals(playerSymbol) && gameButtons[2].getText().equals(playerSymbol)) || (gameButtons[3].getText().equals(playerSymbol) && gameButtons[6].getText().equals(playerSymbol)) || (gameButtons[4].getText().equals(playerSymbol) && gameButtons[8].getText().equals(playerSymbol)))) {
            return 0;
        }
        //Index 1
        else if ((gameButtons[1].getText().isEmpty()) && ((gameButtons[0].getText().equals(playerSymbol) && gameButtons[2].getText().equals(playerSymbol)) || (gameButtons[4].getText().equals(playerSymbol) && gameButtons[7].getText().equals(playerSymbol)))) {
            return 1;
        }
        //Index 2
        else if ((gameButtons[2].getText().isEmpty()) && ((gameButtons[0].getText().equals(playerSymbol) && gameButtons[1].getText().equals(playerSymbol)) || (gameButtons[5].getText().equals(playerSymbol) && gameButtons[8].getText().equals(playerSymbol)) || (gameButtons[4].getText().equals(playerSymbol) && gameButtons[6].getText().equals(playerSymbol)))) {
            return 2;
        }
        //Index 3
        else if ((gameButtons[3].getText().isEmpty()) && ((gameButtons[0].getText().equals(playerSymbol) && gameButtons[6].getText().equals(playerSymbol)) || (gameButtons[4].getText().equals(playerSymbol) && gameButtons[5].getText().equals(playerSymbol)))) {
            return 3;
        }
        //Index 4
        else if ((gameButtons[4].getText().isEmpty()) && ((gameButtons[1].getText().equals(playerSymbol) && gameButtons[7].getText().equals(playerSymbol)) || (gameButtons[3].getText().equals(playerSymbol) && gameButtons[5].getText().equals(playerSymbol)) || (gameButtons[2].getText().equals(playerSymbol) && gameButtons[6].getText().equals(playerSymbol)) || (gameButtons[0].getText().equals(playerSymbol) && gameButtons[8].getText().equals(playerSymbol)))) {
            return 4;
        }
        //Index 5
        else if ((gameButtons[5].getText().isEmpty()) && ((gameButtons[2].getText().equals(playerSymbol) && gameButtons[8].getText().equals(playerSymbol)) || (gameButtons[3].getText().equals(playerSymbol) && gameButtons[4].getText().equals(playerSymbol)))) {
            return 5;
        }
        //Index 6
        else if ((gameButtons[6].getText().isEmpty()) && ((gameButtons[0].getText().equals(playerSymbol) && gameButtons[3].getText().equals(playerSymbol)) || (gameButtons[7].getText().equals(playerSymbol) && gameButtons[8].getText().equals(playerSymbol)) || (gameButtons[2].getText().equals(playerSymbol) && gameButtons[4].getText().equals(playerSymbol)))) {
            return 6;
        }
        //Index 7
        else if ((gameButtons[7].getText().isEmpty()) && ((gameButtons[1].getText().equals(playerSymbol) && gameButtons[4].getText().equals(playerSymbol)) || (gameButtons[6].getText().equals(playerSymbol) && gameButtons[8].getText().equals(playerSymbol)))) {
            return 7;
        }
        //Index 8
        else if ((gameButtons[8].getText().isEmpty()) && ((gameButtons[2].getText().equals(playerSymbol) && gameButtons[5].getText().equals(playerSymbol)) || (gameButtons[0].getText().equals(playerSymbol) && gameButtons[4].getText().equals(playerSymbol)) || (gameButtons[6].getText().equals(playerSymbol) && gameButtons[7].getText().equals(playerSymbol)))) {
            return 8;
        }
        return -1;
    }
}