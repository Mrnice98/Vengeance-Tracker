package com.example;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
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
import net.runelite.client.util.Text;

import javax.inject.Inject;
import javax.swing.*;
import java.util.ArrayList;

@Slf4j
@PluginDescriptor(
		name = "Vengeance Tracker",
		description = "Shows Veng icon next to players who are venged and more (works with party plugin)",
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

	@Inject
	private ConfigManager configManager;

	@Inject
	private ChatMessageManager chatMessageManager;

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
				currentlyVenged.add(Text.sanitize(player.getName()));
			}
		}
	}

	@Subscribe
	public void onMenuOpened(MenuOpened event)
	{
		for (MenuEntry entry : event.getMenuEntries())
		{
			if (entry.getPlayer() != null && currentlyVenged.contains(Text.sanitize(entry.getPlayer().getName())) && entry.getTarget().contains("Vengeance Other") && config.indicateVenged())
			{
				entry.setTarget(entry.getTarget() + " <col=ffffff> (V) <col=ffff00>");
			}
		}
	}

	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{

		MenuEntry entry = event.getMenuEntry();
		if (entry.getPlayer() != null && currentlyVenged.contains(Text.sanitize(entry.getPlayer().getName())) && entry.getTarget().contains("Vengeance Other") && config.dePrioVenged())
		{
			entry.setDeprioritized(true);
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

				if (partyData.isVengeanceActive() && !currentlyVenged.contains(Text.sanitize(playerName)))
				{
					currentlyVenged.add(Text.sanitize(playerName));
				}

				if (!partyData.isVengeanceActive() && currentlyVenged.contains(Text.sanitize(playerName)))
				{
					currentlyVenged.remove(Text.sanitize(playerName));
				}
			}
		}

		if (currentlyVenged.contains(Text.sanitize(client.getLocalPlayer().getName())) && client.getVarbitValue(Varbits.VENGEANCE_ACTIVE) == 0)
		{
			currentlyVenged.remove(Text.sanitize(client.getLocalPlayer().getName()));
		}

	}


	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{


		if (configManager.getConfiguration("party","statusOverlayVeng").equals("true") && gameStateChanged.getGameState() == GameState.LOGGED_IN && config.remindToDisable())
		{
			SwingUtilities.invokeLater(()->
			{
				String[] options = { "Yes", "No", "No & Don't show again" };
				int option = JOptionPane.showOptionDialog(null, "Disable Party Vengeance (Vengeance Tracker handles party also)", "Vengeance Tracker & Party Vengeance are both enabled", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);

				if (options[option].equals("Yes"))
				{
					configManager.setConfiguration("party","statusOverlayVeng","false");
				}

				if (options[option].equals("No & Don't show again"))
				{
					configManager.setConfiguration("VengTracker","remindToDisable","false");
				}

			});

			chatMessageManager.queue(QueuedMessage.builder().type(ChatMessageType.GAMEMESSAGE).runeLiteFormattedMessage("<col=ff6600>Please Disable 'Show Vengance' in the Party Plugin Config <col=ffff00>").build());
		}

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
			currentlyVenged.remove(Text.sanitize(actor.getName()));
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
