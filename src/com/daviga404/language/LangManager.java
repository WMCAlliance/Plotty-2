package com.daviga404.language;

import com.daviga404.Plotty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import org.bukkit.ChatColor;

public class LangManager {
	private Plotty plugin;
	private File file;
	private Lang language;
	private Gson gson;
	private Lang defaultLang;
	char amp = '&';
	public LangManager(Plotty pl) throws Exception{
		defaultLang = new Lang();
		defaultLang.alreadyFriend = "&1[Plotty] &9This player is already a friend in your plot.";
		defaultLang.createdPlot = "&a[Plotty] Plot created with ID &2%s";
		defaultLang.noPlots = "&4[Plotty] &cYou do not have any plots.";
		defaultLang.dontOwn = "&4[Plotty] &cYou do not own this plot.";
		defaultLang.friendAdded = "&a[Plotty] Friend added to plot!";
		defaultLang.friendNotFound = "&4[Plotty] &cFriend not found.";
		defaultLang.friendRemoved = "&a[Plotty] Friend removed from plot!";
		defaultLang.madePublic = "&a[Plotty] Plot is now &2public.";
		defaultLang.madePrivate = "&a[Plotty] Plot is now &2private.";
		defaultLang.notFound = "&4[Plotty] &cPlot not found.";
		defaultLang.notStandingInPlot = "&4[Plotty] &cYou are not standing in a plot.";
		defaultLang.plotClaimed = "&a[Plotty] Plot claimed with ID &2%s.";
		defaultLang.plotCleared = "&a[Plotty] Plot cleared!";
		defaultLang.plotDeleted = "&a[Plotty] Plot deleted!";
		defaultLang.plotFree = "&1[Plotty] &9This plot is free.";
		defaultLang.plotHere = "&4[Plotty] &cThere is a plot here!";
		defaultLang.reachedMaxPlots = "&4[Plotty] &cYou have reached your maximum number of plots. Type /plot del <id> to delete a plot.";
		defaultLang.teleportedToPlot = "&1[Plotty] &9Teleported to plot (ID %s).";
		defaultLang.mustBePlayer = "[Plotty] You must be a player to use Plotty's commands.";
		defaultLang.mustBeInPlotsWorld = "&4[Plotty] &cYou must be in a plots world to do this!";
		defaultLang.cantVote = "&4[Plotty] &cYou cannot vote for another %s minutes.";
		defaultLang.noMoney = "&4[Plotty] &cYou do not have enough money to buy a plot!";
		defaultLang.moneyTaken = "&a[Plotty] Plot purchased for %s!";
		this.gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		this.plugin = pl;
		File folder = plugin.getDataFolder();
		file = new File(folder+File.separator+"language.json");
		if(!folder.exists()){
			folder.mkdir();
		}
		if(!file.exists()){
			file.createNewFile();
			FileWriter fw = new FileWriter(file);
			fw.write(gson.toJson(defaultLang,Lang.class));
			fw.flush();
			fw.close();
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String ln,buff="";
		while((ln = br.readLine()) != null){
			buff += ln;
		}
		br.close();
		ChatColor.translateAlternateColorCodes(amp, buff);
		language = gson.fromJson(buff, Lang.class);
		checkDefaults();
	}
	public void checkDefaults(){
		language.alreadyFriend = language.alreadyFriend == null ? defaultLang.alreadyFriend : language.alreadyFriend;
		language.cantVote = language.cantVote == null ? defaultLang.cantVote : language.cantVote;
		language.createdPlot = language.createdPlot == null ? defaultLang.createdPlot : language.createdPlot;
		language.noPlots = language.noPlots == null ? defaultLang.noPlots : language.noPlots;
		language.dontOwn = language.dontOwn == null ? defaultLang.dontOwn : language.dontOwn;
		language.friendAdded = language.friendAdded == null ? defaultLang.friendAdded : language.friendAdded;
		language.friendNotFound = language.friendNotFound == null ? defaultLang.friendNotFound : language.friendNotFound;
		language.friendRemoved = language.friendRemoved == null ? defaultLang.friendRemoved : language.friendRemoved;
		language.madePrivate = language.madePrivate == null ? defaultLang.madePrivate : language.madePrivate;
		language.madePublic = language.madePublic == null ? defaultLang.madePublic : language.madePublic;
		language.moneyTaken = language.moneyTaken == null ? defaultLang.moneyTaken : language.moneyTaken;
		language.mustBeInPlotsWorld = language.mustBeInPlotsWorld == null ? defaultLang.mustBeInPlotsWorld : language.mustBeInPlotsWorld;
		language.mustBePlayer = language.mustBePlayer == null ? defaultLang.mustBePlayer : language.mustBePlayer;
		language.noMoney = language.noMoney == null ? defaultLang.noMoney : language.noMoney;
		language.notFound = language.notFound == null ? defaultLang.notFound : language.notFound;
		language.notStandingInPlot = language.notStandingInPlot == null ? defaultLang.notStandingInPlot : language.notStandingInPlot;
		language.plotClaimed = language.plotClaimed == null ? defaultLang.plotClaimed : language.plotClaimed;
		language.plotCleared = language.plotCleared == null ? defaultLang.plotCleared : language.plotCleared;
		language.plotDeleted = language.plotDeleted == null ? defaultLang.plotDeleted : language.plotDeleted;
		language.plotFree = language.plotFree == null ? defaultLang.plotFree : language.plotFree;
		language.plotHere = language.plotHere == null ? defaultLang.plotHere : language.plotHere;
		language.reachedMaxPlots = language.reachedMaxPlots == null ? defaultLang.reachedMaxPlots : language.reachedMaxPlots;
		language.teleportedToPlot = language.teleportedToPlot == null ? defaultLang.teleportedToPlot : language.teleportedToPlot;
		//TODO: Properly do this conversion
        language.alreadyFriend = language.alreadyFriend.replaceAll("\u00A7","&");
        language.cantVote = language.cantVote.replaceAll("\u00A7","&");
		language.createdPlot = language.createdPlot.replaceAll("\u00A7", "&");
		language.noPlots = language.noPlots.replaceAll("\u00A7", "&");
        language.dontOwn = language.dontOwn.replaceAll("\u00A7","&");
        language.friendAdded = language.friendAdded.replaceAll("\u00A7","&");
        language.friendNotFound = language.friendNotFound.replaceAll("\u00A7","&");
        language.friendRemoved = language.friendRemoved.replaceAll("\u00A7","&");
        language.madePrivate = language.madePrivate.replaceAll("\u00A7","&");
        language.madePublic = language.madePublic.replaceAll("\u00A7","&");
        language.moneyTaken = language.moneyTaken.replaceAll("\u00A7","&");
        language.mustBeInPlotsWorld = language.mustBeInPlotsWorld.replaceAll("\u00A7","&");
        language.mustBePlayer = language.mustBePlayer.replaceAll("\u00A7","&");
        language.noMoney = language.noMoney.replaceAll("\u00A7","&");
        language.notFound = language.notFound.replaceAll("\u00A7","&");
        language.notStandingInPlot = language.notStandingInPlot.replaceAll("\u00A7","&");
        language.plotClaimed = language.plotClaimed.replaceAll("\u00A7","&");
        language.plotCleared = language.plotCleared.replaceAll("\u00A7","&");
        language.plotDeleted = language.plotDeleted.replaceAll("\u00A7","&");
        language.plotFree = language.plotFree.replaceAll("\u00A7","&");
        language.plotHere = language.plotHere.replaceAll("\u00A7","&");
        language.reachedMaxPlots = language.reachedMaxPlots.replaceAll("\u00A7","&");
        language.teleportedToPlot = language.teleportedToPlot.replaceAll("\u00A7","&");
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(gson.toJson(language,Lang.class));
			fw.flush();
			fw.close();
		}catch(Exception e1){
			e1.printStackTrace();
		}
		language.alreadyFriend = ChatColor.translateAlternateColorCodes(amp, language.alreadyFriend);
		language.cantVote = ChatColor.translateAlternateColorCodes(amp, language.cantVote);
		language.createdPlot = ChatColor.translateAlternateColorCodes(amp, language.createdPlot);
		language.noPlots = ChatColor.translateAlternateColorCodes(amp, language.noPlots);
		language.dontOwn = ChatColor.translateAlternateColorCodes(amp, language.dontOwn);
		language.friendAdded = ChatColor.translateAlternateColorCodes(amp, language.friendAdded);
		language.friendNotFound = ChatColor.translateAlternateColorCodes(amp, language.friendNotFound);
		language.friendRemoved = ChatColor.translateAlternateColorCodes(amp, language.friendRemoved);
		language.madePrivate = ChatColor.translateAlternateColorCodes(amp, language.madePrivate);
		language.madePublic = ChatColor.translateAlternateColorCodes(amp, language.madePublic);
		language.moneyTaken = ChatColor.translateAlternateColorCodes(amp, language.moneyTaken);
		language.mustBeInPlotsWorld = ChatColor.translateAlternateColorCodes(amp, language.mustBeInPlotsWorld);
		language.mustBePlayer = ChatColor.translateAlternateColorCodes(amp, language.mustBePlayer);
		language.noMoney = ChatColor.translateAlternateColorCodes(amp, language.noMoney);
		language.notFound = ChatColor.translateAlternateColorCodes(amp, language.notFound);
		language.notStandingInPlot = ChatColor.translateAlternateColorCodes(amp, language.notStandingInPlot);
		language.plotClaimed = ChatColor.translateAlternateColorCodes(amp, language.plotClaimed);
		language.plotCleared = ChatColor.translateAlternateColorCodes(amp, language.plotCleared);
		language.plotDeleted = ChatColor.translateAlternateColorCodes(amp, language.plotDeleted);
		language.plotFree = ChatColor.translateAlternateColorCodes(amp, language.plotFree);
		language.plotHere = ChatColor.translateAlternateColorCodes(amp, language.plotHere);
		language.reachedMaxPlots = ChatColor.translateAlternateColorCodes(amp, language.reachedMaxPlots);
		language.teleportedToPlot = ChatColor.translateAlternateColorCodes(amp, language.teleportedToPlot);
	}
	public Lang getLang(){
		return language;
	}
}
