package baritone.strategies.utils.patch;

import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(targets = { "net.minecraft.client.gui.inventory.GuiContainerCreative$CreativeSlot" })
public interface ICreativeSlot
{
    @Accessor("slot")
    Slot getInnerSlot();
}
