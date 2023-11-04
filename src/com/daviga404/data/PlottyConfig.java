package com.daviga404.data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlottyConfig {

	public double version;
	public int plotSize;
	public int plotHeight;
	public int maxPlots;
	public String baseBlock;
	public String surfaceBlock;
	public int delCooldown;
	public boolean clearOnDelete;
	public boolean clearEnabled = false;
	public String[] worlds;
	public boolean centertp;
	public boolean publicByDefault;
	public boolean enableTnt;
	public double voteDelay = 24.0;
	public UUID[] playerGrantNotify = new UUID[]{};
	@Deprecated
	public PlottyPlayer[] players;
	public Map<UUID, PlottyPlayer> playerPlots = new HashMap<>();
	public boolean enableEco = false;
	public double plotCost = 0.0;
	public Map<String, String> flags = new HashMap<String, String>();
}
