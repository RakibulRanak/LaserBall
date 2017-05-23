package com.chrisni.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.chrisni.game.LaserBall;
import com.chrisni.screens.GameScreen;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = GameScreen.getWidth();
		config.height = GameScreen.getHeight();
		new LwjglApplication(new LaserBall(), config);
	}
}
