package com.chrisni.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.chrisni.game.LaserBall;
import com.chrisni.screens.GameScreen;

/**
 * Created by Chris on 5/24/2017.
 */
public class ScoreActor extends Actor {

    private int score;
    final LaserBall game;

    public ScoreActor(final LaserBall game) {
        super();
        this.game = game;
        this.score = 0;
    }


    @Override
    public void draw(Batch batch, float delta) {
        game.font.draw(batch, "Score: " + score, 10, GameScreen.getHeight() - 10);
    }

    public void increase() {
        score++;
    }

    public int getScore() {
        return score;
    }

}
