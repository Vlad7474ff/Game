import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random; // Добавьте эту строку

// Взаимодействие с переменными по названию строкового типа (button_)

public class WordsGameGUI extends JDialog {

    private JPanel contentPane;
    private JButton button_backspace;
    private JLabel word_text;
    private JButton button_2;
    private JButton button_1;
    private JButton button_10;
    private JButton button_19;
    private JButton button_3;
    private JButton button_4;
    private JButton button_5;
    private JButton button_6;
    private JButton button_7;
    private JButton button_34;
    private JButton button_9;
    private JButton button_8;
    private JButton button_31;
    private JButton button_32;
    private JButton button_33;
    private JButton button_35;
    private JButton button_36;
    private JButton button_18;
    private JButton button_27;
    private JButton button_17;
    private JButton button_26;
    private JButton button_16;
    private JButton button_25;
    private JButton button_15;
    private JButton button_24;
    private JButton button_14;
    private JButton button_23;
    private JButton button_13;
    private JButton button_22;
    private JButton button_12;
    private JButton button_21;
    private JButton button_11;
    private JButton button_30;
    private JButton button_20;
    private JButton button_28;
    private JButton button_29;
//    private JLabel mistakes;
    private JTextField input_text;
    private JButton start_button;
    private JLabel victory_count;
    private JLabel error_picture;
    private JCheckBox inputCheckBox;
    private JCheckBox computer_game;
    private static final String GameHistoryFile = "game_history.txt";
    private StringBuilder pressedLetters = new StringBuilder();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    int randomNum;
    String InputGameWord = "";
    Integer errorCount = 0;
    Integer gamesCount = 0;
    Boolean startFlagFromRightInputWord = true;
    ArrayList<String> words = new ArrayList<String>();
    ArrayList<String> usingWordLetters = new ArrayList<String>(); //Перевести в Character substring
    ArrayList<JButton> usedButtons = new ArrayList<JButton>();
    ArrayList<JButton> all_buttons = new ArrayList<JButton>(Arrays.asList(button_1, button_2, button_3, button_4,
                                                                          button_5, button_6, button_7, button_8,
                                                                          button_9, button_10, button_11, button_12,
                                                                          button_13, button_14, button_15, button_16,
                                                                          button_17, button_18, button_19, button_20,
                                                                          button_21, button_22, button_23, button_24,
                                                                          button_25, button_26, button_27, button_28,
                                                                          button_29, button_30, button_31, button_32,
                                                                          button_33));
    private Random random = new Random();
    private static final double COMPUTER_GUESS_CHANCE = 0.4; // Шанс угадывания буквы компьютером
    private Timer computerMoveTimer;
    private String currentWord;
    private static final String RESOURCES_PATH = "src/";  // папка с ресурсами
    private static final String DICTIONARY_FILE = RESOURCES_PATH + "dictionary.txt";
    private static final String ERROR_IMAGE_PATTERN = RESOURCES_PATH + "error_%d.png";

    private void SaveGameResult(boolean isVictory, String word) {
        try (PrintWriter out = new PrintWriter(new FileWriter(GameHistoryFile, true))) {
            String result = isVictory ? "Победа" : "Поражение";
            String playerType = computer_game.isSelected() ? "Компьютер" : "Человек";
            String formattedDateTime = LocalDateTime.now().format(formatter);
            out.println(formattedDateTime + " - " + result + ": " + word + 
                        " | Игрок: " + playerType +
                        " | Нажатые буквы: " + pressedLetters.toString());
            pressedLetters.setLength(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton) e.getSource();
            String letter = button.getText();
            pressedLetters.append(letter).append(" ");
            usedButtons.add(button);
            button.setEnabled(false);
            if (usingWordLetters.contains(e.getActionCommand())) {
                for (int i = 0; i < usingWordLetters.size(); i++) {
                    if (usingWordLetters.get(i).equals(button.getText())) {
                        InputGameWord = InputGameWord.substring(0, (i + 1) * 2) + button.getText() + InputGameWord.substring((i + 1) * 2 + 1);
                        word_text.setText(InputGameWord);
                        System.out.println(InputGameWord);
                    }
                }
                button.setBackground(Color.GREEN);
                if (InputGameWord.indexOf("*") == -1) {
                    // Используем текущее слово вместо words.get(randomNum)
                    String wordToSave = inputCheckBox.isSelected() ? input_text.getText().toUpperCase() : words.get(randomNum);
                    SaveGameResult(true, wordToSave);
                    for (int i = 0; i < usedButtons.size(); i++) {
                        usedButtons.get(i).setEnabled(true);
                        usedButtons.get(i).setBackground(null);
                        input_text.setText("");
                        word_text.setText("ПОБЕДА!");
                        pictureChange(100);
                        input_text.setEnabled(true);
                        start_button.setEnabled(true);
                        InputGameWord = "";
                        usingWordLetters.clear();
                    }
                    errorCount = 0;
                    for (int i = 0; i < all_buttons.size(); i++) {
                        all_buttons.get(i).removeActionListener(actionListener);
                    }
//                    mistakes.setText(errorCount + "");
                    gamesCount += 1;
                    victory_count.setText(gamesCount + "");
                }
            } else {
                button.setBackground(Color.RED);
                errorCount += 1;
                pictureChange(errorCount);
                errorCheck();
//                mistakes.setText(errorCount + "");
            }
        }
    };

