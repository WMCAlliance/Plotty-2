package com.daviga404.commands.admin;

import com.daviga404.Plotty;
import com.daviga404.commands.PlottyCommand;
import com.daviga404.language.LangManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class CommandPlotReload extends PlottyCommand {

	private final Plotty plugin;

	public CommandPlotReload(Plotty pl) {
		super(
				"reload",
				"(reload)",
				"plotty.admin.reload",
				"/plot reload",
				"Reloads the configuration and language files.",
				false
		);
		this.plugin = pl;
	}

	@Override
	public boolean execute(Player p, String[] args) {
		try {
			plugin.dm.checkForFile();
			plugin.langMan = new LangManager(plugin);
			plugin.lang = plugin.langMan.getLang();
			p.sendMessage(ChatColor.GREEN + "[Plotty] All configs reloaded!");
		} catch (Exception e) {
			p.sendMessage(ChatColor.DARK_RED + "[Plotty] " + ChatColor.RED + "An error occurred while reloading. Check the console for errors.");
			e.printStackTrace();
		}
		return true;
	}
}
