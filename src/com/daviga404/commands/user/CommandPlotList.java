package com.daviga404.commands.user;

import com.daviga404.Plotty;
import com.daviga404.commands.PlottyCommand;
import com.daviga404.data.DataManager;
import com.daviga404.data.PlottyPlayer;
import com.daviga404.data.PlottyPlot;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.hover.content.Text;

import org.bukkit.entity.Player;

public class CommandPlotList extends PlottyCommand {

	private final Plotty plugin;

	public CommandPlotList(Plotty pl) {
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

	@Override
	public boolean execute(Player p, String[] args) {
		DataManager dm = plugin.getDataManager();
		PlottyPlayer pp = dm.getPlayer(p.getUniqueId());
		if (pp == null || pp.plots == null || pp.plots.length == 0) {
			p.sendMessage(plugin.lang.noPlots);
			return true;
		}
		ComponentBuilder builder = new ComponentBuilder("[Plotty] Your Plots:").color(ChatColor.DARK_BLUE)
			.append("\n");
		for (PlottyPlot plot : pp.plots) {
			builder.append("- ").color(ChatColor.AQUA)
				.append("Plot ")
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/plot tp " + plot.id))
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.BLUE + "Click to teleport\nto " + ChatColor.AQUA + "plot " + plot.id)))
				.append(Integer.toString(plot.id))
				.append(" [x:").color(ChatColor.BLUE)
				.append(Integer.toString(plot.x))
				.append(", z:")
				.append(Integer.toString(plot.z));
			if (dm.config.worlds.length > 1) {
				builder.append(", w:")
					.append(plot.world);
			}
			builder.append("]");
			String friendsString = "";
			for (String s : plot.friends) {
				friendsString += s + ", ";
			}
			if (!friendsString.isEmpty()) {
				friendsString = friendsString.substring(0, friendsString.length() - 2);
				builder
					.append(" [Friends: ", FormatRetention.NONE).color(ChatColor.AQUA)
					.append(friendsString);
			}
			builder.append("]\n", FormatRetention.NONE).color(ChatColor.AQUA);
		}
		p.spigot().sendMessage(builder.create());
		return true;
	}
}
