package fr.neatmonster.nocheatplus.checks.moving;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.neatmonster.nocheatplus.actions.ParameterName;
import fr.neatmonster.nocheatplus.checks.Check;
import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.checks.ViolationData;

/*
 * M"""""`'"""`YM                            MM"""""""`YM                   dP                  dP            
 * M  mm.  mm.  M                            MM  mmmmm  M                   88                  88            
 * M  MMM  MMM  M .d8888b. 88d888b. .d8888b. M'        .M .d8888b. .d8888b. 88  .dP  .d8888b. d8888P .d8888b. 
 * M  MMM  MMM  M 88'  `88 88'  `88 88ooood8 MM  MMMMMMMM 88'  `88 88'  `"" 88888"   88ooood8   88   Y8ooooo. 
 * M  MMM  MMM  M 88.  .88 88       88.  ... MM  MMMMMMMM 88.  .88 88.  ... 88  `8b. 88.  ...   88         88 
 * M  MMM  MMM  M `88888P' dP       `88888P' MM  MMMMMMMM `88888P8 `88888P' dP   `YP `88888P'   dP   `88888P' 
 * MMMMMMMMMMMMMM                            MMMMMMMMMMMM                                                     
 * 
 * M""MMMMM""M          dP       oo                   dP          
 * M  MMMMM  M          88                            88          
 * M  MMMMP  M .d8888b. 88d888b. dP .d8888b. dP    dP 88 .d8888b. 
 * M  MMMM' .M 88ooood8 88'  `88 88 88'  `"" 88    88 88 88ooood8 
 * M  MMP' .MM 88.  ... 88    88 88 88.  ... 88.  .88 88 88.  ... 
 * M     .dMMM `88888P' dP    dP dP `88888P' `88888P' dP `88888P' 
 * MMMMMMMMMMM                                                    
 */
/**
 * This check does the exact same thing as the MorePacket check but this one works for players inside vehicles.
 */
public class MorePacketsVehicle extends Check {

    /**
     * The usual number of packets per timeframe.
     * 
     * 20 would be for perfect internet connections, 22 is good enough.
     */
    private final static int packetsPerTimeframe = 22;

    /**
     * Instantiates a new more packet vehicle check.
     */
    public MorePacketsVehicle() {
        super(CheckType.MOVING_MOREPACKETSVEHICLE);
    }

    /**
     * Checks a player.
     * 
     * (More information on the MorePacket class.)
     * 
     * @param player
     *            the player
     * @param from
     *            the from
     * @param to
     *            the to
     * @return the location
     */
    public Location check(final Player player, final Location from, final Location to) {
    	// Take time once, first:
    	final long time = System.currentTimeMillis();
    	
        final MovingData data = MovingData.getData(player);

        Location newTo = null;

        if (data.morePacketsVehicleSetback == null)
            data.morePacketsVehicleSetback = from;

        // Take a packet from the buffer.
        data.morePacketsVehicleBuffer--;

        // Player used up buffer, he fails the check.
        if (data.morePacketsVehicleBuffer < 0) {
            data.morePacketsVehiclePackets = -data.morePacketsVehicleBuffer;

            // Increment violation level.
            data.morePacketsVehicleVL = -data.morePacketsVehicleBuffer;

            // Execute whatever actions are associated with this check and the violation level and find out if we should
            // cancel the event.
            if (executeActions(player, data.morePacketsVehicleVL, -data.morePacketsVehicleBuffer,
                    MovingConfig.getConfig(player).morePacketsVehicleActions))
                newTo = data.morePacketsVehicleSetback;
        }

        if (data.morePacketsVehicleLastTime + 1000 < time) {
            // More than 1 second elapsed, but how many?
            final double seconds = (time - data.morePacketsVehicleLastTime) / 1000D;

            // For each second, fill the buffer.
            data.morePacketsVehicleBuffer += packetsPerTimeframe * seconds;

            // If there was a long pause (maybe server lag?), allow buffer to grow up to 100.
            if (seconds > 2) {
                if (data.morePacketsVehicleBuffer > 100)
                    data.morePacketsVehicleBuffer = 100;
            } else if (data.morePacketsVehicleBuffer > 50)
                // Only allow growth up to 50.
                data.morePacketsVehicleBuffer = 50;

            // Set the new "last" time.
            data.morePacketsVehicleLastTime = time;

            // Set the new "setback" location.
            if (newTo == null)
                data.morePacketsVehicleSetback = from;
        } else if (data.morePacketsVehicleLastTime > time)
            // Security check, maybe system time changed.
            data.morePacketsVehicleLastTime = time;

        if (newTo == null)
            return null;

        // Compose a new location based on coordinates of "newTo" and viewing direction of "event.getTo()" to allow the
        // player to look somewhere else despite getting pulled back by NoCheatPlus.
        return new Location(player.getWorld(), newTo.getX(), newTo.getY(), newTo.getZ(), to.getYaw(), to.getPitch());
    }

    /* (non-Javadoc)
     * @see fr.neatmonster.nocheatplus.checks.Check#getParameter(fr.neatmonster.nocheatplus.actions.ParameterName,
     * org.bukkit.entity.Player)
     */
    @Override
    public String getParameter(final ParameterName wildcard, final ViolationData violationData) {
        if (wildcard == ParameterName.PACKETS)
            return String.valueOf(MovingData.getData(violationData.player).morePacketsVehiclePackets);
        else
            return super.getParameter(wildcard, violationData);
    }
}
