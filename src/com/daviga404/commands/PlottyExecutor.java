package com.daviga404.commands;

import com.daviga404.Plotty;
import com.daviga404.commands.admin.CommandPlotConfig;
import com.daviga404.commands.admin.CommandPlotGrant;
import com.daviga404.commands.admin.CommandPlotReload;
import com.daviga404.commands.user.CommandPlotClaim;
import com.daviga404.commands.user.CommandPlotClear;
import com.daviga404.commands.user.CommandPlotDel;
import com.daviga404.commands.user.CommandPlotFriend;
import com.daviga404.commands.user.CommandPlotGList;
import com.daviga404.commands.user.CommandPlotInfo;
import com.daviga404.commands.user.CommandPlotList;
import com.daviga404.commands.user.CommandPlotNew;
import com.daviga404.commands.user.CommandPlotPrivate;
import com.daviga404.commands.user.CommandPlotPublic;
import com.daviga404.commands.user.CommandPlotTp;
import com.daviga404.commands.user.CommandPlotUnfriend;
import com.daviga404.commands.user.CommandPlotVote;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class PlottyExecutor implements TabExecutor {

	private final ArrayList<PlottyCommand> cmds = new ArrayList<>();
	private final Plotty pl;

	public PlottyExecutor(Plotty pl) {
		this.pl = pl;
		cmds.add(new CommandPlotNew(pl));
		cmds.add(new CommandPlotList(pl));
		cmds.add(new CommandPlotFriend(pl));
		cmds.add(new CommandPlotUnfriend(pl));
		cmds.add(new CommandPlotTp(pl));
		cmds.add(new CommandPlotInfo(pl));
		cmds.add(new CommandPlotDel(pl));
		cmds.add(new CommandPlotClear(pl));
		cmds.add(new CommandPlotClaim(pl));
		cmds.add(new CommandPlotPublic(pl));
		cmds.add(new CommandPlotPrivate(pl));
		cmds.add(new CommandPlotGList(pl));
		cmds.add(new CommandPlotVote(pl));
		//admin commands
		cmds.add(new CommandPlotConfig(pl));
		cmds.add(new CommandPlotReload(pl));
		cmds.add(new CommandPlotGrant(pl));
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1) {
			String base = args.length > 0 ? args[0] : "";
			ArrayList<String> matches = new ArrayList<>();
			for (PlottyCommand cmd : cmds) {
				if (cmd.base.startsWith(base)) {
					matches.add(cmd.base);
				}
			}
			return matches;
		}
		return new ArrayList<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String base = args.length > 0 ? args[0] : "";
		if (base.equalsIgnoreCase("")) {
			sender.sendMessage(ChatColor.DARK_BLUE + "Plotty v" + pl.getDescription().getVersion() + " " + ChatColor.AQUA + "by Daviga404\n" + ChatColor.BLUE + "Type /plot ? for help.");
			return true;
		}
		if (base.equalsIgnoreCase("?") || base.equalsIgnoreCase("help")) {
			if (args.length > 1) {
				try {
					showHelp(sender, Integer.parseInt(args[1]) - 1);
				} catch (Exception ex) {
					sender.sendMessage("'" + args[1] + "' is not a number.");
				}
			} else {
				showHelp(sender, 0);
			}
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage(pl.lang.mustBePlayer);
			return true;
		}

		Player p = (Player) sender;

		ArrayList<PlottyCommand> matches = new ArrayList<>();
		ArrayList<PlottyCommand> basematches = new ArrayList<>();
		String commandString = combineArgs(args);
		for (PlottyCommand cmd : cmds) {
			if (base.equalsIgnoreCase(cmd.base)) {
				Pattern pat = Pattern.compile(cmd.regex);
				Matcher mat = pat.matcher(commandString);
				if (mat.matches()) {
					matches.add(cmd);
				} else {
					System.out.println("Base Matches, Command Doesn't, Command: " + commandString);
					basematches.add(cmd);
				}
			}
		}

		if (matches.size() < 1) {
			if (basematches.size() < 1) {
				p.sendMessage(ChatColor.RED + "[Plotty] Unknown command. Type /plot ? for help.");
			} else {
				if (basematches.size() == 1) {
					p.sendMessage(ChatColor.RED + "[Plotty] Usage: " + basematches.get(0).usage);
				} else {
					p.sendMessage(ChatColor.DARK_RED + "[Plotty] Multiple possible matches for your command were found:");
					for (PlottyCommand match : basematches) {
						p.sendMessage(ChatColor.RED + "[Plotty] " + match.usage);
					}
				}
			}
			return true;
		} else if (matches.size() > 1) {
			p.sendMessage(ChatColor.DARK_RED + "[Plotty] Multiple possible matches for your command were found:");
			for (PlottyCommand match : matches) {
				p.sendMessage(ChatColor.RED + "[Plotty] " + match.usage);
			}
			return true;
		}

		PlottyCommand cmd = matches.get(0);

		if (!(p.hasPermission(cmd.permission) || p.hasPermission("plotty.*") || p.isOp())) {
			p.sendMessage(ChatColor.RED + "[Plotty] You do not have access to this command.");
			return true;
		}

		boolean isInRightWorld = false;
		for (String worldName : pl.dm.config.worlds) {
			if (p.getWorld().getName().equalsIgnoreCase(worldName)) {
				isInRightWorld = true;
			}
		}
		if (isInRightWorld || !cmd.plotsWorld) {
			String[] newargs = new String[args.length - 1];
			System.arraycopy(args, 1, newargs, 0, newargs.length);
			cmd.execute(p, newargs);
		} else {
			p.sendMessage(pl.lang.mustBeInPlotsWorld);
		}
		return true;
	}

	public void showHelp(CommandSender sender, int page) {
		ArrayList<List<PlottyCommand>> pages = new ArrayList<>();
		ArrayList<PlottyCommand> current = new ArrayList<>();
		if (cmds.size() > 8) {
			for (int i = 0; i < cmds.size(); i++) {
				if (i % 8 == 0) {
					pages.add(cmds.subList(i, cmds.size() - i < 8 ? cmds.size() : i + 8));
				}
			}
		} else {
			for (PlottyCommand cmd : cmds) {
				current.add(cmd);
			}
			pages.add(current);
		}
		if (page > pages.size() - 1) {
			sender.sendMessage(ChatColor.GRAY + "Warning: page not found.");
			sender.sendMessage(ChatColor.BLUE + "Plotty Help " + ChatColor.AQUA + "(Page 1/" + pages.size() + "):");
			for (PlottyCommand com : pages.get(0)) {
				if (sender.hasPermission(com.permission) || sender.hasPermission("plotty.*") || sender.isOp()) {
					sender.sendMessage(ChatColor.BLUE + com.usage + ChatColor.AQUA + " - " + com.description);
				} else {
					sender.sendMessage(ChatColor.BLUE + "" + ChatColor.STRIKETHROUGH + com.usage + ChatColor.AQUA + "" + ChatColor.STRIKETHROUGH + " - " + com.description);
				}
			}
		} else {
			sender.sendMessage(ChatColor.DARK_BLUE + "Plotty Help " + ChatColor.AQUA + "(Page " + (page + 1) + "/" + pages.size() + "):");
			for (PlottyCommand com : pages.get(page)) {
				if (sender.hasPermission(com.permission) || sender.hasPermission("plotty.*") || sender.isOp()) {
					sender.sendMessage(ChatColor.BLUE + com.usage + ChatColor.AQUA + " - " + com.description);
				} else {
					sender.sendMessage(ChatColor.BLUE + "" + ChatColor.STRIKETHROUGH + com.usage + ChatColor.AQUA + "" + ChatColor.STRIKETHROUGH + " - " + com.description);
				}
			}
		}
		/*
		ArrayList<PlottyCommand> currentCommands = new ArrayList<PlottyCommand>();
		ArrayList<ArrayList<PlottyCommand>> pages = new ArrayList<ArrayList<PlottyCommand>>();
		if(cmds.size() > 8){
			int j = 0;
			for(int i=0;i<cmds.size();i++){
				if(j == 8){
					pages.add(currentCommands);
					currentCommands.clear();
					currentCommands.add(cmds.get(i));
					j = 0;
				}else{
					currentCommands.add(cmds.get(i));
					j++;
				}
			}
		}else{
			for(PlottyCommand cmd : cmds){
				currentCommands.add(cmd);
			}
			pages.add(currentCommands);
		}
		
		if(page > (pages.size()-1)){
			sender.sendMessage(ChatColor.DARK_BLUE + "Plotty Help " + ChatColor.AQUA + "(Page 1/"+pages.size()+"):");
			for(PlottyCommand com : pages.get(0)){
				sender.sendMessage(ChatColor.BLUE +com.usage+" + ChatColor.AQUA + " - "+com.description);
			}
		}else{
			sender.sendMessage(ChatColor.DARK_BLUE + "Plotty Help " + ChatColor.AQUA + "(Page "+(page+1)+"/"+pages.size()+"):");
			for(PlottyCommand com : pages.get(page)){
				sender.sendMessage(ChatColor.BLUE +com.usage+" + ChatColor.AQUA + " - "+com.description);
			}
		}*/
	}

	public String combineArgs(String[] args) {
		String finalstr = "";
		for (String a : args) {
			finalstr += a + " ";
		}
		finalstr = finalstr.substring(0, finalstr.length() - 1);
		return finalstr;
	}
}
