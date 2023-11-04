package com.daviga404.data;

import java.util.UUID;

public class PlottyPlayer {

	public String name;
	public UUID uuid;
	public int maxPlots;
	public int grantedPlots = 0;
	public long lastVoted = 0;
	public PlottyPlot[] plots;
}
