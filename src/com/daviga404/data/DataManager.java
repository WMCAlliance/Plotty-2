package com.daviga404.data;

import com.daviga404.Plotty;
import com.daviga404.plots.Plot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public final class DataManager {

	public File file;
	public Gson gson;
	public Plotty plugin;
	public PlottyConfig config, defaultConfig;

	public DataManager(Plotty plugin) {
		defaultConfig = new PlottyConfig();
		defaultConfig.baseBlock = "STONE";
		defaultConfig.centertp = true;
		defaultConfig.clearEnabled = false;
		defaultConfig.clearOnDelete = true;
		defaultConfig.delCooldown = 30;
		defaultConfig.enableEco = false;
		defaultConfig.enableTnt = false;
		defaultConfig.maxPlots = 5;
		defaultConfig.playerGrantNotify = new UUID[]{};
		defaultConfig.players = new PlottyPlayer[]{};
		defaultConfig.playerPlots = new HashMap<UUID, PlottyPlayer>();
		defaultConfig.plotCost = 0.0;
		defaultConfig.plotHeight = 20;
		defaultConfig.plotSize = 64;
		defaultConfig.publicByDefault = true;
		defaultConfig.surfaceBlock = "GRASS";
		defaultConfig.voteDelay = 12.0;
		defaultConfig.worlds = new String[]{};
		defaultConfig.flags = new HashMap<String, String>();
		this.plugin = plugin;
		try {
			checkForFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void checkDefaults() {
		config.flags = config.flags == null ? defaultConfig.flags : config.flags;
		config.playerGrantNotify = config.playerGrantNotify == null ? defaultConfig.playerGrantNotify : config.playerGrantNotify;
		config.players = config.players == null ? defaultConfig.players : config.players;
		config.worlds = config.worlds == null ? defaultConfig.worlds : config.worlds;
		save();
	}

	/**
	 * Checks if the plots file exists and loads data into class.
	 *
	 * @throws IOException
	 */
	public void checkForFile() throws IOException {
		if (!plugin.getDataFolder().exists()) {
			plugin.getDataFolder().mkdir();
		}
		file = new File(plugin.getDataFolder() + File.separator + "plots.json");
		gson = new GsonBuilder().setPrettyPrinting().create();
		if (!file.exists()) {
			file.createNewFile();
			Bukkit.getLogger().info("[Plotty] Creating default plots file...");
			try (FileWriter out = new FileWriter(file)) {
				out.write(gson.toJson(defaultConfig));
				out.flush();
				config = defaultConfig;
				Bukkit.getLogger().info("[Plotty] Created default plots file (plugins/Plotty/plots.json)");
			}
		} else {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
				String ln, buff = "";
				while ((ln = br.readLine()) != null) {
					buff += ln;
				}
				config = gson.fromJson(buff, PlottyConfig.class);
				Bukkit.getLogger().info("[Plotty] Plotty data loaded.");
			}
		}
		migratePlayers();
		checkDefaults();
	}

	public void migratePlayers() {
		PlottyPlayer[] players = config.players;
		if (players == null)
			return;
		if (players.length == 0 || !config.playerPlots.isEmpty())
			return;

		Logger l = plugin.getServer().getLogger();
		l.log(Level.INFO, "[Plotty] Migrating player files for significantly better performance.");
		for (PlottyPlayer pp : players) {
			if (pp.uuid == null || pp.uuid.toString().isEmpty()) {
				plugin.getServer().getLogger().log(Level.INFO, "[Plotty] Failed to migrate player {0}. Missing UUID.", pp.name);
				continue;
			}
			config.playerPlots.put(pp.uuid, pp);
		}
		if (players.length == config.playerPlots.size()) {
			l.log(Level.INFO, "[Plotty] Successfully migrated all {0} players.", config.playerPlots.size());
			config.players = new PlottyPlayer[]{};
		} else {
			l.warning("[Plotty] Not all Plotty players were migrated. The config will remain untouched, but unmigrated player's plots will not function correctly until you manually edit the config to migrate them.");
		}
		save();
	}

	/**
	 * Adds a plot to the config file and saves.
	 *
	 * @param p The plot to add
	 * @param owner The UUID of the owner of the plot
	 * @param id The ID of the plot
	 * @see getLatestId();
	 */
	public void addPlot(Plot p, UUID owner, int id) {
		PlottyPlayer player = getPlayer(owner);///////////////
		PlottyPlot plot = new PlottyPlot();
		plot.friends = new String[]{};
		plot.id = id;
		plot.world = p.getWorld().getName();
		plot.x = p.getX();
		plot.z = p.getZ();
		plot.visible = config.publicByDefault;
		player.plots = pushPlottyPlot(player.plots, plot);

		config.playerPlots.put(owner, player);

		save();
	}

	/**
	 * Removes a plot from the config file and saves.
	 *
	 * @param id The ID of the plot
	 * @param owner The name of the owner
	 * @return Whether the task succeeded.
	 */
	public boolean removePlot(int id, UUID owner) {
		PlottyPlayer p = getPlayer(owner);
		if (p == null)
			return false;
		PlottyPlot[] newArray = new PlottyPlot[p.plots.length - 1];
		int i = 0;
		for (PlottyPlot plot : p.plots) {
			if (plot.id != id) {
				newArray[i] = plot;
				i++;
			}
		}
		p.plots = newArray;
		config.playerPlots.put(p.uuid, p);
		save();
		return true;
	}

	/**
	 * Adds a friend to a plot in the config and saves.
	 *
	 * @param p The plot to add a friend to
	 * @param owner The name of the owner of the plot
	 * @param friend The friend to add
	 */
	public void addFriend(PlottyPlot p, UUID owner, String friend) {
		p.friends = pushString(p.friends, friend);
		PlottyPlayer player = getPlayer(owner);
		player.plots[plotIndex(p.id, player)] = p;
		config.playerPlots.put(owner, player);
		save();
	}

	/**
	 * Removes a friend from a plot in the config and saves.
	 *
	 * @param p The plot to remove a friend from
	 * @param owner The UUID of the plot owner
	 * @param friend The name of the friend to remove.
	 */
	public void removeFriend(PlottyPlot p, UUID owner, String friend) {
		boolean exists = false;
		for (String cf : p.friends) {
			if (friend.equalsIgnoreCase(cf)) {
				exists = true;
			}
		}
		if (!exists) {
			return;
		}
		String[] newFriends = new String[p.friends.length - 1];
		int added = 0;
		for (String cfriend : p.friends) {
			if (!friend.equalsIgnoreCase(cfriend)) {
				newFriends[added] = cfriend;
				added++;
			}
		}
		p.friends = newFriends;
		PlottyPlayer player = getPlayer(owner);
		player.plots[plotIndex(p.id, player)] = p;
		config.playerPlots.put(owner, player);
		save();
	}

	/**
	 * Gets a plot object from coordinates of corner.
	 *
	 * @param x The X location of the plot corner.
	 * @param z The Z location of the plot corner.
	 * @return A plot object (null if not found)
	 */
	public PlottyPlot getPlotFromCoords(int x, int z) {
		for (PlottyPlayer p : config.playerPlots.values()) {
			for (PlottyPlot pl : p.plots) {
				if (pl.x == x && pl.z == z) {
					return pl;
				}
			}
		}
		return null;
	}

	/**
	 * Gets a plot object from an ID.
	 *
	 * @param id The ID of the plot.
	 * @return
	 */
	public PlottyPlot getPlotFromId(int id) {
		for (PlottyPlayer p : config.playerPlots.values()) {
			for (PlottyPlot pl : p.plots) {
				if (pl.id == id) {
					return pl;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the owner of a plot from the plot object.
	 *
	 * @param pl The plot to find the owner of.
	 * @return The UUID of the plot owner (null if not found)
	 */
	public UUID getPlotOwner(PlottyPlot pl) {
		for (PlottyPlayer p : config.playerPlots.values()) {
			for (PlottyPlot pp : p.plots) {
				if (pp.id == pl.id) {
					return p.uuid;
				}
			}
		}
		return null;
	}

	/**
	 * Gets the index of a plot in a player's config file (used to replace plots with new info)
	 *
	 * @param id The ID of the plot.
	 * @param p The player file of the owner of the plot.
	 * @return The index of the plot in a player's file. Returns -1 if not found.
	 */
	public int plotIndex(int id, PlottyPlayer p) {
		int i = 0;
		for (PlottyPlot pl : p.plots) {
			if (pl.id == id) {
				return i;
			}
			i++;
		}
		return -1;
	}

	/**
	 * Gets if the player has reached their maximum number of plots.
	 *
	 * @param uuid The name of the player.
	 * @return A boolean that is true if the player has reached their maximum number of plots.
	 */
	public boolean pExceededMaxPlots(UUID uuid) {
		int count = getPlayer(uuid).plots.length;
		int max = getPlayer(uuid).maxPlots == -1 ? config.maxPlots : getPlayer(uuid).maxPlots;
		return count >= max;
	}

	//Utils
	public PlottyPlot[] pushPlottyPlot(PlottyPlot[] array, PlottyPlot object) {
		PlottyPlot[] newArray = new PlottyPlot[array.length + 1];
		System.arraycopy(array, 0, newArray, 0, array.length);
		newArray[newArray.length - 1] = object;
		return newArray;
	}

	public PlottyPlayer[] pushPlottyPlayer(PlottyPlayer[] array, PlottyPlayer object) {
		PlottyPlayer[] newArray = new PlottyPlayer[array.length + 1];
		System.arraycopy(array, 0, newArray, 0, array.length);
		newArray[newArray.length - 1] = object;
		return newArray;
	}

	public String[] pushString(String[] array, String object) {
		String[] newArray = new String[array.length + 1];
		System.arraycopy(array, 0, newArray, 0, array.length);
		newArray[newArray.length - 1] = object;
		return newArray;
	}

	public PlottyPlayer getPlayer(UUID uuid) {
		if (config.playerPlots.containsKey(uuid))
			return config.playerPlots.get(uuid);
		return null;
	}

	public PlottyPlayer getPlayerOrCreate(OfflinePlayer p) {
		UUID uuid = p.getUniqueId();
		if (config.playerPlots.containsKey(uuid))
			return config.playerPlots.get(uuid);
		PlottyPlayer pp = new PlottyPlayer();
		pp.maxPlots = -1;
		pp.name = p.getName();
		pp.uuid = uuid;
		pp.plots = new PlottyPlot[]{};
		config.playerPlots.put(uuid, pp);
		save();
		return pp;
	}

	public void save() {
		try {
			FileWriter out = new FileWriter(file);
			out.write(gson.toJson(config));
			out.flush();
			out.close();
		} catch (Exception e) {
			Bukkit.getLogger().log(Level.SEVERE, "IOException: {0} ({1})", new Object[]{e.getMessage(), e.toString()});
		}
	}

	public int getLatestId() {
		int greatest = -1;
		for (PlottyPlayer p : config.playerPlots.values()) {
			for (PlottyPlot pl : p.plots) {
				if (pl.id > greatest) {
					greatest = pl.id;
				}
			}
		}
		greatest++;
		return greatest;
	}
}
