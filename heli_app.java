package io.github.Til33n;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.Color;

import java.net.HttpURLConnection;
import java.util.Random;
import java.net.URL;
import java.io.IOException;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class heli_app extends ApplicationAdapter {
    private SpriteBatch batch;                                                                      // sprites are images, characters, backgrounds ...
    private Texture background;
    private Texture game_over;
    //ShapeRenderer shapeRenderer;
    private Texture[] helis;                                                                         // bird info
    int State = 0;
    float heliY = 500;
    float velocity = 0;
    int gameState = 0;
    int execute = 0;
    float gravity = 1;
    Circle heliCircle;

    int score = 0;
    int scoringTube = 0;
    String user = "Tilen";
    BitmapFont font;

    Texture topTube;                                                                                // tubes
    Texture bottomTube;
    float gap = 400;
    float maxTubeOffset;                                                                            // gape between two opposed tubes
    Random randomGenerator;
    float tubeVelocity = 2 ;                                                                        // speed of game or the tubes
    int numberOfTubes = 4;
    float[] tubeX = new float[numberOfTubes];
    float[] tubeOffset = new float[numberOfTubes];
    float tubeDistance;
    Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;

    @Override
    public void create() {                                                                          // method when the app is started and run
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        game_over = new Texture("game_over.png");
        //shapeRenderer = new ShapeRenderer();
        heliCircle = new Circle();

        font = new BitmapFont();                                                                    // SCORE DISPLAY ON SCREEN
        font.setColor(Color.RED);
        font.getData().setScale(10);

        helis = new Texture[2];
        helis[0] = new Texture("bird.png");
        helis[1] = new Texture("bird_2.png");
        //birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
        randomGenerator = new Random();                                                             // offset of tubes pairs between them
        tubeDistance = Gdx.graphics.getWidth() * 3 / 4;
        topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];
        start_game();
    }

public static void send() throws IOException {

    Gdx.app.log("Response", "1");
    URL url = new URL("http://93.103.156.225:5000");  // server (back-end) ip-adress
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    Gdx.app.log("Response", "2");
    conn.setReadTimeout(500000 /* milliseconds */);
    conn.setConnectTimeout(1000000 /* milliseconds */);
    //conn.setRequestMethod("POST");
    //conn.setDoInput(true);
    //conn.setRequestProperty("Accept", "application/json");
    //conn.connect();	// Starts the query
    Gdx.app.log("Response", "3");
    String response = conn.getResponseMessage();
    Gdx.app.log("Response", String.valueOf(response));
    Gdx.app.log("Response", "4");
    // Convert the InputStream into a string

}

    public void start_game() {
        heliY = Gdx.graphics.getHeight() / 2 - helis[0].getHeight() / 2;
        for (int i = 0; i < numberOfTubes; i++) {

            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * tubeDistance;
            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();
        }
    }

    @Override
    public void render() {                                                                          //method executing continuously

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        if (gameState == 1) {

            if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {

                score++;
                Gdx.app.log("Score", String.valueOf(score));

                if (scoringTube < numberOfTubes - 1) {
                    scoringTube++;
                } else {
                    scoringTube = 0;
                }
            }

            if (Gdx.input.justTouched()) {
                velocity = -15;
            }


            for (int i = 0; i < numberOfTubes; i++) {

                if (tubeX[i] < - topTube.getWidth()) {

                    tubeX[i] += numberOfTubes * tubeDistance;
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

                } else {

                    tubeX[i] = tubeX[i] - tubeVelocity;

                }

                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

                topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], topTube.getWidth(), topTube.getHeight());

            }

            if (heliY > 0) {
                velocity = velocity + gravity;
                heliY -= velocity;
            } else {

                gameState = 2;
            }

        } else if (gameState == 0){

            if (Gdx.input.justTouched()) {
                gameState = 1;
            }


        } else if (gameState == 2) /// WE SEND USER SCORE TO SERVER DATABASE HERE
                {
                if(execute == 0) {
                    try {
                        send();   /// WE SEND USER SCORE TO SERVER DATABASE HERE
                    } catch (IOException e) {
                    }
                }
                execute = 1;

            batch.draw(game_over, Gdx.graphics.getWidth() / 2 - game_over.getWidth() / 2, Gdx.graphics.getHeight() / 2 - game_over.getHeight() / 2);

            if (Gdx.input.justTouched()) {
                gameState = 1;
                start_game();
                score = 0;
                scoringTube = 0;
                velocity = 0;
                execute = 0;
            }
        }

        if (State == 0) {
            State = 1;
        } else {
            State = 0;
        }

        batch.draw(helis[State], Gdx.graphics.getWidth() / 2 - helis[State].getWidth() / 2, heliY);
        font.draw(batch, String.valueOf(score), 100, 200);
        heliCircle.set(Gdx.graphics.getWidth() /2, heliY + helis[State].getHeight() / 2, helis[State].getWidth() / 2);      /// RED CIRCLE ON TOP OF BIRD TO TEST HIT DETECTION
        batch.end();

        //shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        //shapeRenderer.setColor(Color.RED);
        //shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);

        for (int i = 0; i < numberOfTubes; i++) {

            //shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i] , topTube.getWidth(), topTube.getHeight());
            //shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], topTube.getWidth(), topTube.getHeight());

                if (Intersector.overlaps(heliCircle, topTubeRectangles[i]))
                {
                    Gdx.app.log("Collision", "Detected !");
                    gameState = 2;
                }
                if (Intersector.overlaps(heliCircle, bottomTubeRectangles[i]))
                {
                    Gdx.app.log("Collision", "Detected !");
                    gameState = 2;
                }
        }
        //shapeRenderer.end();
    }
}

// game states
// 0 - game start
// 1 - game playing
// 2 - game over
