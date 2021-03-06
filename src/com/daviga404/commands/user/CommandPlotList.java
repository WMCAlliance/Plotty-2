package com.daviga404.commands.user;

import com.daviga404.Plotty;
import com.daviga404.commands.PlottyCommand;
import com.daviga404.data.DataManager;
import com.daviga404.data.PlottyPlot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandPlotList extends PlottyCommand{
	private Plotty plugin;
	public CommandPlotList(Plotty pl){
		super(
		"list",
		"list",
		"plotty.list",
		"/plot list",
		"Lists all plots.",
		false
		);
		this.plugin = pl;
	}
	public boolean execute(Player p, String[] args){
		StringBuilder builder = new StringBuilder();
		builder.append(ChatColor.DARK_BLUE + "[Plotty] Your Plots:\n");
		DataManager dm = plugin.getDataManager();
		for(PlottyPlot plot : dm.getPlayer(p.getName()).plots){
			builder.append(ChatColor.AQUA + "- Plot ");
			builder.append(plot.id);
			builder.append(ChatColor.BLUE + " [x:");
			builder.append(plot.x);
			builder.append(", z:");
			builder.append(plot.z);
			builder.append(", w:");
			builder.append(plot.world);
			builder.append("] " + ChatColor.AQUA + "[Friends: ");
			String friendsString="";
			for(String s : plot.friends){
				friendsString += s +", ";
			}
			if(friendsString != ""){
				friendsString = friendsString.substring(0,friendsString.length()-2);
			}else{
				friendsString = "none";
			}
			builder.append(friendsString);
			builder.append("]\n");
		}
		p.sendMessage(builder.toString());
		return true;
	}
}
