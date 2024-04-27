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
    private boolean isGameDone;
    private final JLabel mainLabel;
    private final Listener listener;

    public TicTacToe() {
        listener = new Listener();
        JPanel mainPanel = new JPanel(new GridLayout(3, 3));
        JPanel southPanel = new JPanel(new FlowLayout());
        JButton restartButton = new JButton("Restart Game");
        mainLabel = new JLabel("Your Turn");

        initRestartButton(restartButton);
        initGameButtons(mainPanel);
        styleMainLabel();

        southPanel.add(restartButton);
        add(mainPanel);
        add(mainLabel, BorderLayout.NORTH);
        add(southPanel, BorderLayout.SOUTH);

        initFrame();
    }

    private void styleMainLabel() {
        mainLabel.setForeground(Color.RED);
        mainLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainLabel.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 30));
        mainLabel.setOpaque(true);
        mainLabel.setBackground(Color.WHITE);
    }

    private void initRestartButton(JButton restartButton) {
        restartButton.setFocusable(false);
        restartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        restartButton.setBackground(Color.GREEN);
        restartButton.setForeground(Color.RED);
        restartButton.addActionListener(new Listener());
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
        for (JButton gameButton: gameButtons) {
            gameButton.setBackground(Color.WHITE);
            if(enable) {
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
    }

    private void endGame(Object[] places, String description) {
        isGameDone = true;
        enableGameButtons(false);
        mainLabel.setText(description);
        if(places[0] != null) {
            for(Object place: places) {
                ((JButton)place).setBackground(Color.gray);
            }
        }
    }

    public static void main(String[] args) {
        new TicTacToe();
    }

    private void botTurn() {
        mainLabel.setText("BOT Turn");
        if (isWin("O")[0] == null && isWin("X")[0] == null && !isTie()) {
            int randomPos = botIntelligence();
            if (randomPos == -1) {
                java.util.List<Integer> possiblePlaces = getPossiblePlaces();
                randomPos = possiblePlaces.get((int)(Math.random() * possiblePlaces.size()));
            }
            this.setEnabled(false);
            Timer timer = createTimerForBOT(randomPos);
            timer.start();
        }
    }

    private java.util.List<Integer> getPossiblePlaces() {
        java.util.List<Integer> possiblePlaces = new ArrayList<>();
        for(int i = 0; i < gameButtons.length; i++) {
            if(gameButtons[i].getText().isEmpty()) possiblePlaces.add(i);
        }
        return possiblePlaces;
    }

    private Timer createTimerForBOT(int randomPos) {
        final Timer timer = new Timer(2000, e -> {
            gameButtons[randomPos].setText("O");
            gameButtons[randomPos].setForeground(Color.RED);
            Object[] places = isWin("O");
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
        return ifTie && isWin("O")[0] == null && isWin("X")[0] == null;
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
        } else if(gameButtons[2].getText().equals(symbol) && gameButtons[4].getText().equals(symbol) && gameButtons[6].getText().equals(symbol)) {
            response[0] = gameButtons[2];
            response[1] = gameButtons[4];
            response[2] = gameButtons[6];
        }
        return response;
    }

    private class Listener implements ActionListener, MouseListener {

        public void actionPerformed(ActionEvent event) {
            JButton clickedButton = (JButton) event.getSource();
            if (clickedButton.getActionCommand().equals("gameButton")) {
                if (!(clickedButton.getText().equals("X") || clickedButton.getText().equals("O")) && !isGameDone) {
                    clickedButton.setText("X");
                    clickedButton.setForeground(Color.BLUE);
                    clickedButton.setBackground(Color.WHITE);

                    Object[] places = isWin("X");
                    if (places[0] != null) endGame(places, "You Win");
                    else if (isTie()) endGame(places, "TIE!");
                    else botTurn();
                }
            } else if (clickedButton.getActionCommand().equals("Restart Game")) {
                isGameDone = false;
                for (JButton gameButton : gameButtons) {
                    gameButton.setText("");
                    gameButton.setBackground(Color.WHITE);
                }
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
        if ((gameButtons[0].getText().isEmpty()) && ((gameButtons[1].getText().equals("O") && gameButtons[2].getText().equals("O")) || (gameButtons[3].getText().equals("O") && gameButtons[6].getText().equals("O")) || (gameButtons[4].getText().equals("O") && gameButtons[8].getText().equals("O")))) {
            return 0;
        }
        //Index 1
        else if ((gameButtons[1].getText().isEmpty()) && ((gameButtons[0].getText().equals("O") && gameButtons[2].getText().equals("O")) || (gameButtons[4].getText().equals("O") && gameButtons[7].getText().equals("O")))) {
            return 1;
        }
        //Index 2
        else if ((gameButtons[2].getText().isEmpty()) && ((gameButtons[0].getText().equals("O") && gameButtons[1].getText().equals("O")) || (gameButtons[5].getText().equals("O") && gameButtons[8].getText().equals("O")) || (gameButtons[4].getText().equals("O") && gameButtons[6].getText().equals("O")))) {
            return 2;
        }
        //Index 3
        else if ((gameButtons[3].getText().isEmpty()) && ((gameButtons[0].getText().equals("O") && gameButtons[6].getText().equals("O")) || (gameButtons[4].getText().equals("O") && gameButtons[5].getText().equals("O")))) {
            return 3;
        }
        //Index 4
        else if ((gameButtons[4].getText().isEmpty()) && ((gameButtons[1].getText().equals("O") && gameButtons[7].getText().equals("O")) || (gameButtons[3].getText().equals("O") && gameButtons[5].getText().equals("O")) || (gameButtons[2].getText().equals("O") && gameButtons[6].getText().equals("O")) || (gameButtons[0].getText().equals("O") && gameButtons[8].getText().equals("O")))) {
            return 4;
        }
        //Index 5
        else if ((gameButtons[5].getText().isEmpty()) && ((gameButtons[2].getText().equals("O") && gameButtons[8].getText().equals("O")) || (gameButtons[3].getText().equals("O") && gameButtons[4].getText().equals("O")))) {
            return 5;
        }
        //Index 6
        else if ((gameButtons[6].getText().isEmpty()) && ((gameButtons[0].getText().equals("O") && gameButtons[3].getText().equals("O")) || (gameButtons[7].getText().equals("O") && gameButtons[8].getText().equals("O")) || (gameButtons[2].getText().equals("O") && gameButtons[4].getText().equals("O")))) {
            return 6;
        }
        //Index 7
        else if ((gameButtons[7].getText().isEmpty()) && ((gameButtons[1].getText().equals("O") && gameButtons[4].getText().equals("O")) || (gameButtons[6].getText().equals("O") && gameButtons[8].getText().equals("O")))) {
            return 7;
        }
        //Index 8
        else if ((gameButtons[8].getText().isEmpty()) && ((gameButtons[2].getText().equals("O") && gameButtons[5].getText().equals("O")) || (gameButtons[0].getText().equals("O") && gameButtons[4].getText().equals("O")) || (gameButtons[6].getText().equals("O") && gameButtons[7].getText().equals("O")))) {
            return 8;
        }
        //Index 0
        else if ((gameButtons[0].getText().isEmpty()) && ((gameButtons[1].getText().equals("X") && gameButtons[2].getText().equals("X")) || (gameButtons[3].getText().equals("X") && gameButtons[6].getText().equals("X")) || (gameButtons[4].getText().equals("X") && gameButtons[8].getText().equals("X")))) {
            return 0;
        }
        //Index 1
        else if ((gameButtons[1].getText().isEmpty()) && ((gameButtons[0].getText().equals("X") && gameButtons[2].getText().equals("X")) || (gameButtons[4].getText().equals("X") && gameButtons[7].getText().equals("X")))) {
            return 1;
        }
        //Index 2
        else if ((gameButtons[2].getText().isEmpty()) && ((gameButtons[0].getText().equals("X") && gameButtons[1].getText().equals("X")) || (gameButtons[5].getText().equals("X") && gameButtons[8].getText().equals("X")) || (gameButtons[4].getText().equals("X") && gameButtons[6].getText().equals("X")))) {
            return 2;
        }
        //Index 3
        else if ((gameButtons[3].getText().isEmpty()) && ((gameButtons[0].getText().equals("X") && gameButtons[6].getText().equals("X")) || (gameButtons[4].getText().equals("X") && gameButtons[5].getText().equals("X")))) {
            return 3;
        }
        //Index 4
        else if ((gameButtons[4].getText().isEmpty()) && ((gameButtons[1].getText().equals("X") && gameButtons[7].getText().equals("X")) || (gameButtons[3].getText().equals("X") && gameButtons[5].getText().equals("X")) || (gameButtons[2].getText().equals("X") && gameButtons[6].getText().equals("X")) || (gameButtons[0].getText().equals("X") && gameButtons[8].getText().equals("X")))) {
            return 4;
        }
        //Index 5
        else if ((gameButtons[5].getText().isEmpty()) && ((gameButtons[2].getText().equals("X") && gameButtons[8].getText().equals("X")) || (gameButtons[3].getText().equals("X") && gameButtons[4].getText().equals("X")))) {
            return 5;
        }
        //Index 6
        else if ((gameButtons[6].getText().isEmpty()) && ((gameButtons[0].getText().equals("X") && gameButtons[3].getText().equals("X")) || (gameButtons[7].getText().equals("X") && gameButtons[8].getText().equals("X")) || (gameButtons[2].getText().equals("X") && gameButtons[4].getText().equals("X")))) {
            return 6;
        }
        //Index 7
        else if ((gameButtons[7].getText().isEmpty()) && ((gameButtons[1].getText().equals("X") && gameButtons[4].getText().equals("X")) || (gameButtons[6].getText().equals("X") && gameButtons[8].getText().equals("X")))) {
            return 7;
        }
        //Index 8
        else if ((gameButtons[8].getText().isEmpty()) && ((gameButtons[2].getText().equals("X") && gameButtons[5].getText().equals("X")) || (gameButtons[0].getText().equals("X") && gameButtons[4].getText().equals("X")) || (gameButtons[6].getText().equals("X") && gameButtons[7].getText().equals("X")))) {
            return 8;
        }
        return -1;
    }
}