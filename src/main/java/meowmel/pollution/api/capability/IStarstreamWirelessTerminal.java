package meowmel.pollution.api.capability;

import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Implemented by any TileEntity or GregTech MetaTileEntity that can consume
 * constellation energy from the Starstream wireless network.
 */
public interface IStarstreamWirelessTerminal {

    StarstreamWirelessBinding getStarstreamWirelessBinding();

    /** Permission/configuration hook evaluated before a linker changes binding. */
    default boolean canBindStarstreamNetwork(EntityPlayer player, UUID networkId) {
        return true;
    }

    /** Called after binding or unlinking; use it to refresh custom GUI state. */
    default void onStarstreamNetworkChanged(@Nullable UUID networkId) {}
}