    public void errorCheck() {
        if (errorCount == 6) {
            // Используем текущее слово вместо words.get(randomNum)
            String wordToSave = inputCheckBox.isSelected() ? input_text.getText().toUpperCase() : words.get(randomNum);
            SaveGameResult(false, wordToSave);
            word_text.setText("ВЫ ПРОИГРАЛИ! СЛОВО: " + wordToSave);
            resetGame();
        }
    }

    public void gameCreate(String word, JLabel word_text, ArrayList<JButton> all_buttons, ActionListener actionListener) {
        start_button.setEnabled(false);
        input_text.setEnabled(false);
        pictureChange(errorCount);
        for (int i = 1; i < word.length() - 1; i++) {
            usingWordLetters.add(word.substring(i, i + 1));
        }
        InputGameWord += (word.substring(0, 1) + " ");
        for (int i = 0; i < usingWordLetters.size(); i++) {
            InputGameWord += "* ";
        }
        InputGameWord += (word.substring(word.length() - 1));
        word_text.setText(InputGameWord);

        for (int i = 0; i < all_buttons.size(); i++) {
            all_buttons.get(i).addActionListener(actionListener);
        }
    }

    public void pictureChange(int count) {
        try {
            String imagePath = String.format(ERROR_IMAGE_PATTERN, count);
            // Пытаемся загрузить изображение из разных возможных путей
            InputStream imageStream = getClass().getClassLoader().getResourceAsStream(imagePath);
            if (imageStream == null) {
                // Пробуем относительный путь от текущей директории
                File imageFile = new File(imagePath);
                if (!imageFile.exists()) {
                    // Пробуем путь относительно корня проекта
                    imageFile = new File("WordsGame/" + imagePath);
                }
                BufferedImage img = ImageIO.read(imageFile);
                ImageIcon icon = new ImageIcon(img);
                error_picture.setIcon(icon);
            } else {
                BufferedImage img = ImageIO.read(imageStream);
                ImageIcon icon = new ImageIcon(img);
                error_picture.setIcon(icon);
            }
        } catch (IOException ex) {
            System.err.println("Ошибка загрузки изображения: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    public void labelGenerate() {
        randomNum = ThreadLocalRandom.current().nextInt(words.size() - 1);
        gameCreate(words.get(randomNum).toUpperCase(), word_text, all_buttons, actionListener);
    }

    public WordsGameGUI() {
        try {
            // Пытаемся загрузить словарь из разных возможных путей
            InputStream dictStream = getClass().getClassLoader().getResourceAsStream(DICTIONARY_FILE);
            BufferedReader br;
            
            if (dictStream != null) {
                br = new BufferedReader(new InputStreamReader(dictStream));
            } else {
                // Пробуем относительный путь от текущей директории
                File file = new File(DICTIONARY_FILE);
                if (!file.exists()) {
                    // Пробуем путь относительно корня проекта
                    file = new File("WordsGame/" + DICTIONARY_FILE);
                }
                br = new BufferedReader(new FileReader(file));
            }

            String line = br.readLine();
            while(line != null) {
                words.add(line);
                line = br.readLine();
            }
            br.close();
        }
        catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Ошибка загрузки словаря: " + e.getMessage(), 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        setContentPane(contentPane);
        setModal(true);

        inputCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (inputCheckBox.isSelected()) {
                    input_text.setEnabled(true);
                } else {
                    input_text.setEnabled(false);
                    input_text.setText("");
                }
            }
        });

