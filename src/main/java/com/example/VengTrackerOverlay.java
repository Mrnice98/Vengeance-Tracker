package com.example;

import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.*;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class VengTrackerOverlay extends Overlay
{

    @Inject
    private VengTrackerConfig config;

    @Inject
    private VengTrackerPlugin plugin;

    @Inject
    private ImageUtil imageUtil;

    @Inject
    private Client client;

    @Inject
    private SpriteManager spriteManager;

    @Inject
    private VengTrackerOverlay()
    {
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {

        for (Player player : client.getPlayers())
        {
            if (plugin.currentlyVenged.contains(Text.sanitize(player.getName())))
            {
                BufferedImage vengIcon = spriteManager.getSprite(SpriteID.SPELL_VENGEANCE_OTHER, 0);
                if (vengIcon != null)
                {
                    renderPlayerOverlay(graphics, player, vengIcon);
                }
            }
        }

        return null;
    }


    private void renderPlayerOverlay(Graphics2D graphics, Player player, BufferedImage vengIcon)
    {
        Point renderPoint;

        if (config.renderMethod() == VengTrackerConfig.RenderMethod.HUG_PLAYER)
        {
            LocalPoint playerLocation = player.getLocalLocation();
            LocalPoint vengLoc = new LocalPoint(playerLocation.getX() + config.XOffset(), playerLocation.getY());
            renderPoint = Perspective.getCanvasImageLocation(client, vengLoc,vengIcon, (getAnchorPoint(player)) + config.YOffset());

        }
        else
        {
            Point point = player.getCanvasImageLocation(vengIcon, getAnchorPoint(player));
            renderPoint = new Point(point.getX() + config.XOffset(), point.getY() + config.YOffset());
        }

        if (client.getPlane() == player.getWorldLocation().getPlane() && renderPoint != null && player.getLocalLocation().isInScene())
        {
            OverlayUtil.renderImageLocation(graphics, renderPoint,  ImageUtil.resizeImage(vengIcon,16 - config.ZOffset(),18 - config.ZOffset()));
        }

    }


    public int getAnchorPoint(Player player)
    {
        int anchorPoint;

        switch (config.anchorPoints())
        {
            case CHEST:
                anchorPoint = player.getLogicalHeight() / 2;
                break;

            case HEAD:
                anchorPoint = player.getLogicalHeight();
                break;

            case FEET:
                anchorPoint = 0;
                break;

            default:
                anchorPoint = 0;
        }

        return anchorPoint;

    }


}
