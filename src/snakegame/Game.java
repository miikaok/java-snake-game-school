package snakegame;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import java.io.IOException;

public class Game extends JFrame {

    // Kulkusuunta
    static char direction = '0';
    static char previous_direction = '0';

    // Pisteet ja pelin tila
    static int score = 0;
    static boolean gameOver = false;

    // Pelilaudan koko
    static int width = 40;
    static int height = 16;

    // Madon nopeus
    static int Speed = 1;

    public static void main(String... arg) throws IOException, InterruptedException {

        new Game();

        Snake snake = new Snake(width, height);
        Map map = new Map(width, height);
        Fruit fruit = new Fruit(width, height);

        while (!gameOver) {

            CleanScreen();

            Update(snake, fruit, map, direction, width, height);
            Draw(snake, map, fruit, gameOver);

            Thread.sleep(100);
        }
    }

    public static void Update(Snake snake, Fruit fruit, Map map, char direction, int width, int height) {

        if (snake.length > 0) {

            // Siirretään aikaisempia sijainteja yhdellä indeksillä taakse päin
            for (int i = snake.length - 1; i >= 1; i--) {
                snake.tailx[i] = snake.tailx[i - 1];
                snake.taily[i] = snake.taily[i - 1];
            }

            // Lisätään uusi sijainti ensimmäiseksi indeksiksi
            snake.tailx[0] = snake.x;
            snake.taily[0] = snake.y;
        }

        // Päivitetään jos suunta on muuttunut
        if (((direction == 's') && (previous_direction == 'w'))
                || ((direction == 'w') && (previous_direction == 's'))
                || ((direction == 'a') && (previous_direction == 'd'))
                || ((direction == 'd') && (previous_direction == 'a'))) {
            direction = previous_direction;
        }

        switch (direction) {
            case 'w':
                snake.y--;
                break;
            case 's':
                snake.y++;
                break;
            case 'a':
                snake.x--;
                break;
            case 'd':
                snake.x++;
                break;
        }

        previous_direction = direction;

        if ((snake.x == 0) || (snake.x == map.mapWidth + 1) || (snake.y == -1) || (snake.y == map.mapHeight)) {
            if (snake.x == 0) {
                snake.x = map.mapWidth;
            } else if (snake.x == map.mapWidth + 1) {
                snake.x = 1;
            } else if (snake.y == -1) {
                snake.y = map.mapHeight - 1;
            } else {
                snake.y = 0;
            }
        }

        // Jos madon pää osuu hedelmään niin generoidaan uusi hedelmä ja nostetaan
        // pisteitä 10:llä.
        if ((snake.x == fruit.x) && (snake.y == fruit.y)) {
            fruit.reset(width, height, snake);
            snake.eatfood();
            score += 10;
        }

        // Jos madon pää osuu sen omaan häntään niin peli päättyy.
        for (int i = 0; i < snake.length; i++) {
            if ((snake.x == snake.tailx[i]) && (snake.y == snake.taily[i])) {
                gameOver = true;
            }
        }
    }

    public static void CleanScreen() throws IOException, InterruptedException {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }

    public static void Draw(Snake snake, Map map, Fruit fruit, boolean Gameover) {

        int W = map.mapWidth;
        int H = map.mapHeight;

        int snake_x = snake.x;
        int snake_y = snake.y;

        int food_x = fruit.x;
        int food_y = fruit.y;

        String output_buffer = ""; // Carriage return

        for (int i = 0; i < W + 2; i++) {
            output_buffer += "#";
        }

        output_buffer += "\n";

        if (!Gameover) {
            for (int i = 0; i < H; i++) {
                for (int j = 0; j < W + 2; j++) {
                    if (j == 0) {
                        output_buffer += "#";
                    } else if (j == W + 1) {
                        output_buffer += "#";
                    } else if ((i == food_y) && (j == food_x)) {
                        output_buffer += "*";
                    } else if ((i == snake_y) && (j == snake_x)) {
                        output_buffer += "0";
                    } else if (snake.length > 0) {
                        boolean is_tail = false;
                        for (int k = 0; k < snake.length; k++) {
                            if ((snake.tailx[k] == j) && (snake.taily[k] == i)) {
                                output_buffer += "O";
                                is_tail = true;
                                break;
                            }

                        }
                        if (!is_tail)
                            output_buffer += " ";
                    } else {
                        output_buffer += " ";
                    }
                }
                output_buffer += "\n";
            }
        } else {
            for (int i = 0; i < H; i++) {
                if (i != H / 2) {
                    for (int j = 0; j < W + 2; j++) {
                        if (j == 0) {
                            output_buffer += "#";
                        } else if (j == W + 1) {
                            output_buffer += "#";
                        } else {
                            output_buffer += " ";
                        }
                    }
                    output_buffer += "\n";
                } else {
                    output_buffer += "#";
                    for (int j = 0; j < (W - 10) / 2; j++) {
                        output_buffer += " ";
                    }
                    output_buffer += "GAME OVER!";
                    for (int j = 0; j < (W - 10) / 2; j++) {
                        output_buffer += " ";
                    }
                    if (W % 2 != 0) {
                        output_buffer += " ";
                    }
                    output_buffer += "#\n";
                }
            }
        }
        for (int i = 0; i < W + 2; i++) {
            output_buffer += "#";
        }
        output_buffer += "\nScore: " + score + "\n";

        System.out.print(output_buffer);
    }

    public Game() { // constructor
        this.setSize(150, 150);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                direction = e.getKeyChar();
            }
        });
    }
}