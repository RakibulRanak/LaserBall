package com.chrisni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.chrisni.actors.BallActor;
import com.chrisni.actors.LaserActor;
import com.chrisni.actors.LaserPool;
import com.chrisni.actors.ScoreActor;
import com.chrisni.game.LaserBall;

import java.util.Iterator;

public class GameScreen implements Screen {

	class CannonActor extends Actor {

		int num;
		float actorX, actorY, time;
		boolean started = false;
		final float TIME_OPEN = 0.2f;
		final float TIME_DISABLED = 0.3f;
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
					if (((CannonActor) event.getTarget()).time >= TIME_OPEN + TIME_DISABLED ||
							!((CannonActor) event.getTarget()).started ) {
						((CannonActor) event.getTarget()).started = true;
						((CannonActor) event.getTarget()).time = 0;
						cannonTouched[((CannonActor) event.getTarget()).num] = true;
						sound.play(1f);
					}
					return true;
				}
			});
		}

		@Override
		public void draw(Batch batch, float alpha) {
			if (time >= TIME_OPEN) {
				batch.draw(cannon_exhausted, actorX, actorY, cannon_width, cannon_height);
			} else if (time > 0) {
				batch.draw(cannon_prepare, actorX, actorY, cannon_width, cannon_height);
			} else {
				batch.draw(cannon_stationary, actorX, actorY, cannon_width, cannon_height);
			}
		}

		@Override
		public void act(float delta) {
			super.act(delta);
			if (time >= TIME_OPEN + TIME_DISABLED) {
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
	public static TextureRegion cannon_exhausted;
	public static float cannon_width;
	public static float cannon_height;

	private final static float Y_FRAC = 1 / 10f;
	private final static float DT = 1 / 60f;
	private final com.chrisni.actors.LaserPool[] LASERS = new com.chrisni.actors.LaserPool[NUM_CANNON];
	private ObjectSet<Body> deadLasers = new ObjectSet<Body>();

	private Stage stage; //TODO: get rid of static variables
	private Skin skin;
	private Preferences prefs;
	private boolean[] cannonTouched = new boolean[NUM_CANNON];
	private final LaserBall game;

	private World world;
	private Array<Body> balls;
	private Array<Body> lasers;
	private ScoreActor score;
	private BallActor ballActor;
	private float accumulator;
	private final short LASER_MASK = 0x1;
	private final short BALL_MASK = 0x1 << 2;
	private final short WALL_MASK = 0x1 << 3;

	private Sound laserBallSound;

	public static final float PIXELS_TO_METERS = 100f;


	public GameScreen(final LaserBall game, Stage stage, Skin skin, Preferences prefs) {
		this.game = game;
		laserBallSound = Gdx.audio.newSound(Gdx.files.internal("sound\\collision_sound.mp3"));
		score = new ScoreActor(game);
		lasers = new Array<Body>(Body.class);
		balls = new Array<Body>(Body.class);
		this.stage = stage;
		this.skin = skin;
		this.prefs = prefs;
		if (skin.has("titleImg", TextureRegion.class)) {
			Image titleImage = new Image(skin.getRegion("titleImg"));
			stage.addActor(titleImage);
		}
		stage.addActor(score);
		cannon_stationary = skin.getRegion("cannon_stationary");
		cannon_prepare = skin.getRegion("cannon_prepare");
		cannon_exhausted = skin.getRegion("cannon_exhausted");
		cannon_width = cannon_stationary.getRegionWidth() * WIDTH / (float) NUM_CANNON / cannon_stationary.getRegionWidth();
		cannon_height = cannon_stationary.getRegionHeight() * HEIGHT * Y_FRAC / cannon_stationary.getRegionHeight();
		for (int i = 0; i < NUM_CANNON; i++) {
			stage.addActor(new CannonActor(i));
			LASERS[i] = new LaserPool(i, NUM_CANNON);
		}
		initPhysics();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		for (int i = 0; i < NUM_CANNON; i++) {
			if (cannonTouched[i]) {
				LaserActor curr = LASERS[i].obtain();
				laserInit(curr);
				stage.addActor(curr);
				cannonTouched[i] = false;
			}
		}
		stepPhysics(delta);
		stage.act(Gdx.graphics.getDeltaTime());

		Iterator<Body> ballIterator = balls.iterator();

		while(ballIterator.hasNext()) {
			Body body = ballIterator.next();
			if (body.getPosition().y <= (cannon_height - 10 )/ PIXELS_TO_METERS) {
				ballIterator.remove();
				ballActor = (BallActor) body.getUserData();
				ballActor.remove();
				world.destroyBody(body);
			}
		}
		if (balls.size == 0) {
			stage.clear();
			Gdx.app.postRunnable(new Runnable() {
				@Override
				public void run() {
					prefs.putInteger("gameScore", score.getScore());
					prefs.flush();
					game.setScreen(new GameOverScreen(game, stage, skin, prefs));}
			});
			this.dispose();
			return;
		}

		Iterator<Body> laserIterator = deadLasers.iterator();
		while (laserIterator.hasNext()) {
			Body body = laserIterator.next();
			LaserActor curr = (LaserActor) body.getUserData();
			world.destroyBody(body);
			curr.remove();
			LASERS[curr.getNum()].free(curr);
			lasers.removeValue(body, false);
			laserIterator.remove();
		}
		stage.draw();
	}

	@Override
	public void show() {

	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
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
		world.dispose();
		laserBallSound.dispose();
	}

	public static int getWidth() {
		return WIDTH;
	}

	public static int getHeight() {
		return HEIGHT;
	}

	private void initPhysics() {
		world = new World(new Vector2(0, -1f), true);

		ballInit();
		ballInit();

		wallInit(new BodyDef(), true, false);
		wallInit(new BodyDef(), false, true);
		wallInit(new BodyDef(), true, true);


		world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				if ((contact.getFixtureA().getBody().getUserData() instanceof LaserActor &&
						contact.getFixtureB().getBody().getUserData() instanceof BallActor) ||
						(contact.getFixtureA().getBody().getUserData() instanceof BallActor &&
								contact.getFixtureB().getBody().getUserData() instanceof LaserActor)) {
					laserBallSound.play(1f);
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
				if (contact.getFixtureA().getBody().getUserData() instanceof LaserActor) {
					deadLasers.add(contact.getFixtureA().getBody());
					if (contact.getFixtureB().getBody().getUserData() instanceof BallActor) {
						score.increase();
						if (score.getScore() % 10 == 0) Gdx.app.postRunnable(new Runnable() {
							@Override
							public void run() {
								ballInit();
							}
						});
					}
				} else if (contact.getFixtureB().getBody().getUserData() instanceof LaserActor) {
					deadLasers.add(contact.getFixtureB().getBody());
					if (contact.getFixtureA().getBody().getUserData() instanceof BallActor) {
						score.increase();
						if (score.getScore() % 10 == 0) Gdx.app.postRunnable(new Runnable() {
							@Override
							public void run() {
								ballInit();
							}
						});
					}
				}
			}
		});

	}

	private void wallInit(BodyDef wall, boolean vert, boolean top) {
		wall.type = BodyDef.BodyType.StaticBody;
		float originX = (top) ? WIDTH / PIXELS_TO_METERS : 0;
		float originY = (top) ? HEIGHT / PIXELS_TO_METERS : 0;
		wall.position.set(originX, originY);
		FixtureDef wallDef = new FixtureDef();
		EdgeShape edgeShape = new EdgeShape();
		int factor = (top) ? -1 : 1;
		float x = (vert) ? 0 : factor * WIDTH / PIXELS_TO_METERS;
		float y = (vert) ? factor * HEIGHT / PIXELS_TO_METERS : 0;
		edgeShape.set(0, 0, x, y);
		wallDef.shape = edgeShape;
		wallDef.filter.categoryBits = WALL_MASK;
		wallDef.restitution = 0f;
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
			if (body.getLinearVelocity().len() >= 2 * HEIGHT / PIXELS_TO_METERS) {
				body.setLinearVelocity(body.getLinearVelocity().limit(2 * HEIGHT / PIXELS_TO_METERS));
			}
		}
		for (Body body : lasers) {
			LaserActor laserActor = (LaserActor) body.getUserData();
			laserActor.setPosition(body.getPosition().x * PIXELS_TO_METERS - laserActor.getWidth() / 2, body.getPosition().y * PIXELS_TO_METERS - laserActor.getHeight() / 2);
		}
	}

	private void laserInit(LaserActor laser) {
		laser.setFirstRegion(skin.getRegion("laser"));
		BodyDef laserDef = new BodyDef();
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
		laserFixtureDef.density = 100f;
		laserFixtureDef.restitution = 0f;
		laserFixtureDef.filter.categoryBits = LASER_MASK;
		laserFixtureDef.filter.maskBits = BALL_MASK | WALL_MASK;

		laserBody.createFixture(laserFixtureDef);

		laserShape.dispose();

		lasers.add(laserBody);
	}

	private int count = 0;

	private void ballInit() {
//		ballActor = new BallActor(score.getScore() % 10);
		ballActor = new BallActor(count);
		ballActor.setFirstRegion(skin.getRegion("ball"));
		count++;
		stage.addActor(ballActor);
		BodyDef ballDef = new BodyDef();
		ballDef.type = BodyDef.BodyType.DynamicBody;
		ballDef.position.set((ballActor.getX() + ballActor.getWidth() / 2) / PIXELS_TO_METERS, (ballActor.getY() + ballActor.getHeight() / 2) / PIXELS_TO_METERS);
		ballDef.linearVelocity.add(new Vector2(MathUtils.randomSign() * 2f, 0));
		Body ball = world.createBody(ballDef);

		ball.setUserData(ballActor);

		CircleShape ballShape = new CircleShape();
		ballShape.setRadius(ballActor.getHeight() / 2 / PIXELS_TO_METERS);

		FixtureDef ballFixtureDef = new FixtureDef();
		ballFixtureDef.shape = ballShape;
		ballFixtureDef.density = 0.01f;
		ballFixtureDef.restitution = 1f;
		ballFixtureDef.filter.categoryBits = BALL_MASK;
		ballFixtureDef.filter.maskBits = BALL_MASK | LASER_MASK | WALL_MASK;

		ball.createFixture(ballFixtureDef);
		ballShape.dispose();

		balls.add(ball);
	}
}
