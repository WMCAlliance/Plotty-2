package com.daviga404.commands.user;

import com.daviga404.Plotty;
import com.daviga404.commands.PlottyCommand;
import com.daviga404.data.DataManager;
import com.daviga404.data.PlottyPlot;
import com.daviga404.plots.Plot;
import com.daviga404.plots.PlotDeleter;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandPlotClear extends PlottyCommand{
	
	private Plotty plugin;
	
	public CommandPlotClear(Plotty pl){
		super(
		"clear",
		"(clear)(\\s+)(\\d+)",//"(clear)(\\s+)([+-]\\d+)(\\s+)([+-]\\d+)", //clear SPACE int SPACE int SPACE int
		"plotty.clear",
		"/plot clear <id>",
		"Clears a plot."
		);
		this.plugin = pl;
	}

	public boolean execute(Player p, String[] args) {
		if(!plugin.dm.config.clearEnabled){
			p.sendMessage(ChatColor.DARK_RED + "[Plotty] " + ChatColor.RED + "Clearing of plots is prohibited.");
			return true;
		}
		DataManager dm = plugin.getDataManager();
		if(PlotDeleter.isCooling(p.getUniqueId())){
			p.sendMessage(ChatColor.DARK_RED + "[Plotty] " + ChatColor.RED + "You cannot clear another plot for " + dm.config.delCooldown + " seconds.");
			return true;
		}
		PlottyPlot plot = dm.getPlotFromId(Integer.parseInt(args[0]));
		if(plot == null){
			p.sendMessage(plugin.lang.notFound);
			return true;
		}
		UUID owner = dm.getPlotOwner(plot);
		boolean canDelete = false;
		if(owner != null && p.getUniqueId().equals(owner)){
			canDelete = true;
		}else if(p.hasPermission("plotty.clear.others") || p.hasPermission("plotty.*") || p.isOp()){
			canDelete = true;
		}
		if(!canDelete){
			p.sendMessage(plugin.lang.dontOwn);
			return true;
		}
		plugin.getPlotClearer().clearPlot(new Plot(plot.x,plugin.plotHeight,plot.z,p.getWorld()));
		PlotDeleter.addCooldown(p.getUniqueId(), plugin);
		p.sendMessage(plugin.lang.plotCleared);
		return true;
	}
}
