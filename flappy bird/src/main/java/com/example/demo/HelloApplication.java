package com.example.demo;

import javafx.animation.AnimationTimer;       
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Random;

public class HelloApplication extends Application {

    // размеры окна
    private static final int WIDTH = 400;        // ширина окна
    private static final int HEIGHT = 600;       // высота окна

    // птичка (КАРТИНКА вместо Rectangle)
    private ImageView bird;                      // узел, который показывает картинку птицы
    private double birdVy = 0;                   // вертикальная скорость птицы (пикс/кадр)
    private final double GRAVITY = 0.4;          // гравитация — каждую «рамку» прибавляется к скорости
    private final double JUMP = -8;              // сила прыжка — мгновенно задаёт скорость вверх

    // параметры труб
    private static final double PIPE_WIDTH = 60; // ширина трубы
    private static final double GAP = 160;       // зазор между верхней и нижней трубой
    private static final double PIPE_SPEED = 3;  // скорость движения труб влево
    private static final double PIPE_SPACING = 200; // расстояние между парами труб
    private final ArrayList<PipePair> pipes = new ArrayList<>(); // все пары труб на сцене
    private final Random random = new Random();  // рандом для высоты зазора

    //интерфейс
    private Text scoreText;                      // текущий счёт
    private Text gameOverText;                   // надпись Game Over
    private boolean gameOver = false;            // флаг «конец игры»
    private int score = 0;                       // число пройденных пар

    private Pane root;                           // корневой контейнер узлов

    // пара труб (верх + низ)
    private static class PipePair {
        Rectangle top;                           // верхняя труба
        Rectangle bottom;                        // нижняя труба
        boolean scored = false;                  // очко за эту пару уже начислили?

        PipePair(Rectangle top, Rectangle bottom) {
            this.top = top;                      // сохраняем ссылку на верхнюю трубу
            this.bottom = bottom;                // сохраняем ссылку на нижнюю трубу
        }
    }

