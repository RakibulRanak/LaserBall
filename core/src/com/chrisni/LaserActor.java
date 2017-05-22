package com.chrisni;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

/**
 * Created by Chris on 5/21/2017.
 */
public class LaserActor extends Actor {

    private TextureRegion laser = new TextureRegion(new Texture("img/cannon/laser.png"));
    private final float LASER_W = (58  * LaserBall.CANNON_WIDTH / 64) / 2;
    private final float LASER_H = 110 * LaserBall.CANNON_HEIGHT / 128;
    private final float SCALE_X = LaserBall.CANNON_WIDTH / LaserBall.CANNON_STATIONARY.getRegionWidth();
    private final float SCALE_Y = LaserBall.CANNON_HEIGHT / LaserBall.CANNON_STATIONARY.getRegionHeight();
    private int num;

    public LaserActor(int num) {
        super();
        this.num = num;
        this.setX(num * LaserBall.CANNON_WIDTH + LASER_W);
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
    public void act(float delta) {
        super.act(delta);
        if (this.getY() == Gdx.graphics.getHeight()) {
            this.addAction(Actions.removeActor());
        }
    }

}
