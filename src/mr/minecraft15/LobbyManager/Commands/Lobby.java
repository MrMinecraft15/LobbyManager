package mr.minecraft15.LobbyManager.Commands;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mr.minecraft15.LobbyManager.Main.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Lobby extends Command {
	public Lobby() {
		super("lobby", null, Main.arguments);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender s, String[] a) {
		if(s instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) s;
			List<String> l = Main.c.getStringList("Lobbys");
			if(a.length > 0) {
				if(a[0].equalsIgnoreCase("list")) {
					if(p.hasPermission("lobby.list")) {
						ArrayList<String> lobbys = new ArrayList<String>();
						for(String lobby : l) {
							ServerInfo i = ProxyServer.getInstance().getServerInfo(lobby);
							boolean online = false;
						    try {
						        Socket socket = new Socket(i.getAddress().getAddress(), i.getAddress().getPort());
						        socket.close();
						        online = true;
						    } catch(Exception e) {
						    }
							lobbys.add("§f" + lobby + " (" + (i == null ? "§c" + Main.getMessage("Unkown_Lobby") : (online ? "§aOnline" : "§cOffline")) + "§f)");
						}
						p.sendMessage("§aLobbys: " + lobbys.toString().replace("[", "").replace("]", ""));
						return;
					}
				} else if(a[0].equalsIgnoreCase("add")) {
					if(p.hasPermission("lobby.add")) {
						if(a.length >= 2) {
							if(l.contains(a[1])) {
								p.sendMessage(Main.getMessage("Already_Registered"));
							} else {
								l.add(a[1]);
								Main.c.set("Lobbys", l);
								Main.save();
								p.sendMessage(Main.getMessage("Successfully_Added").replace("%server%", a[1]));
								ServerInfo i = ProxyServer.getInstance().getServerInfo(a[1]);
								if(i == null) {
									p.sendMessage(Main.getMessage("Not_Existing").replace("%server%", a[1]));
								} else if(!ProxyServer.getInstance().getServers().containsValue(i)) {
									p.sendMessage(Main.getMessage("Offline").replace("%server%", a[1]));
								}
							}
						} else {
							p.sendMessage(Main.getMessage("Usage_Lobby_Add"));
						}
						return;
					}
				} else if(a[0].equalsIgnoreCase("remove")) {
					if(a.length >= 2) {
						if(p.hasPermission("lobby.remove")) {
							if(!l.contains(a[1])) {
								p.sendMessage(Main.getMessage("Not_Registered"));
							} else {
								l.remove(a[1]);
								Main.c.set("Lobbys", l);
								Main.save();
								p.sendMessage(Main.getMessage("Successfully_Removed").replace("%server%", a[1]));
							}
							return;
						}
					} else {
						p.sendMessage(Main.getMessage("Usage_Lobby_Remove"));
					}
				} else {
					if(p.hasPermission("lobby.list")) {
						p.sendMessage(Main.getMessage("Usage_Lobby"));
					} else {
						connectToLobby(l, p);
					}
				}
			} else {
				connectToLobby(l, p);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void connectToLobby(List<String> l, ProxiedPlayer p) {
		if(l.contains(p.getServer().getInfo().getName())) {
			p.sendMessage(Main.getMessage("Already_In_Lobby"));
			return;
		}
		if(l != null && !l.isEmpty()) {
			Collections.shuffle(l);
			for(String lobby : l) {
				ServerInfo i = ProxyServer.getInstance().getServerInfo(lobby);
				if(i != null) {
					p.connect(i);
					p.sendMessage(Main.getMessage("Connecting_To_Lobby"));
					return;
				}
			}
		}
		p.sendMessage(Main.getMessage("No_Lobby_Available"));
		if(p.hasPermission("lobby.add")) {
			p.sendMessage(Main.getMessage("Usage_Lobby_Add"));
		}
	}
}
