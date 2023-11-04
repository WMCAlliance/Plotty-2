package com.daviga404.commands.user;

import com.daviga404.Plotty;
import com.daviga404.commands.PlottyCommand;
import com.daviga404.data.DataManager;
import com.daviga404.data.PlottyPlot;
import com.daviga404.plots.PlotFinder;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CommandPlotInfo extends PlottyCommand {

	private final Plotty plugin;

	public CommandPlotInfo(Plotty pl) {
		super(
				"info",
				"(info)",
				"plotty.info",
				"/plot info",
				"Displays info about a plot."
		);
		this.plugin = pl;
	}

	public boolean canBuild(Player p, Location l) {
		RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
		com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(l);
		return query.testState(loc, WorldGuardPlugin.inst().wrapPlayer(p), Flags.BUILD);
	}

	@Override
	public boolean execute(Player p, String[] args) {
		Location l = p.getLocation();
		int x = l.getBlockX();
		int z = l.getBlockZ();
		Integer[] corners = PlotFinder.getCornerFromCoords(x, z, plugin.plotSize);
		if (corners.length != 2) {
			p.sendMessage(ChatColor.DARK_RED + "[Plotty] " + ChatColor.RED + "You are not standing in a plot.");
			return true;
		}
		DataManager dm = plugin.getDataManager();
		PlottyPlot plot = dm.getPlotFromCoords(corners[0], corners[1]);
		if (plot == null) {
			p.sendMessage(ChatColor.DARK_BLUE + "[Plotty] " + ChatColor.BLUE + "This plot is free.");
			return true;
		}
		StringBuilder plotInfo = new StringBuilder();
		plotInfo.append(ChatColor.DARK_BLUE).append("[Plotty] Plot Info:\n");
		plotInfo.append(ChatColor.BLUE).append("- Owner: ").append(ChatColor.AQUA);
		UUID owner = dm.getPlotOwner(plot);
		String plotOwnerName = "Unknown";
		if (owner != null) {
			plotOwnerName = plugin.getServer().getOfflinePlayer(owner).getName();
			if (plotOwnerName == null)
				plotOwnerName = dm.getPlayer(owner).name;
		}
		plotInfo.append(plotOwnerName);
		plotInfo.append("\n").append(ChatColor.BLUE).append("- ID: ").append(ChatColor.AQUA);
		plotInfo.append(plot.id);
		plotInfo.append("\n").append(ChatColor.BLUE).append("- Can you build: ").append(ChatColor.AQUA);
		boolean canBuild = this.canBuild(p, l);
		if (canBuild) {
			plotInfo.append("yes!");
		} else {
			plotInfo.append("no :(");
		}
		plotInfo.append("\n").append(ChatColor.BLUE).append("- Friends: ").append(ChatColor.AQUA);
		String friends = "";
		for (String friend : plot.friends) {
			friends += friend + ", ";
		}
		if (friends.isEmpty()) {
			friends = "none.";
		} else {
			friends = friends.substring(0, friends.length() - 2);
		}
		plotInfo.append(friends);
		plotInfo.append("\n").append(ChatColor.BLUE).append("- Public: ").append(ChatColor.AQUA);
		plotInfo.append(plot.visible ? "yes" : "no");
		p.sendMessage(plotInfo.toString());
		return true;
	}
}
