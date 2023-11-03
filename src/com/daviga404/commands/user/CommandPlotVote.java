package com.daviga404.commands.user;

import com.daviga404.Plotty;
import com.daviga404.commands.PlottyCommand;
import com.daviga404.data.DataManager;
import com.daviga404.data.PlottyPlayer;
import com.daviga404.data.PlottyPlot;
import com.daviga404.plots.PlotFinder;
import java.util.Calendar;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CommandPlotVote extends PlottyCommand{
	private Plotty plugin;
	public CommandPlotVote(Plotty pl){
		super(
		"vote",
		"(vote)",
		"plotty.vote",
		"/plot vote",
		"Upvotes the plot you are standing in."
		);
		this.plugin = pl;
	}
	public boolean execute(Player p, String[] args){
		DataManager dm = plugin.getDataManager();
		PlottyPlayer player = dm.getPlayerOrCreate(p);
		if (player == null) {
			plugin.getLogger().warning("[Plotty] ERROR: PLAYER IS NULL. CHECK CONFIG FOR ERRORS.");
			return false;
		}
		long lastVoted = player.lastVoted;
		if(lastVoted == 0 || now()-lastVoted > (dm.config.voteDelay*60*60*1000)){
			Location l = p.getLocation();
			int x = l.getBlockX();
			int z = l.getBlockZ();
			Integer[] corners = PlotFinder.getCornerFromCoords(x, z, plugin.plotSize);
			if(corners.length != 2){
				p.sendMessage(plugin.lang.notStandingInPlot);
				return true;
			}
			PlottyPlot plot = dm.getPlotFromCoords(corners[0], corners[1]);
			if(plot == null){
				p.sendMessage(plugin.lang.plotFree);
				return true;
			}
			if(!plot.visible){
				p.sendMessage(ChatColor.DARK_RED + "[Plotty] " + ChatColor.RED + "This plot is private.");
				return true;
			}
			plot.rank = plot.rank+1;
			if(dm.getPlotOwner(plot) == null || dm.getPlayer(dm.getPlotOwner(plot)) == null){
				p.sendMessage(ChatColor.DARK_RED + "ERROR: " + ChatColor.RED + "Owner's playerdata not found.");
				return true;
			}
			PlottyPlayer owner = dm.getPlayer(dm.getPlotOwner(plot));
			if(owner.uuid.toString().equalsIgnoreCase(player.uuid.toString())){
				p.sendMessage(ChatColor.DARK_RED + "[Plotty] " + ChatColor.RED + "You can't vote for your own plot!");
				return true;
			}
			owner.plots[dm.plotIndex(plot.id, owner)] = plot;
			dm.config.playerPlots.put(owner.uuid, owner);
			player.lastVoted = now();
			dm.config.playerPlots.put(player.uuid, player);
			dm.save();
		}else{
			p.sendMessage(plugin.lang.cantVote.replaceAll("%s", Math.round(((lastVoted+(dm.config.voteDelay*60*60*1000))-now())/1000/60)+""));
			return true;
		}
		return true;
	}
	public long now(){
		Calendar cal = Calendar.getInstance();
		return cal.getTimeInMillis();
	}
}
