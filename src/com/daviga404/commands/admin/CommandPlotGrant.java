package com.daviga404.commands.admin;

import com.daviga404.Plotty;
import com.daviga404.commands.PlottyCommand;
import com.daviga404.data.DataManager;
import com.daviga404.data.PlottyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandPlotGrant extends PlottyCommand{
	private Plotty plugin;
	public CommandPlotGrant(Plotty pl){
		super(
		"grant",
		"(grant)( )(\\w+)(( )(\\d+))?",
		"plotty.admin.grant",
		"/plot grant <name> [amount]",
		"Gives a player the ability to make one or 'amount' plots.",
		false
		);
		this.plugin = pl;
	}
	public boolean execute(Player p, String[] args){
		int amount = 1;
		if(args.length == 2){
			amount = Integer.parseInt(args[1]);
		}
		DataManager dm = plugin.getDataManager();
		PlottyPlayer pp = dm.getPlayer(args[0]);
		pp.grantedPlots += amount;
		dm.config.players[dm.pIndex(args[0])] = pp;
		dm.save();
		
		if(Bukkit.getPlayer(args[0]) == null){
			String[] pgn = dm.config.playerGrantNotify;
			String[] newpgn = new String[pgn.length+1];
			int i=0;
			for(String s : pgn){
				newpgn[i] = s;
				i++;
			}
			newpgn[pgn.length] = args[0];
			dm.config.playerGrantNotify = newpgn;
			dm.save();
			p.sendMessage(ChatColor.GREEN + "[Plotty] Granted " + ChatColor.DARK_GREEN + amount + ChatColor.GREEN + " plots to " + ChatColor.DARK_BLUE + args[0] + ChatColor.GREEN + ". Player will be notified when they come online.");
		}else{
			p.sendMessage(ChatColor.GREEN + "[Plotty] Granted " + ChatColor.DARK_GREEN + amount + ChatColor.GREEN + " plots to " + ChatColor.DARK_BLUE + args[0]);
			Bukkit.getPlayer(args[0]).sendMessage(ChatColor.DARK_BLUE + "[Plotty] " + ChatColor.AQUA + "You have been granted " + ChatColor.BLUE +amount+ ChatColor.AQUA + " plot(s)!");
		}
		return true;
	}
}
