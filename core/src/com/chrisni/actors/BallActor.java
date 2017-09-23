package com.chrisni.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.chrisni.screens.GameScreen;

/**
 * Created by Chris on 5/23/2017.
 */
public class BallActor extends Actor {

    private TextureRegion ball;
    private int id;

    public BallActor(int id) {
        super();
        this.id = id;

    }

    @Override
    public void draw(Batch batch, float delta) {
        batch.draw(ball, this.getX(), this.getY());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != this.getClass()) return false;
        if (o == this) return true;
        BallActor other = (BallActor) o;
        return other.id == this.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public void setFirstRegion(TextureRegion region) {
        if (ball == null) {
            this.ball = region;
            this.setWidth(ball.getRegionWidth());
            this.setHeight(ball.getRegionHeight());
            this.setX(ball.getRegionWidth() + MathUtils.random((float) GameScreen.getWidth() - 4 * ball.getRegionWidth()));
            this.setY(GameScreen.getHeight() - ball.getRegionHeight() / 2);
        }
    }

}
