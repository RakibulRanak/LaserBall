package com.chrisni;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

/**
 * Created by Chris on 5/21/2017.
 */
public class LaserActor extends Actor {
    Texture laser = new Texture("img/cannon/laser.png");
    int num;

    public LaserActor(int num) {
        super();
        this.num = num;
        this.setX(num * 128 + 58);
        this.setY(110);
        this.setWidth(laser.getWidth());
        this.setHeight(laser.getHeight());
    }

    @Override
    public void draw(Batch batch, float alpha) {
        batch.draw(laser,this.getX(),this.getY(),this.getOriginX(),this.getOriginY(),this.getWidth(),
                this.getHeight(),this.getScaleX(), this.getScaleY(),this.getRotation(),0,0,
                laser.getWidth(),laser.getHeight(),false,false);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (this.getY() == Gdx.graphics.getHeight()) {
            this.addAction(Actions.removeActor());
        }
    }

}
