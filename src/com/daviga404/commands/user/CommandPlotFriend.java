package com.daviga404.commands.user;

import com.daviga404.Plotty;
import com.daviga404.commands.PlottyCommand;
import com.daviga404.data.DataManager;
import com.daviga404.data.PlottyPlayer;
import com.daviga404.data.PlottyPlot;
import com.daviga404.plots.PlotRegion;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandPlotFriend extends PlottyCommand {

	private final Plotty plugin;

	public CommandPlotFriend(Plotty plugin) {
		super(
				"friend",
				"(friend)(\\s+)(\\w+)(\\s+)(\\d+)",
				"plotty.friend",
				"/plot friend <name> <id>",
				"Adds a friend to a plot.");
		this.plugin = plugin;
	}

	@Override
	public boolean execute(Player p, String[] args) {
		//Check if player has plot by id
		DataManager dm = plugin.getDataManager();
		PlottyPlayer pp = dm.getPlayer(p.getUniqueId());
		if (pp == null) {
			p.sendMessage(plugin.lang.noPlots);
			return true;
		}
		PlottyPlot plot = null;
		for (PlottyPlot pplot : pp.plots) {
			if (pplot.id == Integer.parseInt(args[1])) {
				plot = pplot;
			}
		}
		if (plot == null) {
			p.sendMessage(plugin.lang.notFound);
			return true;
		}
		//Check if friend is already friended
		for (String s : plot.friends) {
			if (args[0].equalsIgnoreCase(s)) {
				p.sendMessage(plugin.lang.alreadyFriend);
				return true;
			}
		}
		//Check if friend is owner
		UUID plotOwner = dm.getPlotOwner(plot);
		if (plotOwner != null && plotOwner.equals(p.getUniqueId()) && p.getName().equalsIgnoreCase(args[0])) {
			p.sendMessage(ChatColor.DARK_RED + "[Plotty] " + ChatColor.RED + "Why would you want to add yourself as a friend?");
			return true;
		}
		//Add as a friend in plots.json
		dm.addFriend(plot, p.getUniqueId(), args[0]);
		//Add as owner (WG)
		if (!PlotRegion.addFriend(plot.id, p, plugin.getOfflinePlayer(args[0]).getUniqueId(), Bukkit.getWorld(plot.world))) {
			p.sendMessage(ChatColor.DARK_RED + "[Plotty] " + ChatColor.RED + "Unexpected error: region not found.");
			return true;
		}
		p.sendMessage(plugin.lang.friendAdded);
		return true;
	}
}
