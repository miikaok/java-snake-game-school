package snakegame;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Scanner;

public class Highscores {

    public static String RecordsPath = "records.txt";

    /**
     * Kirjoittaa pelaajan pisteet tekstitiedostoon
     * 
     * @param name  Pelaajan nimi
     * @param score Pelaajan saamat pisteet
     */
    public static void WriteHighscores(String name, int score) {
        String line = name + ":" + score + "\n";
        try {
            FileOutputStream stream = new FileOutputStream(RecordsPath, true);
            byte[] byteArr = line.getBytes(); // converting string into byte array
            stream.write(byteArr);
            stream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lukee huippupisteet tekstitiedostosta
     * 
     * @return
     */
    public static HashMap<String, Integer> ReadHighscores() {

        HashMap<String, Integer> highscores = new HashMap<String, Integer>();

        try {
            FileInputStream stream = new FileInputStream(RecordsPath);
            Scanner sc = new Scanner(stream);

            while (sc.hasNextLine()) {
                String[] highscore = ParseHighscores(sc.nextLine());
                highscores.put(highscore[0], Integer.parseInt(highscore[1]));
            }
            sc.close();
        } catch (FileNotFoundException e) {
        }

        return highscores;
    }

    private static String[] ParseHighscores(String highscore) {
        return highscore.split(":", 2);
    }
}