        computer_game.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (computer_game.isSelected()) {
                    inputCheckBox.setSelected(true);
                    input_text.setEnabled(true);
                    disableAllButtons();
                } else {
                    enableAllButtons();
                }
            }
        });

        start_button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startFlagFromRightInputWord = true;
                String inputWord = input_text.getText().trim(); // Сохраняем введенное слово
                
                if (!inputCheckBox.isSelected()) {
                    labelGenerate();
                    input_text.setText(""); // Очищаем после запуска игры
                } else {
                    // Проверяем, не пустое ли слово
                    if (inputWord.isEmpty()) {
                        word_text.setText("ВВЕДИТЕ СЛОВО!");
                        startFlagFromRightInputWord = false;
                        return;
                    }

                    for (int i = 0; i < inputWord.length(); i++) {
                        if (!"АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ".contains(inputWord.toUpperCase().substring(i, i + 1))) {
                            word_text.setText("В СЛОВЕ ЕСТЬ НЕДОПУСТИМЫЕ СИМВОЛЫ!");
                            startFlagFromRightInputWord = false;
                        }
                    }

                    if (inputWord.length() < 3) {
                        word_text.setText("ВВЕДИТЕ СЛОВО ДЛИНОЙ БОЛЬШЕ 3-Х СИМВОЛОВ!");
                        startFlagFromRightInputWord = false;
                    }

                    if (!startFlagFromRightInputWord) {
                        input_text.setText("");
                    } else {
                        if (computer_game.isSelected()) {
                            currentWord = inputWord.toUpperCase(); // Используем сохраненное слово
                            startComputerGame();
                            input_text.setText(""); // Очищаем после запуска игры
                        } else {
                            gameCreate(inputWord.toUpperCase(), word_text, all_buttons, actionListener);
                        }
                    }
                }
            }
        });
    }

    private void disableAllButtons() {
        for (JButton button : all_buttons) {
            button.setEnabled(false);
        }
    }

    private void enableAllButtons() {
        for (JButton button : all_buttons) {
            button.setEnabled(true);
        }
    }

    private void startComputerGame() {
        // Полностью очищаем предыдущее состояние
        if (computerMoveTimer != null) {
            computerMoveTimer.stop();
            computerMoveTimer = null;
        }

        // Очищаем все обработчики с кнопок
        for (JButton button : all_buttons) {
            if (button != null) {
                for (ActionListener al : button.getActionListeners()) {
                    button.removeActionListener(al);
                }
            }
        }

        currentWord = input_text.getText().toUpperCase();
        usedButtons.clear();
        errorCount = 0;
        gameCreate(currentWord, word_text, all_buttons, null);
        
        computerMoveTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeComputerMove();
            }
        });
        computerMoveTimer.start();
    }

    private void makeComputerMove() {
        if (InputGameWord.contains("*")) {
            if (random.nextDouble() < COMPUTER_GUESS_CHANCE) {
                // Пытаемся угадать правильную букву
                ArrayList<String> availableLetters = new ArrayList<>();
                for (int i = 0; i < usingWordLetters.size(); i++) {
                    if (InputGameWord.charAt((i + 1) * 2) == '*') {
                        availableLetters.add(usingWordLetters.get(i));
                    }
                }
                
                if (!availableLetters.isEmpty()) {
                    String letter = availableLetters.get(random.nextInt(availableLetters.size()));
                    JButton button = findButtonByLetter(letter.charAt(0));
                    if (button != null && !usedButtons.contains(button)) {
                        processComputerGuess(letter.charAt(0), true);
                        return;
                    }
                }
            }
            
            // Если не удалось угадать правильную букву или не выпал шанс
            makeRandomWrongGuess();
        } else {
            computerMoveTimer.stop();
        }
    }

    private void makeRandomWrongGuess() {
        ArrayList<JButton> availableButtons = new ArrayList<>();
        for (JButton button : all_buttons) {
            if (!usedButtons.contains(button)) {
                availableButtons.add(button);
            }
        }

        if (!availableButtons.isEmpty()) {
            JButton selectedButton = availableButtons.get(random.nextInt(availableButtons.size()));
            char letter = selectedButton.getText().charAt(0);
            processComputerGuess(letter, usingWordLetters.contains(String.valueOf(letter)));
        } else {
            computerMoveTimer.stop();
        }
    }

    private JButton findButtonByLetter(char letter) {
        for (JButton button : all_buttons) {
            if (button.getText().charAt(0) == letter) {
                return button;
            }
        }
        return null;
    }

    private void processComputerGuess(char letter, boolean correct) {
        // Проверяем, не закончена ли игра
        if (errorCount >= 6 || !InputGameWord.contains("*")) {
            if (computerMoveTimer != null) {
                computerMoveTimer.stop();
                computerMoveTimer = null;
            }
            return;
        }

        JButton button = findButtonByLetter(letter);
        if (button != null && !usedButtons.contains(button)) {
            usedButtons.add(button);
            button.setEnabled(false);
            pressedLetters.append(letter).append(" ");
            
            if (correct) {
                button.setBackground(Color.GREEN);
                boolean letterAdded = false;
                for (int i = 0; i < usingWordLetters.size(); i++) {
                    if (usingWordLetters.get(i).equals(String.valueOf(letter))) {
                        InputGameWord = InputGameWord.substring(0, (i + 1) * 2) + letter + InputGameWord.substring((i + 1) * 2 + 1);
                        letterAdded = true;
                    }
                }
                word_text.setText(InputGameWord);
                
                if (!InputGameWord.contains("*")) {
                    if (computerMoveTimer != null) {
                        computerMoveTimer.stop();
                        computerMoveTimer = null;
                    }
                    SaveGameResult(true, currentWord);
                    word_text.setText("ПОБЕДА!");
                    pictureChange(100);
                    gamesCount++;
                    victory_count.setText(String.valueOf(gamesCount));
                    
                    Timer delayTimer = new Timer(2000, e -> {
                        resetGame();
                        ((Timer)e.getSource()).stop();
                    });
                    delayTimer.setRepeats(false);
                    delayTimer.start();
                }
            } else {
                button.setBackground(Color.RED);
                errorCount++;
                pictureChange(errorCount);
                if (errorCount >= 6) {
                    if (computerMoveTimer != null) {
                        computerMoveTimer.stop();
                        computerMoveTimer = null;
                    }
                    SaveGameResult(false, currentWord);
                    word_text.setText("ВЫ ПРОИГРАЛИ! СЛОВО: " + currentWord);
                    
                    // Увеличиваем задержку до 4000 мс (4 секунды)
                    Timer delayTimer = new Timer(4000, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            resetGame();
                            ((Timer)e.getSource()).stop();
                        }
                    });
                    delayTimer.setRepeats(false);
                    delayTimer.start();
                }
            }
        }
    }

    private void resetGame() {
        // Останавливаем все таймеры
        if (computerMoveTimer != null) {
            computerMoveTimer.stop();
            computerMoveTimer = null;
        }

        // Очищаем все обработчики с кнопок
        for (JButton button : all_buttons) {
            if (button != null) {
                for (ActionListener al : button.getActionListeners()) {
                    button.removeActionListener(al);
                }
                button.setEnabled(true);
                button.setBackground(null);
            }
        }

        // Очищаем все состояния игры
        input_text.setText("");
        input_text.setEnabled(true);
        start_button.setEnabled(true);
        InputGameWord = "";
        usingWordLetters.clear();
        usedButtons.clear();
        errorCount = 0;
        
        // Показываем начальную картинку только после задержки
        Timer pictureTimer = new Timer(100, e -> {
            pictureChange(0);
            ((Timer)e.getSource()).stop();
        });
        pictureTimer.setRepeats(false);
        pictureTimer.start();
        
        // Сбрасываем галочки
        inputCheckBox.setSelected(false);
        computer_game.setSelected(false);
        input_text.setEnabled(false);
    }

    public static void main(String[] args) {
        WordsGameGUI dialog = new WordsGameGUI();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
