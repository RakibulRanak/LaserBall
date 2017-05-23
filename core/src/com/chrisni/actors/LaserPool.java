package com.chrisni.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Pool;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;

/**
 * Created by Chris on 5/23/2017.
 */
public class LaserPool extends Pool<LaserActor> {

    private int num;
    private int count;

    public LaserPool(int num, int cannons) {
        super(cannons);
        this.num = num;
    }

    @Override
    protected LaserActor newObject() {
        LaserActor currLaser = new LaserActor(num);
        currLaser.addAction(sequence(parallel(scaleTo(1f, 2f, 0.25f), moveTo(currLaser.getX(), Gdx.graphics.getHeight(), 2f)), run(
                new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Laser offscreen.");
                    }
                }
        )));
        return currLaser;
    }
}
