package com.daviga404.plots;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;

import com.daviga404.Plotty;

public class PlotDeleter{
	public static ArrayList<UUID> deletecooldown = new ArrayList<UUID>();
	public static void addCooldown(UUID player, Plotty plotty){
		if(!isCooling(player)){
			deletecooldown.add(player);
			Bukkit.getScheduler().scheduleSyncDelayedTask(plotty, PlotDeleter.makeRunnable(player), plotty.dm.config.delCooldown*20);
		}
	}
	public static boolean isCooling(UUID player){
		return PlotDeleter.deletecooldown.contains(player);
	}
	public static Runnable makeRunnable(final UUID player){
		Runnable r = new Runnable(){

			public void run() {
				if(PlotDeleter.deletecooldown.contains(player)){
					PlotDeleter.deletecooldown.remove(player);
				}
			}

		};
		return r;
	}
}
