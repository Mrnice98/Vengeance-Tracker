package com.example;

import net.runelite.client.config.*;

@ConfigGroup("VengTracker")
public interface VengTrackerConfig extends Config
{

	@ConfigSection(
			name = "General settings",
			description = "General settings",
			position = 0,
			closedByDefault = false
	)
	String generalSettings = "General settings";

	@ConfigSection(
			name = "Advanced settings",
			description = "Advanced settings",
			position = 1,
			closedByDefault = true
	)
	String advancedSettings = "Advanced settings";


	@ConfigItem(
			keyName = "Icon Anchor Point",
			name = "Icon Anchor Point",
			description = "Icon Anchor Point",
			position = 1,
			section = generalSettings
	)
	default AnchorPoints anchorPoints(){return AnchorPoints.CHEST;}

	enum AnchorPoints
	{
		HEAD,
		CHEST,
		FEET;
	}

	@Range(
			min = 0,
			max = 15
	)
	@ConfigItem(
			keyName = "Size Offset",
			name = "Size Offset",
			description = "Size Offset (Increase # to make smaller)",
			position = 2,
			section = generalSettings
	)
	default int ZOffset()
	{
		return 0;
	}

	@Range(
			min = -1000,
			max = 1000
	)
	@ConfigItem(
			keyName = "YOffset",
			name = "Y-Offset",
			description = "Y-Offset",
			position = 3,
			section = generalSettings
	)
	default int YOffset()
	{
		return 0;
	}

	@Range(
			min = -1000,
			max = 1000
	)
	@ConfigItem(
			keyName = "XOffset",
			name = "X-Offset",
			description = "X-Offset",
			position = 4,
			section = generalSettings
	)
	default int XOffset()
	{
		return 0;
	}


	@ConfigItem(
			keyName = "Render Method",
			name = "Render Method",
			description = "Render Method",
			position = 0,
			section = advancedSettings
	)
	default RenderMethod renderMethod(){return RenderMethod.HUG_PLAYER;}

	enum RenderMethod
	{
		HUG_PLAYER,
		ADJUST_VIA_CAM_ANGLE;
	}








}
