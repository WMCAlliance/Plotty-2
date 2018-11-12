package com.daviga404.commands.user;

import com.daviga404.Plotty;
import com.daviga404.commands.PlottyCommand;
import com.daviga404.data.DataManager;
import com.daviga404.data.PlottyPlot;
import com.daviga404.plots.PlotFinder;
import com.sk89q.worldguard.bukkit.BukkitPlayer;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CommandPlotInfo extends PlottyCommand{
	private Plotty plugin;
	public CommandPlotInfo(Plotty pl){
		super(
		"info",
		"(info)",
		"plotty.info",
		"/plot info",
		"Displays info about a plot."
		);
		this.plugin = pl;
	}
	public boolean execute(Player p, String[] args){
		Location l = p.getLocation();
		int x = l.getBlockX();
		int z = l.getBlockZ();
		Integer[] corners = PlotFinder.getCornerFromCoords(x, z, plugin.plotSize);
		if(corners.length != 2){
			p.sendMessage(ChatColor.DARK_RED + "[Plotty] " + ChatColor.RED + "You are not standing in a plot.");
			return true;
		}
		DataManager dm = plugin.getDataManager();
		PlottyPlot plot = dm.getPlotFromCoords(corners[0], corners[1]);
		if(plot == null){
			p.sendMessage(ChatColor.DARK_BLUE + "[Plotty] " + ChatColor.BLUE + "This plot is free.");
			return true;
		}
		StringBuilder plotInfo = new StringBuilder();
		plotInfo.append(ChatColor.DARK_BLUE).append("[Plotty] Plot Info:\n");
		plotInfo.append(ChatColor.BLUE).append("- Owner: ").append(ChatColor.AQUA);
		String owner = dm.getPlotOwner(plot);
		if(owner == null){
			plotInfo.append("unknown");
		}else{
			plotInfo.append(owner);
		}
		plotInfo.append("\n").append(ChatColor.BLUE).append("- ID: ").append(ChatColor.AQUA);
		plotInfo.append(plot.id);
		plotInfo.append("\n").append(ChatColor.BLUE).append("- Can you build: ").append(ChatColor.AQUA);
		RegionManager rm = plugin.worldGuard.getRegionManager(p.getWorld());
		boolean canBuild = rm.getApplicableRegions(l).canBuild(new BukkitPlayer(plugin.worldGuard,p));
		if(canBuild){
			plotInfo.append("yes!");
		}else{
			plotInfo.append("no :(");
		}
		plotInfo.append("\n").append(ChatColor.BLUE).append("- Friends: ").append(ChatColor.AQUA);
		String friends="";
		for(String friend : plot.friends){
			friends += friend+", ";
		}
		if(friends == ""){
			friends = "none.";
		}else{
			friends = friends.substring(0,friends.length()-2);
		}
		plotInfo.append(friends);
		plotInfo.append("\n").append(ChatColor.BLUE).append("- Public: ").append(ChatColor.AQUA);
		plotInfo.append(plot.visible ? "yes" : "no");
		p.sendMessage(plotInfo.toString());
		return true;
	}
}
