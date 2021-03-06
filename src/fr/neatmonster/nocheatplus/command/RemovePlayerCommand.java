package fr.neatmonster.nocheatplus.command;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.neatmonster.nocheatplus.NoCheatPlus;
import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.checks.ViolationHistory;
import fr.neatmonster.nocheatplus.players.Permissions;
import fr.neatmonster.nocheatplus.utilities.CheckUtils;

public class RemovePlayerCommand extends NCPCommand {

	public RemovePlayerCommand(NoCheatPlus plugin) {
		super(plugin, "removeplayer", Permissions.ADMINISTRATION_REMOVEPLAYER, new String[]{
			"remove",	
		});
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (args.length < 2 || args.length > 3) return false;
		String playerName = args[1];
		final CheckType checkType;
		if (args.length == 3){
			try{
				checkType = CheckType.valueOf(args[2].toUpperCase().replace('-', '_').replace('.', '_'));
			} catch (Exception e){
				sender.sendMessage(TAG + "Could not interpret: " + args[2]);
				sender.sendMessage(TAG + "Check type should be one of: " + CheckUtils.join(Arrays.asList(CheckType.values()), " | "));
				return true;
			}
		}
		else checkType = CheckType.ALL;
		
		final Player player = Bukkit.getPlayerExact(playerName);
		if (player != null) playerName = player.getName();
		
		ViolationHistory hist = ViolationHistory.getHistory(playerName, false);
		boolean histRemoved = false;
		if (hist != null) histRemoved = hist.remove(checkType);
		
		final boolean dataRemoved = CheckType.removeData(playerName, checkType);
		
		if (dataRemoved || histRemoved){
			String which;
			if (dataRemoved && histRemoved) which = "data and history";
			else if (dataRemoved) which = "data";
			else which = "history";
			sender.sendMessage(TAG + "Removed " + which + " (" + checkType + "): " + playerName);
		}
		else
			sender.sendMessage(TAG + "Nothing found (" + checkType + ", exact spelling): " + playerName);
		return true;
	}

}
