package snakegame;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import java.io.IOException;

public class Game extends JFrame {

    static char dir = '0';
    static char Predir = '0';
    static int score = 0;
    static boolean gameOver = false;
    static int width = 40;
    static int height = 16;
    static int Speed = 0;

    public static void main(String... arg) throws IOException, InterruptedException {

        new Game();
        Snake snake = new Snake(width, height);
        Map map = new Map(width, height);
        Fruit fruit = new Fruit(width, height);

        while (!gameOver) {
            CleanScreen();
            Update(snake, fruit, map, dir, width, height);
            Draw(snake, map, fruit, gameOver);
            Speed = UpdateSpeed(score);
        }

    }

    public static int UpdateSpeed(int score) throws InterruptedException {
        if ((270 - score) > 100) {
            Thread.sleep(270 - score);
            return (int) (score / (1.7));
        } else {
            Thread.sleep(100);
            return 100;
        }
    }

    public static void Update(Snake snake, Fruit fruit, Map map, char dir, int W, int H) {

        if (snake.length > 0) {

            for (int i = snake.length - 1; i >= 1; i--) {
                snake.tailx[i] = snake.tailx[i - 1];
                snake.taily[i] = snake.taily[i - 1];
            }
            snake.tailx[0] = snake.x;
            snake.taily[0] = snake.y;
        }

        if (((dir == 's') && (Predir == 'w')) || ((dir == 'w') && (Predir == 's')) || ((dir == 'a') && (Predir == 'd'))
                || ((dir == 'd') && (Predir == 'a'))) {
            dir = Predir;
        }

        switch (dir) {
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

        Predir = dir;

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
            fruit.reset(W, H, snake);
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

    public static void Draw(Snake s, Map m, Fruit f, boolean Gameover) {
        int W = m.mapWidth;
        int H = m.mapHeight;
        int snake_x = s.x;
        int snake_y = s.y;
        int food_x = f.x;
        int food_y = f.y;
        for (int i = 0; i < W + 2; i++)
            System.out.printf("#");
        System.out.printf("\n");

        if (!Gameover) {

            for (int i = 0; i < H; i++) {
                for (int j = 0; j < W + 2; j++) {
                    if (j == 0) {
                        System.out.printf("#");
                    } else if (j == W + 1) {
                        System.out.printf("#");
                    } else if ((i == food_y) && (j == food_x)) {
                        System.out.printf("*");
                    } else if ((i == snake_y) && (j == snake_x)) {
                        System.out.printf("0");
                    } else if (s.length > 0) {
                        boolean is_tail = false;
                        for (int k = 0; k < s.length; k++) {
                            if ((s.tailx[k] == j) && (s.taily[k] == i)) {
                                System.out.printf("O");
                                is_tail = true;
                                break;
                            }
                        }
                        if (!is_tail)
                            System.out.printf(" ");
                    } else {
                        System.out.printf(" ");
                    }
                }
                System.out.printf("\n");
            }
        } else {
            for (int i = 0; i < H; i++) {
                if (i != H / 2) {
                    for (int j = 0; j < W + 2; j++) {
                        if (j == 0) {
                            System.out.printf("#");
                        } else if (j == W + 1) {
                            System.out.printf("#");
                        } else {
                            System.out.printf(" ");
                        }
                    }
                    System.out.printf("\n");
                } else {

                    System.out.printf("#");
                    for (int j = 0; j < (W - 10) / 2; j++) {
                        System.out.printf(" ");
                    }
                    System.out.printf("GAME OVER!");
                    for (int j = 0; j < (W - 10) / 2; j++) {
                        System.out.printf(" ");
                    }
                    if (W % 2 != 0)
                        System.out.printf(" ");
                    System.out.printf("#\n");
                }
            }

        }

        for (int i = 0; i < W + 2; i++)
            System.out.printf("#");
        System.out.printf("\n");
        System.out.println("Score : " + score);
        System.out.println("Speed : " + Speed + "%");
    }

    public Game() { // constructor
        this.setSize(250, 150);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setAlwaysOnTop(true);
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                dir = e.getKeyChar();
            }
        });
    }
}