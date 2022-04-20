package snakegame;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;

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

        // Luodaan objetit
        Snake snake = new Snake(width, height);
        Map map = new Map(width, height);
        Fruit fruit = new Fruit(width, height);

        while (!gameOver) {

            CleanScreen(); // Tyhjätään komentokehote uudelleen piirtoa varten

            Update(snake, fruit, map, direction, width, height); // Päivitetään madon sijainti

            Draw(snake, map, fruit, gameOver); // Piiretään kaikki

            Thread.sleep(100); // Pysäytetään säije 100 millisekunniksi
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

        // Muutetaan madon pään koordinaatteja suunnan perusteella jokaisen
        // päivityskerran yhteydessä
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

        // Jos mato törmää seinään niin piirretään se alkamaan vastakkaiselta seinältä
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
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); // Tyhjätään komentokehote
                                                                              // "cls"-komennolla
    }

    public static void Draw(Snake snake, Map map, Fruit fruit, boolean Gameover) {

        // Annetaan kartan koolle lyhyemmät muuttujanimet
        int W = map.mapWidth;
        int H = map.mapHeight;

        // Annetaan madon koordinaateille lyhyemmät muuttujanimet
        int snake_x = snake.x;
        int snake_y = snake.y;

        // Annetaan ruuan kkoordinaateilleoolle lyhyemmät muuttujanimet
        int food_x = fruit.x;
        int food_y = fruit.y;

        String output_buffer = "";

        // Piiretään kartan yläreuna
        for (int i = 0; i < W + 2; i++) {
            output_buffer += "#";
        }
        output_buffer += "\n";

        if (!Gameover) { // Mikäli peli ei ole päättynyt

            // Käydään läpi jokainen rivi
            for (int i = 0; i < H; i++) {

                // Käydään läpi jokainen sarake
                for (int j = 0; j < W + 2; j++) {

                    if (j == 0) { // Piiretään jokaisen rivin alkuun kartan reunaviiva
                        output_buffer += "#";
                    } else if (j == W + 1) { // Piiretään jokaisen rivin loppuun kartan reunaviiva
                        output_buffer += "#";
                    } else if ((i == food_y) && (j == food_x)) { // Piiretään hedelmä kartaan
                        output_buffer += "*";
                    } else if ((i == snake_y) && (j == snake_x)) { // Piiretään madon pää kartaan
                        output_buffer += "0";
                    } else if (snake.length > 0) { // Jos madon pituus on suurempi kuin 0 niin piiretään madon häntä
                                                   // karttaan
                        boolean is_tail = false;
                        for (int k = 0; k < snake.length; k++) { // Käydään läpi kaikki madon hännän koordinaatit
                            if ((snake.tailx[k] == j) && (snake.taily[k] == i)) { // Jos häntä on tässä kohtaa
                                                                                  // piirretään se
                                output_buffer += "O";
                                is_tail = true;
                                break; // Lopetetaan listan turha läpikäyminen
                            }

                        }
                        if (!is_tail) // Jos häntä ei ole tässä kohdassa piirrä tyhjä
                            output_buffer += " ";
                    } else {
                        output_buffer += " "; // Jos ei kuulu mihinkään muuhun näistä piirrä tyhjä
                    }
                }
                output_buffer += "\n"; // Jokaisen rivin perään lisätään newline-character
            }
        } else { // Jos peli on päättynyt niin piirretään lopetusnäyttö
            for (int i = 0; i < H; i++) { // Käydään läpi jokainen rivi
                if (i != H / 2) { // Jos rivin numero on kahdella jaollinen
                    for (int j = 0; j < W + 2; j++) { // Käydään läpi jokainen sarake
                        if (j == 0) { // Piiretään kartan yläreuna
                            output_buffer += "#";
                        } else if (j == W + 1) { // Piiretään kartan oikea reunaviiva
                            output_buffer += "#";
                        } else {
                            output_buffer += " "; // Tyhjä
                        }
                    }
                    output_buffer += "\n"; // Jokaisen rivin perään lisätään newline-character
                } else {
                    output_buffer += "#"; // Piiretään oikea reunaviiva
                    for (int j = 0; j < (W - 10) / 2; j++) {
                        output_buffer += " ";
                    }
                    output_buffer += "GAME OVER!"; // Piiretään "Game over"-teksti
                    for (int j = 0; j < (W - 10) / 2; j++) {
                        output_buffer += " ";
                    }
                    if (W % 2 != 0) { // Jos sarakkeen jakojäännös ei ole 0 piiretään tyhjä
                        output_buffer += " ";
                    }
                    output_buffer += "#\n"; // Lisätään vielä oikeaan alanurkkaan reunaviiva
                }
            }
        }
        for (int i = 0; i < W + 2; i++) { // Piirretään kartan alareuna
            output_buffer += "#";
        }
        output_buffer += "\nScore: " + score + "\n"; // Piiretään pistetulos kartan alapuollelle

        System.out.print(output_buffer); // Tulostetaan "puskuroitu" teksti
    }

    public Game() {
        // Luodaan JFrame jotta voidaan lukea nappäinpainallukset
        this.setSize(200, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);

        // Lisätään teksti
        JLabel label = new JLabel("Ohjaa matoa wasd-näppäimillä");
        label.setBounds(0, 0, 100, 30);

        this.add(label);

        // Lisätään tapahtumankäsittelijä näppäin painalluksille
        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent event) { // Näppäinpainallus tapahtuma
                direction = event.getKeyChar();
            }
        });
    }
}