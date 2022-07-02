package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("VengTracker")
public interface VengTrackerConfig extends Config
{
	@Range(
			min = 0,
			max = 15
	)
	@ConfigItem(
			keyName = "Size Offset",
			name = "Size Offset",
			description = "Size Offset (Increase # to make smaller)",
			position = 2
	)
	default int ZOffset()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "YOffset",
			name = "Y-Offset",
			description = "Y-Offset",
			position = 3
	)
	default int YOffset()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "XOffset",
			name = "X-Offset",
			description = "X-Offset",
			position = 4
	)
	default int XOffset()
	{
		return 0;
	}

}
