package com.chrisni.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;
import com.chrisni.screens.GameScreen;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

/**
 * Created by Chris on 5/21/2017.
 */
public class LaserActor extends Actor implements Pool.Poolable {

    private TextureRegion laser = new TextureRegion(new Texture("img/cannon/laser.png"));
    private final float LASER_W = (58  * GameScreen.cannon_width / 64) / 2;
    private final float LASER_H = 110 * GameScreen.cannon_height / 128;
    private final float SCALE_X = GameScreen.cannon_width / GameScreen.cannon_stationary.getRegionWidth();
    private final float SCALE_Y = GameScreen.cannon_height / GameScreen.cannon_stationary.getRegionHeight();

    private int num, id;

    public LaserActor(int num, int id) {
        super();
        this.num = num;
        this.id = id;
        this.setX(num * GameScreen.cannon_width + LASER_W);
        this.setY(LASER_H);
        this.setWidth(laser.getRegionWidth());
        this.setHeight(laser.getRegionHeight());
    }

    @Override
    public void draw(Batch batch, float alpha) {
        batch.draw(laser,this.getX(),this.getY(),this.getOriginX(),this.getOriginY(),this.getWidth() * SCALE_X,
                this.getHeight() * SCALE_Y,this.getScaleX(), this.getScaleY(),this.getRotation());
    }

    @Override
    public void reset() {
        this.setX(num * GameScreen.cannon_width + LASER_W);
        this.setY(LASER_H);
        this.clearActions();
        this.addAction(scaleTo(1f, 2f, 0.1f));
    }

    public int getNum() {
        return num;
    }

    public float getVel() {
        return (GameScreen.getHeight() - LASER_H * SCALE_Y) / 1f;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != this.getClass()) return false;
        if (o == this) return true;
        LaserActor other = (LaserActor) o;
        return other.id == this.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
