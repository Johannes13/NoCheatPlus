package fr.neatmonster.nocheatplus.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.neatmonster.nocheatplus.NoCheatPlus;
import fr.neatmonster.nocheatplus.players.Permissions;

public class BanCommand extends DelayableCommand {

	public BanCommand(NoCheatPlus plugin) {
		super(plugin, "ban", Permissions.ADMINISTRATION_BAN);
	}

	@Override
	public boolean execute(final CommandSender sender, Command command, String label,
			String[] alteredArgs, long delay) {
		// Args contains "ban" as first arg.
		if (alteredArgs.length < 2) return false;
		final String name = alteredArgs[1];
		final String reason;
		if (alteredArgs.length > 2) reason = join(alteredArgs, 2);
		else reason = "";
		schedule(new Runnable() {
			@Override
			public void run() {
				ban(sender, name, reason);
			}
		}, delay);
		return true;
	}
	
	void ban(CommandSender sender, String name, String reason) {
		Player player = Bukkit.getPlayerExact(name);
		if (player != null)
			player.kickPlayer(reason);
		OfflinePlayer offlinePlayer = Bukkit.getServer().getOfflinePlayer(name);
		offlinePlayer.setBanned(true);
		System.out.println("[NoCheatPlus] (" + sender.getName() + ") Banned " + offlinePlayer.getName() + " : " + reason);
	}

}
