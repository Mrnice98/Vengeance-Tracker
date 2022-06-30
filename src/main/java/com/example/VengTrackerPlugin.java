package com.example;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.ArrayList;

@Slf4j
@PluginDescriptor(
		name = "Vengeance Tracker",
		description = "Shows Veng icon next to players who are venganced",
		tags = {"PVM", "Vengeance", "Player status"}
)
public class VengTrackerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private VengTrackerConfig config;

	@Inject
	private VengTrackerOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	public ArrayList<String> currentlyVenged = new ArrayList<String>();

	@Subscribe
	public void onGraphicChanged(GraphicChanged graphicChanged)
	{
		if (client.getLocalPlayer() != null && graphicChanged.getActor() instanceof Player)
		{
			Player player = (Player) graphicChanged.getActor();
			boolean playerVenged = currentlyVenged.contains(player.getName());

			if ((player.getGraphic() == 725 || player.getGraphic() == 726) && !playerVenged)
			{
				currentlyVenged.add(player.getName());
			}

		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.HOPPING || gameStateChanged.getGameState() == GameState.LOGGING_IN)
		{
			currentlyVenged.clear();
		}
	}

	@Subscribe
	public void onOverheadTextChanged(OverheadTextChanged event)
	{
		Actor actor = event.getActor();
		if (actor instanceof Player && actor.getName() != null && currentlyVenged.contains(actor.getName()) && actor.getOverheadText().equals("Taste vengeance!"))
		{
			currentlyVenged.remove(actor.getName());
		}
	}


	@Provides
	VengTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(VengTrackerConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
		currentlyVenged.clear();
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
		currentlyVenged.clear();
		overlayManager.remove(overlay);
	}




}
