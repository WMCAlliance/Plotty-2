package com.daviga404.commands.admin;

import com.daviga404.Plotty;
import com.daviga404.commands.PlottyCommand;
import com.daviga404.data.DataManager;
import com.daviga404.data.PlottyPlayer;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CommandPlotGrant extends PlottyCommand {

	private Plotty plugin;

	public CommandPlotGrant(Plotty pl) {
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

	public boolean execute(Player p, String[] args) {
		int amount = 1;
		if (args.length == 2) {
			amount = Integer.parseInt(args[1]);
		}
		DataManager dm = plugin.getDataManager();
		OfflinePlayer op = plugin.getOfflinePlayer(args[0]);
		if (!op.isOnline() && !op.hasPlayedBefore()) {
			p.sendMessage(ChatColor.RED + "[Plotty] That player has never played before!");
			return true;
		}
		PlottyPlayer pp = dm.getPlayerOrCreate(op);
		pp.grantedPlots += amount;
		dm.config.playerPlots.put(op.getUniqueId(), pp);
		dm.save();

		if (Bukkit.getPlayer(args[0]) == null) {
			UUID[] pgn = dm.config.playerGrantNotify;
			UUID[] newpgn = new UUID[pgn.length + 1];
			int i = 0;
			for (UUID s : pgn) {
				newpgn[i] = s;
				i++;
			}
			newpgn[pgn.length] = op.getUniqueId();
			dm.config.playerGrantNotify = newpgn;
			dm.save();
			p.sendMessage(ChatColor.GREEN + "[Plotty] Granted " + ChatColor.DARK_GREEN + amount + ChatColor.GREEN + " plots to " + ChatColor.DARK_BLUE + args[0] + ChatColor.GREEN + ". Player will be notified when they come online.");
		} else {
			p.sendMessage(ChatColor.GREEN + "[Plotty] Granted " + ChatColor.DARK_GREEN + amount + ChatColor.GREEN + " plots to " + ChatColor.DARK_BLUE + args[0]);
			Bukkit.getPlayer(args[0]).sendMessage(ChatColor.DARK_BLUE + "[Plotty] " + ChatColor.AQUA + "You have been granted " + ChatColor.BLUE + amount + ChatColor.AQUA + " plot(s)!");
		}
		return true;
	}
}