    @Override
    public void start(Stage stage) {
        root = new Pane();                       // создаём пустой слой
        root.setPrefSize(WIDTH, HEIGHT);         // задаём желаемый размер слоя

        // создаём ПТИЧКУ как картинку
        Image birdImg = new Image(               // загружаем изображение из ресурсов
                getClass().getResourceAsStream("bird.png")
        );
        bird = new ImageView(birdImg);           // оборачиваем картинку в отображаемый узел
        bird.setFitWidth(30);                    // задаём логический размер хитбокса по ширине
        bird.setFitHeight(30);                   // и по высоте (картинка масштабируется)
        bird.setPreserveRatio(true);             // держим пропорции картинки
        bird.setSmooth(true);                    // сглаживание при масштабировании
        bird.setX(100);                          // стартовая позиция X (левее центра)
        bird.setY(HEIGHT / 2.0 - 15);            // стартовая позиция Y (примерно по центру)

        // текст счёта
        scoreText = new Text("0");               // начальный счёт
        scoreText.setFont(Font.font(28));        // размер шрифта
        scoreText.setFill(Color.BLACK);          // цвет текста
        scoreText.setX(20);                      // позиция X
        scoreText.setY(40);                      // позиция Y

        //текст Game Over
        gameOverText = new Text("GAME OVER\nPress ENTER to restart");
        gameOverText.setFont(Font.font(24));
        gameOverText.setFill(Color.CRIMSON);
        gameOverText.setX(WIDTH / 2.0 - 140);    // грубое центрирование
        gameOverText.setY(HEIGHT / 2.0 - 20);
        gameOverText.setVisible(false);          // по умолчанию скрыт

        // добавляем узлы на сцену
        root.getChildren().addAll(bird, scoreText, gameOverText);

        // гарантируем, что текст поверх всех труб/птички
        scoreText.toFront();
        gameOverText.toFront();

        // стартовые 3 пары труб (чтобы игра была «живая» сразу)
        double startX = 450;                     // первая пара справа от экрана
        for (int i = 0; i < 3; i++) {
            spawnPipe(startX + i * PIPE_SPACING);
        }

        // создаём сцену и вешаем обработчики ввода
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BEIGE); // бежевый фон
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case SPACE -> {
                    if (gameOver) {
                        resetGame();       // если игра закончена — рестарт
                    } else {
                        jump();             // если нет — обычный прыжок
                    }
                }
                case ENTER -> resetGame();  // можно оставить ENTER тоже для рестарта
                default -> {}
            }
        });
        scene.setOnMouseClicked(e -> jump());    // клик мышью — тоже прыжок

        stage.setTitle("Flappy Bird — JavaFX");  // заголовок окна
        stage.setScene(scene);                   // прикрепляем сцену к окну
        stage.show();                            // показываем окно

        // игровой цикл: обновляем игру каждый кадр
        new AnimationTimer() {
            @Override public void handle(long now) {
                if (!gameOver) update();         // пока не конец — выполняем логику кадра
            }
        }.start();
    }

    // прыжок: задаём скорость вверх
    private void jump() {
        if (!gameOver) birdVy = JUMP;            // если не конец, меняем вертикальную скорость
    }

    // логика одного кадра
    private void update() {
        // 1) физика птички
        birdVy += GRAVITY;                        // прибавляем гравитацию к скорости
        bird.setY(bird.getY() + birdVy);         // смещаем птицу на величину скорости

        // 2) движение труб влево
        for (PipePair p : pipes) {
            p.top.setX(p.top.getX() - PIPE_SPEED);
            p.bottom.setX(p.bottom.getX() - PIPE_SPEED);
        }

        // 3) подспавниваем новую пару, когда последняя ушла влево
        PipePair last = pipes.get(pipes.size() - 1);
        if (last.top.getX() < WIDTH - PIPE_SPACING) {
            spawnPipe(WIDTH + 50);               // создаём новую пару справа
        }

        // 4) удаляем самую левую пару, если она далеко за экраном
        if (!pipes.isEmpty()) {
            PipePair first = pipes.get(0);
            if (first.top.getX() + PIPE_WIDTH < -50) {
                root.getChildren().removeAll(first.top, first.bottom);
                pipes.remove(0);
            }
        }

        // 5) вылет за верх/низ — проигрыш
        if (bird.getY() < 0 || bird.getY() + bird.getFitHeight() > HEIGHT) {
            setGameOver(); return;
        }

// 6) столкновения с трубами — проигрыш
        for (PipePair p : pipes) {
            boolean hitTop = bird.getBoundsInParent().intersects(p.top.getBoundsInParent());
            boolean hitBottom = bird.getBoundsInParent().intersects(p.bottom.getBoundsInParent());
            if (hitTop || hitBottom) { setGameOver(); return; }
        }

        // 7) счёт: +1, когда птица перелетела пару
        for (PipePair p : pipes) {
            boolean passed = p.top.getX() + PIPE_WIDTH < bird.getX();
            if (!p.scored && passed) {
                p.scored = true;
                score++;
                scoreText.setText(String.valueOf(score));
            }
        }
    }

    // ---- создание одной пары труб ----
    private void spawnPipe(double x) {
        // выбираем случайный центр зазора в «безопасной зоне»
        double gapCenter = 120 + random.nextDouble() * (HEIGHT - 240);
        double topHeight = gapCenter - GAP / 2.0;     // высота верхней трубы
        double bottomY = gapCenter + GAP / 2.0;       // верхняя координата нижней трубы
        double bottomHeight = HEIGHT - bottomY;       // высота нижней трубы

        Rectangle top = new Rectangle(PIPE_WIDTH, topHeight);   // верхняя труба
        top.setFill(Color.FORESTGREEN);
        top.setX(x); top.setY(0);

        Rectangle bottom = new Rectangle(PIPE_WIDTH, bottomHeight); // нижняя труба
        bottom.setFill(Color.FORESTGREEN);
        bottom.setX(x); bottom.setY(bottomY);

        PipePair pair = new PipePair(top, bottom);    // собираем в пару
        pipes.add(pair);                               // добавляем в список
        root.getChildren().addAll(top, bottom);       // рисуем на сцене

        // снова поднимаем текст поверх, чтобы новые трубы его не перекрыли
        scoreText.toFront();
        gameOverText.toFront();
    }

    // ---- включаем режим Game Over ----
    private void setGameOver() {
        gameOver = true;                               // стопим апдейт
        gameOverText.setVisible(true);                 // показываем надпись
        gameOverText.toFront();                        // на всякий случай — наверх
    }

    // ---- полный сброс игры ----
    private void resetGame() {
        gameOver = false;                              // снова играем
        score = 0;                                     // сбрасываем счёт
        scoreText.setText("0");
        gameOverText.setVisible(false);

        // удаляем старые трубы
        for (PipePair p : pipes) root.getChildren().removeAll(p.top, p.bottom);
        pipes.clear();

        // возвращаем птицу в центр и обнуляем скорость
        bird.setX(100);
        bird.setY(HEIGHT / 2.0 - 15);
        birdVy = 0;

        // создаём стартовые 3 пары
        double startX = 450;
        for (int i = 0; i < 3; i++) spawnPipe(startX + i * PIPE_SPACING);
    }

    public static void main(String[] args) {
        launch();                                      // стандартный запуск JavaFX
    }
}
