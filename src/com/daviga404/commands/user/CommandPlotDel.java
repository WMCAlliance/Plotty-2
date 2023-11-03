package com.daviga404.commands.user;

import com.daviga404.Plotty;
import com.daviga404.commands.PlottyCommand;
import com.daviga404.data.DataManager;
import com.daviga404.data.PlottyPlot;
import com.daviga404.plots.Plot;
import com.daviga404.plots.PlotDeleter;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandPlotDel extends PlottyCommand{
	private Plotty plugin;
	public CommandPlotDel(Plotty pl){
		super(
		"del",
		"(del)(\\s+)(\\d+)",
		"plotty.del",
		"/plot del <id>",
		"Deletes a plot."
		);
		this.plugin = pl;
	}
	
	public boolean execute(Player p, String[] args){
		if(PlotDeleter.isCooling(p.getUniqueId())){
			p.sendMessage(ChatColor.DARK_RED + "[Plotty] " + ChatColor.RED + "You cannot delete another plot for "+plugin.dm.config.delCooldown+" seconds.");
			return true;
		}
		DataManager dm = plugin.getDataManager();
		PlottyPlot plot = dm.getPlotFromId(Integer.parseInt(args[0]));
		if(plot == null){
			p.sendMessage(plugin.lang.notFound);
			return true;
		}
		boolean canDelete = false;
		if(dm.getPlotOwner(plot) != null && dm.getPlotOwner(plot).equals(p.getUniqueId())){
			canDelete = true;
		}else if(p.hasPermission("plotty.del.others") || p.hasPermission("plotty.*") || p.isOp()){
			canDelete = true;
		}
		if(!canDelete){
			p.sendMessage(plugin.lang.dontOwn);
			return true;
		}
		UUID owner = dm.getPlotOwner(plot);
		if(!dm.removePlot(plot.id, owner)){
			p.sendMessage(ChatColor.DARK_RED + "[Plotty] " + ChatColor.RED + "Error while removing plot (contact daviga404 on Bukkit to report)");
			return true;
		}
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager rm = container.get(BukkitAdapter.adapt(p.getWorld()));
		if(rm.hasRegion("plot_"+p.getName().toLowerCase()+"_"+plot.id)){
			rm.removeRegion("plot_"+p.getName().toLowerCase()+"_"+plot.id);
		}else{
			p.sendMessage(ChatColor.DARK_RED + "[Plotty] " + ChatColor.RED + "Region not found - Plotty will continue. (please report this to an admin)");
		}
		if(dm.config.clearOnDelete){
			plugin.getPlotClearer().clearPlot(new Plot(plot.x,plugin.plotHeight,plot.z,p.getWorld()));
		}
		PlotDeleter.addCooldown(p.getUniqueId(), plugin);
		p.sendMessage(plugin.lang.plotDeleted);
		return true;
	}
}
