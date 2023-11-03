package com.daviga404.commands.user;

import com.daviga404.Plotty;
import com.daviga404.commands.PlottyCommand;
import com.daviga404.data.DataManager;
import com.daviga404.data.PlottyPlayer;
import com.daviga404.data.PlottyPlot;
import java.util.UUID;
import org.bukkit.entity.Player;

public class CommandPlotPrivate extends PlottyCommand{
	private Plotty plugin;
	public CommandPlotPrivate(Plotty pl){
		super(
		"private",
		"(private)(\\s+)(\\d+)",
		"plotty.private",
		"/plot private <id>",
		"Hides a plot from the global list & rankings."
		);
		this.plugin = pl;
	}
	public boolean execute(Player p, String[] args){
		DataManager dm = plugin.getDataManager();
		PlottyPlot plot = dm.getPlotFromId(Integer.parseInt(args[0]));
		if(plot == null){p.sendMessage(plugin.lang.notFound);return true;}
		UUID owner = dm.getPlotOwner(plot);
		if(!owner.equals(p.getUniqueId())){
			p.sendMessage(plugin.lang.dontOwn);
			return true;
		}
		plot.visible = false;
		PlottyPlayer pp = dm.getPlayer(p.getUniqueId());
		if (pp == null) {
			p.sendMessage(plugin.lang.noPlots);
			return true;
		}
		pp.plots[dm.plotIndex(plot.id, pp)] = plot;
		dm.config.playerPlots.put(p.getUniqueId(), pp);
		dm.save();
		p.sendMessage(plugin.lang.madePrivate);
		return true;
	}
}
