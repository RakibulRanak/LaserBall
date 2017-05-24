package com.chrisni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.chrisni.actors.BallActor;
import com.chrisni.actors.LaserActor;
import com.chrisni.actors.LaserPool;
import com.chrisni.actors.ScoreActor;
import com.chrisni.game.LaserBall;

import java.util.Arrays;

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
	private final static float DT = 1 / 60f;
	private final com.chrisni.actors.LaserPool[] LASERS = new com.chrisni.actors.LaserPool[NUM_CANNON];
	private final Array<Body> deadLasers = new Array<Body>();

	private Stage stage;
	private Batch batch;
	private FitViewport viewp;
	private com.chrisni.actors.LaserActor curr;
	private boolean[] cannonTouched = new boolean[NUM_CANNON];
	private final LaserBall game;

	private World world;
	private Array<Body> balls;
	private Array<Body> lasers;
	private ScoreActor score;
	private float accumulator;
	private final short LASER_MASK = 0x1;
	private final short BALL_MASK = 0x1 << 2;
	private final short WALL_MASK = 0x1 << 3;

	public static final float PIXELS_TO_METERS = 100f;


	public GameScreen(final LaserBall game) {
		this.game = game;
		score = new ScoreActor(game);
		lasers = new Array<Body>(Body.class);
		balls = new Array<Body>(Body.class);
//		viewp = new FitViewport(WIDTH, HEIGHT, MainMenuScreen.camera);
//		batch = new SpriteBatch();
//		stage = new Stage(viewp, batch); TODO: fix this
		stage = new Stage();
		stage.addActor(score);
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
		initPhysics();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(255, 255, 255, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		for (int i = 0; i < NUM_CANNON; i++) {
			if (cannonTouched[i]) {
				curr = LASERS[i].obtain();
				stage.addActor(curr);
				laserInit(curr);
				cannonTouched[i] = false;
			}
		}
		stepPhysics(delta);
		stage.act(Gdx.graphics.getDeltaTime());

		for (Body body: balls) {
			if (body.getPosition().y <= 0) {
				game.setScreen(new GameOverScreen(game, score.getScore()));
				this.dispose();
			}
		}

			for (Body body: deadLasers) {
			curr = (LaserActor) body.getUserData();
			curr.remove();
			LASERS[curr.getNum()].free(curr);
			lasers.removeValue(body, false);
			world.destroyBody(body);
		}
		deadLasers.clear();
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
		world.dispose();
	}

	public static int getWidth() {
		return WIDTH;
	}

	public static int getHeight() {
		return HEIGHT;
	}

	private void initPhysics() {
		BallActor ballActor = new BallActor(0);
		stage.addActor(ballActor);
		world = new World(new Vector2(0, -1f), true);
		BodyDef ballDef = new BodyDef();
		ballDef.type = BodyDef.BodyType.DynamicBody;
		ballDef.position.set((ballActor.getX() + ballActor.getWidth() / 2) / PIXELS_TO_METERS, (ballActor.getY() + ballActor.getHeight() / 2) / PIXELS_TO_METERS);
		ballDef.linearVelocity.add(new Vector2(1f, 0));
		Body ball = world.createBody(ballDef);

		ball.setUserData(ballActor);

		CircleShape ballShape = new CircleShape();
		ballShape.setRadius(ballActor.getHeight() / 2 / PIXELS_TO_METERS);

		FixtureDef ballFixtureDef = new FixtureDef();
		ballFixtureDef.shape = ballShape;
		ballFixtureDef.density = 0.75f;
		ballFixtureDef.restitution = 1.5f;
		ballFixtureDef.filter.categoryBits = BALL_MASK;
		ballFixtureDef.filter.maskBits = BALL_MASK | LASER_MASK | WALL_MASK;

		ball.createFixture(ballFixtureDef);
		ballShape.dispose();

		balls.add(ball);

//		initWall(new BodyDef(), false, false);
		initWall(new BodyDef(), true, false);
		initWall(new BodyDef(), false, true);
		initWall(new BodyDef(), true, true);


		world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				if (contact.getFixtureA().getBody().getUserData() instanceof LaserActor) {
					deadLasers.add(contact.getFixtureA().getBody());
					if (contact.getFixtureA().getBody().getUserData() instanceof BallActor
							||contact.getFixtureB().getBody().getUserData() instanceof BallActor) {
						score.increase();
					}
				} else if (contact.getFixtureB().getBody().getUserData() instanceof LaserActor) {
					deadLasers.add(contact.getFixtureB().getBody());
					if (contact.getFixtureA().getBody().getUserData() instanceof BallActor
							||contact.getFixtureB().getBody().getUserData() instanceof BallActor) {
						score.increase();
					}
				}
			}

			@Override
			public void endContact(Contact contact) {

			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {

			}
		});

	}

	private void initWall(BodyDef wall, boolean vert, boolean top) {
		wall.type = BodyDef.BodyType.StaticBody;
		float originX = (top) ? Gdx.graphics.getWidth() / PIXELS_TO_METERS : 0;
		float originY = (top) ? Gdx.graphics.getHeight() / PIXELS_TO_METERS : 0;
		wall.position.set(originX, originY);
		FixtureDef wallDef = new FixtureDef();
		EdgeShape edgeShape = new EdgeShape();
		int factor = (top) ? -1 : 1;
		float x = (vert) ? 0 : factor * Gdx.graphics.getWidth() / PIXELS_TO_METERS;
		float y = (vert) ? factor * Gdx.graphics.getHeight() / PIXELS_TO_METERS : 0;
		edgeShape.set(0, 0, x, y);
		wallDef.shape = edgeShape;
		wallDef.filter.categoryBits = WALL_MASK;
		wallDef.restitution = 0.25f;
		Body wallBody = world.createBody(wall);
		wallBody.createFixture(wallDef);
		edgeShape.dispose();
	}

	private void stepPhysics(float delta) {
		accumulator += delta;
		while (accumulator >= DT) {
			world.step(DT, 6, 2);
			accumulator -= DT;
		}
		for (Body body : balls) {
			BallActor ballActor = (BallActor) body.getUserData();
			ballActor.setPosition(body.getPosition().x * PIXELS_TO_METERS - ballActor.getWidth() / 2, body.getPosition().y * PIXELS_TO_METERS - ballActor.getHeight() / 2);
			ballActor.setRotation((float)Math.toDegrees(body.getAngle()));
		}
		for (Body body : lasers) {
			LaserActor laserActor = (LaserActor) body.getUserData();
			laserActor.setPosition(body.getPosition().x * PIXELS_TO_METERS - laserActor.getWidth() / 2, body.getPosition().y * PIXELS_TO_METERS - laserActor.getHeight() / 2);
		}
	}

	private void laserInit(LaserActor laser) {
		BodyDef laserDef = new BodyDef();
		laserDef.type = BodyDef.BodyType.DynamicBody;
		laserDef.type = BodyDef.BodyType.DynamicBody;
		laserDef.position.set((laser.getX() + laser.getWidth() / 2) / PIXELS_TO_METERS, (laser.getY() + laser.getHeight() / 2) / PIXELS_TO_METERS);
		laserDef.linearVelocity.add(new Vector2(0, laser.getVel() / PIXELS_TO_METERS));
		laserDef.gravityScale = 0;
		Body laserBody = world.createBody(laserDef);
		laserBody.setUserData(laser);

		PolygonShape laserShape = new PolygonShape();
		laserShape.setAsBox(laser.getWidth() / PIXELS_TO_METERS, laser.getHeight() / PIXELS_TO_METERS);

		FixtureDef laserFixtureDef = new FixtureDef();
		laserFixtureDef.shape = laserShape;
		laserFixtureDef.density = 2f;
		laserFixtureDef.restitution = 1f;
		laserFixtureDef.filter.categoryBits = LASER_MASK;
		laserFixtureDef.filter.maskBits = BALL_MASK | WALL_MASK;

		laserBody.createFixture(laserFixtureDef);

		laserShape.dispose();

		lasers.add(laserBody);
	}
}
