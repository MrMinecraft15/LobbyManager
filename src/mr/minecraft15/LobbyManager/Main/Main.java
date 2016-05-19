package mr.minecraft15.LobbyManager.Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.ArrayList;

import mr.minecraft15.LobbyManager.Commands.Lobby;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Main extends Plugin {
	public static Plugin plugin;
	
	public static File f;
	public static Configuration c;
	
	public static String lang;
	
	public static String[] arguments;
	
	public void onEnable() {
		plugin = this;
		loadConfig();
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new Lobby());
	}
	
	public static void loadConfig() {
		try {
			f = new File(Main.plugin.getDataFolder(), "config.yml");
			if(!Main.plugin.getDataFolder().exists()) {
				Main.plugin.getDataFolder().mkdir();
				if(!f.exists()) {
					Files.copy(Main.plugin.getResourceAsStream("config.yml"), f.toPath(), new CopyOption[0]);
				}
			}
			c = ConfigurationProvider.getProvider(YamlConfiguration.class).load(f);

			if(c.get("Aliases") == null) {
				ArrayList<String> list = new ArrayList<String>();
				list.add("hub");
				list.add("leave");
				list.add("l");
				c.set("Aliases", list);
				save();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		lang = c.getString("Language");
		arguments = new String[c.getStringList("Aliases").size()];
		int i = 0;
		for(String alias : c.getStringList("Aliases")) {
			arguments[i] = alias;
			i++;
		}
	}

	public static void save() {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(Main.c, Main.f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getMessage(String message) {
		return ChatColor.translateAlternateColorCodes('&', c.getString("Lang." + lang + "." + message));
	}
}
