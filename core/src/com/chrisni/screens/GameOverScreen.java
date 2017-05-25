package com.chrisni.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.chrisni.game.LaserBall;

/**
 * Created by Chris on 5/23/2017.
 */
public class GameOverScreen implements Screen {

    private final LaserBall game;
    private final int NUM_HIGHSCORES = 5;
    private int score;

    static OrthographicCamera camera;


    public GameOverScreen(final LaserBall laserBall, int score) {
        this.game = laserBall;
        this.score = score;
        camera = MainMenuScreen.camera;
        setHighScores();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "You lost with a score of " + score + "!!!", 170, 400);
        game.font.draw(game.batch, "High Scores", 170, 350);
        game.font.draw(game.batch, "Tap anywhere to play again!", 170, 100);
        displayHighScores();
        game.batch.end();

        if (Gdx.input.isTouched()) {
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    game.setScreen(new GameScreen(game));
                    dispose();
                }});
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    private void setHighScores() {
        Preferences prefs = MainMenuScreen.prefs;
        int[] scores = new int[NUM_HIGHSCORES + 1];
        for (int i = 0; i < NUM_HIGHSCORES; i++) {
            String key = "highscore" + i;
            scores[i] = prefs.getInteger(key, -1);
        }
        scores[NUM_HIGHSCORES] = score;
        insertionSort(scores);
        for (int i = 0; i < NUM_HIGHSCORES; i++) {
            String key = "highscore" + i;
            int curr = scores[i];
            if (curr >= 0) {
                prefs.putInteger(key, curr);
            }
        }
        prefs.flush();
    }

    private void displayHighScores() {
        Preferences prefs = MainMenuScreen.prefs;
        int[] scores = new int[NUM_HIGHSCORES + 1];
        for (int i = 0; i < NUM_HIGHSCORES; i++) {
            String key = "highscore" + i;
            scores[i] = prefs.getInteger(key, -1);
            String currScore = (i + 1) + ". " + ((scores[i] >= 0) ? scores[i] : "--");
            game.font.draw(game.batch, currScore, 170, 325 - 25 * i);
        }
    }

    private void insertionSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            int temp = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] < temp) {
                arr[j+1] = arr[j];
                j--;
            }
            arr[j+1] = temp;
        }
    }
}
