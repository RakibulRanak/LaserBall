package com.chrisni.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.chrisni.screens.GameScreen;

/**
 * Created by Chris on 5/23/2017.
 */
public class BallActor extends Actor {

    private TextureRegion ball = new TextureRegion(new Texture("img/ball/normal_ball.png"));
    private int id;
    private final float SCALE_X = GameScreen.cannon_width / GameScreen.cannon_stationary.getRegionWidth();
    private final float SCALE_Y = GameScreen.cannon_height / GameScreen.cannon_stationary.getRegionHeight();

    public BallActor(int id) {
        super();
        this.id = id;
        this.setWidth(ball.getRegionWidth());
        this.setHeight(ball.getRegionHeight());
        this.setX(ball.getRegionWidth() / 2);
        this.setY(Gdx.graphics.getHeight() - this.getHeight() / 2);
//        this.setHeight(ball.getRegionHeight() * SCALE_Y);
//        this.setWidth(ball.getRegionWidth() * SCALE_X);
//        this.setX(this.getWidth() / 2);
//        this.setY(Gdx.graphics.getHeight() - this.getHeight() / 2);
    }

    @Override
    public void draw(Batch batch, float delta) {
//        batch.draw(ball, this.getX(), this.getY(), this.getWidth(), this.getHeight());
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

}
