package com.chrisni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.chrisni.actors.LaserPool;
import com.chrisni.game.LaserBall;

public class GameScreen implements Screen {

	class CannonActor extends Actor {

		int num;
		float actorX, actorY, time;
		boolean started = false;
		final float TIME_OPEN = 0.25f;
		final Sound sound = Gdx.audio.newSound(Gdx.files.internal("sound\\laser_sound.mp3"));

		CannonActor(int num) {
			super();
			this.num = num;
			actorX = num * cannon_width;
			actorY = 0;
			time = 0;
			setBounds(actorX, actorY, cannon_width, cannon_height);
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
				batch.draw(cannon_prepare, actorX, actorY, cannon_width, cannon_height);
			} else {
				batch.draw(cannon_stationary, actorX, actorY, cannon_width, cannon_height);
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
	private final static int WIDTH = 480;
	private final static int HEIGHT = 800;

	public static TextureRegion cannon_stationary;
	public static TextureRegion cannon_prepare;
	public static float cannon_width;
	public static float cannon_height;

	private final static float Y_FRAC = 1 / 10f;
	private final com.chrisni.actors.LaserPool[] LASERS = new com.chrisni.actors.LaserPool[NUM_CANNON];
	private final Array<com.chrisni.actors.LaserActor> activeLasers = new Array<com.chrisni.actors.LaserActor>();

	private Stage stage;
	private Batch batch;
	private FitViewport viewp;
	private com.chrisni.actors.LaserActor curr;
	private boolean[] cannonTouched = new boolean[NUM_CANNON];
	private final LaserBall game;


	public GameScreen(final LaserBall game) {
		this.game = game;
		viewp = new FitViewport(WIDTH, HEIGHT, MainMenuScreen.camera);
		batch = new SpriteBatch();
//		stage = new Stage(viewp, batch); TODO: fix this
		stage = new Stage();
		Gdx.graphics.setWindowedMode(WIDTH, HEIGHT);
		cannon_stationary = new TextureRegion(new Texture("img/cannon/cannon_stationary.png"));
		cannon_prepare = new TextureRegion(new Texture("img/cannon/cannon_prepare.png"));
		cannon_width = cannon_stationary.getRegionWidth() * Gdx.graphics.getWidth() / (float) NUM_CANNON / cannon_stationary.getRegionWidth();
		cannon_height = cannon_stationary.getRegionHeight() * Gdx.graphics.getHeight() * Y_FRAC / cannon_stationary.getRegionHeight();
		for (int i = 0; i < NUM_CANNON; i++) {
			stage.addActor(new CannonActor(i));
			LASERS[i] = new LaserPool(i, NUM_CANNON);
		}
		Gdx.graphics.setResizable(false);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(255, 255, 255, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		for (int i = 0; i < NUM_CANNON; i++) {
			if (cannonTouched[i]) {
				curr = LASERS[i].obtain();
				activeLasers.add(curr);
				stage.addActor(curr);
				cannonTouched[i] = false;
			}
		}
		stage.act(Gdx.graphics.getDeltaTime());

		int len = activeLasers.size;
		for (int i = len; --i >= 0;) {
			curr = activeLasers.get(i);
			if (curr.getY() >= Gdx.graphics.getHeight()) {
				curr.remove();
				LASERS[curr.getNum()].free(curr);
				activeLasers.removeIndex(i);
			}
		}

		stage.draw();
	}

	@Override
	public void show() {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

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
