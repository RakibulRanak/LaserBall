package com.chrisni.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.chrisni.game.LaserBall;

/**
 * Created by Chris on 5/23/2017.
 */
public class GameOverScreen implements Screen {

    private final LaserBall game;
    private int score;

    static OrthographicCamera camera;


    public GameOverScreen(final LaserBall laserBall, int score) {
        this.game = laserBall;
        this.score = score;
        camera = MainMenuScreen.camera;
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
        game.font.draw(game.batch, "Tap anywhere to play again!", 170, 100);
        game.batch.end();

        if (Gdx.input.isTouched()) {
            game.setScreen(new GameScreen(game));
            dispose();
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
}
