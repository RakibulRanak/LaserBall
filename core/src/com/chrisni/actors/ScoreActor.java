package com.chrisni.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.chrisni.game.LaserBall;

/**
 * Created by Chris on 5/24/2017.
 */
public class ScoreActor extends Actor {

    private int score;
    final LaserBall game;

    public ScoreActor(final LaserBall game) {
        super();
        this.game = game;
    }


    @Override
    public void draw(Batch batch, float delta) {
        game.font.draw(batch, "Score: " + score, 10, Gdx.graphics.getHeight() - 10);
    }

    public void increase() {
        score++;
    }

    public int getScore() {
        return score;
    }

}
