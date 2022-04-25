package snakegame;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class Game extends JFrame {

    // Kulkusuunta
    static char direction = '0';        
    static char previous_direction = '0';

    // Pelin tila
    static boolean gameOver = false;
    static boolean quit = false;
    static boolean newGame = true;

    // Pelaaja
    static int score = 0;
    static String playerName = null;

    // Alustetaan pelilauta
    static int width = 40;
    static int height = 16;
    static int speed = 1;
            
    // Historiatiedot
    static String[] records = null;

    public static void main(String... arg) throws InterruptedException {
        
        // pelaaja
        Scanner in = new Scanner(System.in);
        System.out.println("PLAYER NAME:");
        playerName = in.nextLine();
        in.close();

        // vaikka konsolipeli, niin laitetaan kuuntelija JFramelle
        new Game();

        // Luodaan objetit
        Map map = new Map(width, height);
        Fruit fruit = null;
        Snake snake = null;

        while (true) {

            // lopetetaan peli-istunto
            if(quit) {
                break;
            }

            // alustetaan uusi peli
            if (newGame) {
                snake = new Snake(width, height);
                fruit = new Fruit(width, height);
                newGame = false;
            }

            cleanScreen(); // Tyhjätään komentokehote uudelleen piirtoa varten

            update(snake, fruit, map); // Päivitetään madon sijainti

            draw(snake, map, fruit); // Piiretään kaikki            

            Thread.sleep(100); // Pysäytetään säije 100 millisekunniksi
        }

        cleanScreen(); 
    }

    public static void update(Snake snake, Fruit fruit, Map map) {

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
    
    public static void cleanScreen() {
        
        try {
        
            // Tyhjätään komentokehote "cls"-komennolla
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e); 
        }   
    }

    public static void draw(Snake snake, Map map, Fruit fruit) {

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

        /**  JUST FOR TESTING
        if (snake.length > 0) {
            gameOver = true;
        }
        */
        if (!gameOver) { // Mikäli peli ei ole päättynyt

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

            for (int i = 0; i < W + 2; i++) { // Piirretään kartan alareuna
                output_buffer += "#";
            }
            output_buffer += "\nScore: " + score + "\n"; // Piiretään pistetulos kartan alapuollelle
    
            System.out.print(output_buffer); // Tulostetaan "puskuroitu" teksti

        } else {
            drawEndScreen();
        } 
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
               
                char keyChar = event.getKeyChar();
                
                // uusi peli? 
                if (keyChar == 'y' || keyChar == 'Y') {
                    gameOver = false;
                    newGame = true;
                    score = 0;
                    records = null;
                // lopetaanko?
                } else if (keyChar == 'q' || keyChar == 'Q') {
                    quit = true;
                } else {
                    direction = keyChar;
                }
            }
        });
    }

    private static void drawEndScreen() {
        
        cleanScreen();

        if(records == null) {
            records = handleTopUsers();
        }

        System.out.println("*** GAME OVER! ***");
        System.out.println("\nPLAYER " + playerName.toUpperCase() + " SCORE IS " + score);
        System.out.println("\nTOP PLAYERS:");
        
        for (int i = 3; i > 0; i--) {
            System.out.println("\t" + records[i]);
        }

        System.out.println("\n\nTRY AGAIN OR QUIT (Y/Q)?");
    }

    private static  String[] handleTopUsers() {
        
        // Luetaan kolme parasta tulosta 
        File file = new File("records.data");
        
        String[] records = new String[4];

        try {

            Scanner sc = new Scanner(file);

            records[0] = sc.nextLine();
            records[1] = sc.nextLine();
            records[2] = sc.nextLine();

            sc.close();
        } 
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        
        records[3] = score + " " + playerName;

        Arrays.sort(records);

        records[0] = "";
        
        try {
            PrintWriter writer = new PrintWriter(new File("records.data"));

            for (int i = 3; i > 0; i--) {
                writer.println(records[i]);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            
            throw new RuntimeException(e);
        }
        return records;
    }
}