package com.daviga404.plots;

import com.daviga404.Plotty;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.util.BlockVector;

public class PlotRegion {
	public static Plotty plugin;
	public static void init(Plotty pl){
		PlotRegion.plugin = pl;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void makePlotRegion(Plot p,String owner,int id){
		String name = "plot_"+owner.toLowerCase()+"_"+id;
		BlockVector3 point1 = BlockVector3.at(p.getX(),0,p.getZ());
		BlockVector3 point2 = BlockVector3.at(p.getX()+plugin.plotSize,256,p.getZ()+plugin.plotSize);
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager rm = container.get(BukkitAdapter.adapt(p.getWorld()));
		ProtectedCuboidRegion pcr = new ProtectedCuboidRegion(name, point1, point2);
		pcr.getOwners().addPlayer(owner);
		if(!plugin.getDataManager().config.enableTnt){
			pcr.setFlag(Flags.TNT,State.DENY);
		}
		CommandSender s = (CommandSender)Bukkit.getPlayer(owner);
		for(Map.Entry<String, String> flag : plugin.getDataManager().config.flags.entrySet()){
			try {
				/**
				 * TODO: Fix this flag reader
				 * The previous version looped through all flags in WG.
				 * This new code instead loops through the configured flags in Plotty, meaning no loop if it's empty
				 * However, figuring out the below code to reverse it is a little harder without an example
				 * I assume it's "TNT": "DENY"
				 */
//				Object obj = flag.parseInput(plugin.worldGuard, s, plugin.getDataManager().config.flags.get(flag.getName()));
//				Flag f = (Flag)flag;
//				pcr.setFlag(f, obj);
			} catch(Exception e1) {
				s.sendMessage(ChatColor.DARK_RED + "[Plotty] " + ChatColor.RED + "Error in config: custom WorldGuard flag has incorrect value.");
			}
		}
		rm.addRegion(pcr);
		try {
			rm.save();
		} catch (StorageException e) {
			e.printStackTrace();
		}
	}
	public static boolean addFriend(int id, String owner, String friend, World w){
		String name = "plot_"+owner.toLowerCase()+"_"+id;
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager rm = container.get(BukkitAdapter.adapt(w));
		if(rm.getRegion(name) != null){
			rm.getRegion(name).getOwners().addPlayer(friend);
			try {
				rm.save();
			} catch (StorageException e) {
				e.printStackTrace();
			}
			return true;
		}else{
			return false;
		}
	}
	public static boolean removeFriend(int id, String owner, String friend, World w){
		String name = "plot_"+owner.toLowerCase()+"_"+id;
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager rm = container.get(BukkitAdapter.adapt(w));
		if(rm.getRegion(name) != null){
			if(rm.getRegion(name).getOwners().contains(friend)){
				rm.getRegion(name).getOwners().removePlayer(friend);
			}
			try {
				rm.save();
			} catch (StorageException e) {
				e.printStackTrace();
			}
			return true;
		}else{
			return false;
		}
	}
}
