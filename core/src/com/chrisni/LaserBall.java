package com.chrisni;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;


import java.util.Iterator;

public class LaserBall extends ApplicationAdapter {

	private class CannonActor extends Actor {
		Texture cannon_stationary = new Texture("img/cannon/cannon_stationary.png");
		Texture cannon_prepare = new Texture("img/cannon/cannon_prepare.png");
		int num;
		float actorX, actorY, time;
		boolean started = false;
		final float TIME_OPEN = 0.5f;

		CannonActor(int num) {
			super();
			this.num = num;
			actorX = num * cannon_stationary.getWidth();
			actorY = 0;
			time = 0;
			setBounds(actorX, actorY, cannon_stationary.getWidth(), cannon_stationary.getHeight());
			addListener(new InputListener(){
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					((CannonActor)event.getTarget()).started = true;
					((CannonActor)event.getTarget()).time = 0;
					cannonTouched[((CannonActor)event.getTarget()).num] = true;
					return true;
				}
			});
		}

		@Override
		public void draw(Batch batch, float alpha) {
			if (started) {
				batch.draw(cannon_prepare, actorX, actorY);
			} else {
				batch.draw(cannon_stationary, actorX, actorY);
			}
		}

		@Override
		public void act(float delta) {
			super.act(delta);
			if (time >= TIME_OPEN) {
				started = false;
				time = 0;
			} else if (started) {
				time += delta;
			}
		}
	}

	Stage stage;
	final int NUM_CANNON = 5;
	boolean[] cannonTouched = new boolean[NUM_CANNON];

	@Override
	public void create () {
		stage = new Stage();
		Gdx.graphics.setWindowedMode(640, 720);
		for (int i = 0; i < NUM_CANNON; i++) {
			stage.addActor(new CannonActor(i));
		}
		Gdx.graphics.setResizable(false);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(255, 255, 255, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		for (int i = 0; i < NUM_CANNON; i++) {
			if (cannonTouched[i]) {
				LaserActor currLaser = new LaserActor(i);
				currLaser.addAction(sequence(parallel(scaleTo(1f, 1.5f, 0.25f), moveTo(currLaser.getX(), Gdx.graphics.getHeight(), 2f)), run(
						new Runnable() {
							@Override
							public void run() {
								System.out.println("Laser offscreen.");
							}
						}
				)));
				stage.addActor(currLaser);
				cannonTouched[i] = false;
			}
		}
		for (Actor actor: stage.getActors()) {

		}
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}
	
	@Override
	public void dispose () {
		stage.dispose();
	}
}
