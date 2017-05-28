package com.chrisni.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
        themeButton.setChecked(skin.has("titleImg", TextureRegion.class));
        themeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(themeButton.isChecked()) {
                    skin.addRegions(new TextureAtlas(Gdx.files.internal("img/cannon_atlas/chloe_cannon.atlas")));
                    skin.add("ball", new TextureRegion(new Texture("img/ball/kelsey_ball.png")));
                    skin.add("titleImg", new TextureRegion(new Texture("img/chloe_cannon/ann_bg.png")));
                } else {
                    skin.addRegions(new TextureAtlas(Gdx.files.internal("img/cannon_atlas/cannons.atlas")));
                    skin.add("ball", new TextureRegion(new Texture("img/ball/normal_ball.png")));
                    skin.remove("titleImg", TextureRegion.class);
                }
            }
        });

        stage.addActor(backButton);
        stage.addActor(themeButton);
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
