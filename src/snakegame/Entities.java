package snakegame;

class Snake {

    int x, y, length;
    int[] tailx, taily;

    /**
     * Muodostin madolle
     * 
     * @param width  Pelilaudan leveys
     * @param height Pelilaudan korkeus
     */
    Snake(int width, int height) {

        x = width / 2;
        y = height / 2;

        tailx = new int[width * height];
        taily = new int[width * height];
        length = 0;
    }

    // Lisää madolle pituutta
    void eatfood() {
        this.length++;
    }
}

class Map {

    int mapHeight;
    int mapWidth;

    /**
     * Muodostin pelilaudalle
     * 
     * @param width  Pelilaudan leveys
     * @param height Pelilaudan korkeus
     */
    Map(int width, int height) {
        mapHeight = height;
        mapWidth = width;
    }
}

class Fruit {

    int x;
    int y;

    /**
     * Metodi generoi uuden hedelmä uuteen sijaintiin.
     * 
     * @param width  Pelilaudan leveys
     * @param height Pelilaudan korkeus
     * @param snake  Mato
     */
    void reset(int width, int height, Snake snake) {

        this.x = (int) (Math.random() * width + 1);
        this.y = (int) (Math.random() * height);

        if ((this.x == snake.x) && (this.y == snake.y)) {
            snake.eatfood();
            reset(width, height, snake);
        }

        if (snake.length > 0) {

            boolean overlapsFood = false;

            // Tarkistetaan onko madon koordinaateissa hedelmä
            for (int i = 0; i < snake.length; i++) {
                if ((this.x == snake.tailx[i]) && (this.y == snake.taily[i]))
                    overlapsFood = true;
            }

            // Jos mato koskee hedelmään, syödään se ja generoidaan uusi
            if (overlapsFood) {
                snake.eatfood();
                reset(width, height, snake);
            }
        }
    }

    /**
     * Muodostin hedelmälle
     * 
     * @param width  Pelilaudan leveys
     * @param height Pelilaudan korkeus
     */
    Fruit(int width, int height) {
        this.x = (int) (Math.random() * width + 1);
        this.y = (int) (Math.random() * height);
    }

}