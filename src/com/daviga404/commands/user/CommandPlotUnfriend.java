package com.daviga404.commands.user;

import com.daviga404.Plotty;
import com.daviga404.commands.PlottyCommand;
import com.daviga404.data.DataManager;
import com.daviga404.data.PlottyPlayer;
import com.daviga404.data.PlottyPlot;
import com.daviga404.plots.PlotRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandPlotUnfriend extends PlottyCommand{
	private Plotty plugin;
	public CommandPlotUnfriend(Plotty pl){
		super(
		"unfriend",
		"(unfriend)(\\s+)(\\w+)(\\s+)(\\d+)",
		"plotty.friend",
		"/plot unfriend <name> <id>",
		"Removes a friend from your plot."
		);
		this.plugin = pl;
	}
	public boolean execute(Player p, String[] args){
		//Check if player has plot
		DataManager dm = plugin.getDataManager();
		PlottyPlayer pp = dm.getPlayer(p);
		PlottyPlot plot=null;
		for(PlottyPlot pplot : pp.plots){
			if(pplot.id == Integer.parseInt(args[1])){
				plot = pplot;
			}
		}
		if(plot == null){p.sendMessage(plugin.lang.notFound);return true;}
		//Check that friend exists
		boolean exists = false;
		for(String s : plot.friends){
			if(args[0].equalsIgnoreCase(s)){
				exists = true;
			}
		}
		if(!exists){p.sendMessage(plugin.lang.friendNotFound);return true;}
		//Remove friend from plots.json
		dm.removeFriend(plot,p.getUniqueId(),args[0]);
		//Remove friend from WG
		if(!PlotRegion.removeFriend(plot.id,p.getName(),plugin.getOfflinePlayer(args[0]).getUniqueId(),Bukkit.getWorld(plot.world))){
			p.sendMessage(ChatColor.DARK_RED + "[Plotty] " + ChatColor.RED + "Unexpected error: region not found.");
			return true;
		}
		p.sendMessage(plugin.lang.friendRemoved);
		return true;
	}
}
