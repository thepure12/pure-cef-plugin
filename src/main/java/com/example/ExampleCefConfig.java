package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("exampleCEF")
public interface ExampleCefConfig extends Config
{
	@ConfigItem(
		keyName = "open",
		name = "Pop Out UI",
		description = "Open UI in new window"
	)
	default boolean greeting()
	{
		return false;
	}
}
