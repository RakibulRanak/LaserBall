package com.chrisni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.chrisni.game.LaserBall;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by Chris on 5/27/2017.
 */
public class OptionsScreen implements Screen {

    private final LaserBall game;
    private Stage stage;
    private Skin skin;
    private Preferences prefs;

    public OptionsScreen(final LaserBall laserBall, Stage mStage, Skin mSkin, Preferences mPrefs, final Screen back) {
        this.game = laserBall;
        this.stage = mStage;
        this.skin = mSkin;
        this.prefs = mPrefs;

        final TextButton backButton = new TextButton("Back", skin, "default");
        backButton.setWidth(150f);
        backButton.setHeight(64f);
        backButton.setPosition(150, 100 - 74);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Class<?> c = Class.forName(back.getClass().getName());
                            Constructor<?> cons = c.getConstructor(LaserBall.class, Stage.class, Skin.class, Preferences.class);
                            stage.clear();
                            game.setScreen((Screen) cons.newInstance(game, stage, skin, prefs));
                        } catch (ClassNotFoundException e) {
                            throw new IllegalArgumentException("No such class: " + back.getClass().getName());
                        } catch (NoSuchMethodException e) {
                            throw new IllegalArgumentException("No constructor of type : LaserBall, Stage, Skin, Preferences for " + back.getClass().getName());
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }});
            }
        });

        final ImageButton themeButton = new ImageButton(skin, "theme");
        themeButton.setPosition(150, 200);
        themeButton.setChecked(prefs.getBoolean("themed", false));
        themeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(themeButton.isChecked()) {
                    skin.addRegions(new TextureAtlas(Gdx.files.internal("img/cannon_atlas/chloe_cannon.atlas")));
                    skin.add("ball", new TextureRegion(new Texture("img/ball/kelsey_ball.png")));
                    prefs.putBoolean("themed", true);
                } else {
                    skin.addRegions(new TextureAtlas(Gdx.files.internal("img/cannon_atlas/cannons.atlas")));
                    skin.add("ball", new TextureRegion(new Texture("img/ball/normal_ball.png")));
                    prefs.putBoolean("themed", false);
                }
                prefs.flush();
            }
        });

        final Slider volume = new Slider(0, 1, 1 / 1000f, false, skin, "volume");
        volume.setWidth(150);
        volume.setPosition(150, 500);
        volume.setValue(prefs.getFloat("volume", 1f));

        final Label volumeLabel = new Label(String.format("Vol: %2.1f", volume.getValue() * 100), skin, "title");
        volumeLabel.setPosition(volume.getX(), volume.getY() + 10);

        volume.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                prefs.putFloat("volume", volume.getValue());
                prefs.flush();
                volumeLabel.setText(String.format("Vol: %2.1f", volume.getValue() * 100));
            }
        });

        Image title = new Image(skin.getAtlas().findRegion("label"));
        title.setWidth(170);
        title.setHeight(170);
        title.setPosition(150, 600);

        Label titleLabel = new Label("options", skin, "title");
        titleLabel.setPosition(title.getX() + title.getWidth() / 2, title.getY() + title.getHeight() / 2, Align.center);

        stage.addActor(backButton);
        stage.addActor(themeButton);
        stage.addActor(title);
        stage.addActor(titleLabel);
        stage.addActor(volume);
        stage.addActor(volumeLabel);
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
