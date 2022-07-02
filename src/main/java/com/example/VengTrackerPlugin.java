package com.example;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.annotations.Varbit;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.OverheadTextChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.party.PartyMember;
import net.runelite.client.party.PartyService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.party.PartyPlugin;
import net.runelite.client.plugins.party.PartyPluginService;
import net.runelite.client.plugins.party.data.PartyData;
import net.runelite.client.ui.overlay.OverlayManager;


import java.util.ArrayList;

@Slf4j
@PluginDescriptor(
		name = "Vengeance Tracker",
		description = "Shows Veng icon next to players who are venganced",
		tags = {"PVM", "Vengeance", "Player status"}
)
@PluginDependency(PartyPlugin.class)
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

	@Inject
	private  PartyPluginService partyPluginService;

	@Inject
	private  PartyService partyService;

	public ArrayList<String> currentlyVenged = new ArrayList<String>();

	@Subscribe
	public void onGraphicChanged(GraphicChanged graphicChanged)
	{
		if (client.getLocalPlayer() != null && graphicChanged.getActor() instanceof Player && !isInPvP())
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
	public void onGameTick(GameTick gameTick)
	{
		if(partyService.isInParty() && !isInPvP())
		{
			for (PartyMember partyMember : partyService.getMembers())
			{
				PartyData partyData = partyPluginService.getPartyData(partyMember.getMemberId());
				String playerName = partyMember.getDisplayName();

				if (partyData.isVengeanceActive() && !currentlyVenged.contains(playerName))
				{
					currentlyVenged.add(playerName);
				}
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

	private boolean isInPvP()
	{
		//0 = not in pvp , 1 = in pvp
		return client.getVarbitValue(Varbits.PVP_SPEC_ORB) == 1;
	}

	@Provides
	VengTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(VengTrackerConfig.class);
	}

	@Override
	protected void startUp() throws Exception
	{
		currentlyVenged.clear();
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		currentlyVenged.clear();
		overlayManager.remove(overlay);
	}




}
