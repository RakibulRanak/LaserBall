package com.chrisni;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

public class LaserBall extends ApplicationAdapter {

	class CannonActor extends Actor {

		int num;
		float actorX, actorY, time;
		boolean started = false;
		final float TIME_OPEN = 0.25f;
		final Sound sound = Gdx.audio.newSound(Gdx.files.internal("sound\\laser_sound.mp3"));

		CannonActor(int num) {
			super();
			this.num = num;
			actorX = num * CANNON_WIDTH;
			actorY = 0;
			time = 0;
			setBounds(actorX, actorY, CANNON_WIDTH, CANNON_HEIGHT);
			addListener(new InputListener(){
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					((CannonActor)event.getTarget()).started = true;
					((CannonActor)event.getTarget()).time = 0;
					cannonTouched[((CannonActor)event.getTarget()).num] = true;
					sound.play(1f);
					return true;
				}
			});
		}

		@Override
		public void draw(Batch batch, float alpha) {
			if (started) {
				batch.draw(CANNON_PREPARE, actorX, actorY, CANNON_WIDTH, CANNON_HEIGHT);
			} else {
				batch.draw(CANNON_STATIONARY, actorX, actorY, CANNON_WIDTH, CANNON_HEIGHT);
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

	private final static int NUM_CANNON = 5;
	private final static int WIDTH = 800;
	private final static int HEIGHT = 1024;

	static TextureRegion CANNON_STATIONARY;
	private static TextureRegion CANNON_PREPARE;
	private final static float Y_FRAC = 1 / 10f;
	static float CANNON_WIDTH;
	static float CANNON_HEIGHT;

	Stage stage;
	boolean[] cannonTouched = new boolean[NUM_CANNON];

	@Override
	public void create () {
		stage = new Stage();
		Gdx.graphics.setWindowedMode(WIDTH, HEIGHT);
		CANNON_STATIONARY = new TextureRegion(new Texture("img/cannon/cannon_stationary.png"));
		CANNON_PREPARE = new TextureRegion(new Texture("img/cannon/cannon_prepare.png"));
		CANNON_WIDTH = CANNON_STATIONARY.getRegionWidth() * Gdx.graphics.getWidth() / (float) NUM_CANNON / CANNON_STATIONARY.getRegionWidth();
		CANNON_HEIGHT = CANNON_STATIONARY.getRegionHeight() * Gdx.graphics.getHeight() * Y_FRAC / CANNON_STATIONARY.getRegionHeight();
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
				currLaser.addAction(sequence(parallel(scaleTo(1f, 2f, 0.25f), moveTo(currLaser.getX(), Gdx.graphics.getHeight(), 2f)), run(
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

	public static int getWidth() {
		return WIDTH;
	}

	public static int getHeight() {
		return HEIGHT;
	}
}
