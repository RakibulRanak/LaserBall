package com.chrisni.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.chrisni.game.LaserBall;

/**
 * Created by Chris on 5/23/2017.
 */
public class MainMenuScreen implements Screen {

    private final LaserBall game;
    private Skin skin; //TODO: Use VerticalGroup
    private Stage stage;
    private Preferences prefs;

    public MainMenuScreen(final LaserBall laserBall, Stage mStage, Skin mSkin, Preferences mPrefs) {
        this.game = laserBall;
        this.stage = mStage;
        this.skin = mSkin;
        this.prefs = mPrefs;
        final TextButton playButton = new TextButton("Play", skin, "default");
        playButton.setWidth(150f);
        playButton.setHeight(64f);
        playButton.setPosition((GameScreen.getWidth() - playButton.getWidth()) / 2, (GameScreen.getHeight() - playButton.getHeight()) / 2);
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        stage.clear();
                        game.setScreen(new GameScreen(game, stage, skin, prefs));
                        dispose();
                    }});
            }
        });

        final TextButton optionButton = new TextButton("Options", skin, "default");
        optionButton.setWidth(150f);
        optionButton.setHeight(64f);
        optionButton.setPosition((GameScreen.getWidth() - playButton.getWidth()) / 2, ((GameScreen.getHeight() - playButton.getHeight()) / 2) - 74);
        optionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        stage.clear();
                        game.setScreen(new OptionsScreen(game, stage, skin, prefs, MainMenuScreen.this));
                        dispose();
                    }});
            }
        });

        final TextButton quitButton = new TextButton("Quit", skin, "default");
        quitButton.setWidth(150f);
        quitButton.setHeight(64f);
        quitButton.setPosition((GameScreen.getWidth() - playButton.getWidth()) / 2, ((GameScreen.getHeight() - playButton.getHeight()) / 2) - 74 * 2);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        dispose();
                        Gdx.app.exit();
                    }});
            }
        });

        Image title = new Image(skin.getAtlas().findRegion("label"));
        title.setWidth(170);
        title.setHeight(170);
        title.setPosition(150, 600);

        Label titleLabel = new Label("laserball", skin, "title");
        titleLabel.setPosition(title.getX() + title.getWidth() / 2, title.getY() + title.getHeight() / 2, Align.center);

        stage.addActor(title);
        stage.addActor(titleLabel);
        stage.addActor(playButton);
        stage.addActor(optionButton);
        stage.addActor(quitButton);
    }

    public MainMenuScreen(final LaserBall laserBall) {
        this(laserBall, new Stage(new FitViewport(GameScreen.getWidth(), GameScreen.getHeight(), new OrthographicCamera())),
                new Skin(Gdx.files.internal("skins/menu.json")), Gdx.app.getPreferences("LaserBall"));
        if (!prefs.getBoolean("themed", false)) {
            skin.addRegions(new TextureAtlas(Gdx.files.internal("img/cannon_atlas/cannons.atlas")));
            skin.add("ball", new TextureRegion(new Texture("img/ball/normal_ball.png")));
        } else {
            skin.addRegions(new TextureAtlas(Gdx.files.internal("img/cannon_atlas/chloe_cannon.atlas")));
            skin.add("ball", new TextureRegion(new Texture("img/ball/kelsey_ball.png")));
        }
        skin.add("titleImg", new TextureRegion(new Texture("img/chloe_cannon/ann_bg.png")));
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();

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
    public void dispose() {

    }
}
