package com.example;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.swing.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
	name = "<html><font color=\"#4ade80\">[Pure]</font> Cef Example</html>"
)
public class ExampleCefPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ExampleCefConfig config;
	@Inject
	private ClientToolbar clientToolbar;

	private final boolean REMOTE_DEBUGGING = true;
	private final boolean DEV_MODE = true;
	private NavigationButton navButton;
	private JFrame cefFrame;
	private CefPanel cefPanel;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Pure Cef Example started!");
		if (cefPanel == null) {
			cefPanel = CefPanel.createPanel(false, false, REMOTE_DEBUGGING, DEV_MODE);
		}
		final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "icon.png");
		navButton = NavigationButton.builder()
				.tooltip("Pure Cef Example")
				.priority(8)
				.icon(icon)
				.panel(cefPanel)
				.build();
		clientToolbar.addNavigation(navButton);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Pure Cef Example stopped!");
		clientToolbar.removeNavigation(navButton);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{

	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged) {
		if (configChanged.getKey().equalsIgnoreCase("open")) {
			if (configChanged.getNewValue().equalsIgnoreCase("true")) {
				if (cefFrame == null) {
					cefFrame = CefPanel.createFrame(false,false, REMOTE_DEBUGGING, DEV_MODE);
				} else {
					cefFrame.pack();
					cefFrame.setVisible(true);
				}
			} else {
				if (cefFrame != null) {
					cefFrame.setVisible(false);
				}
			}
		}
	}

	@Provides
    ExampleCefConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ExampleCefConfig.class);
	}
}
