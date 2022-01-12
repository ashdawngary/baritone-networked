package baritone.strategies.utils.patch;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin({ GuiContainer.class })
public interface IGuiContainer
{
    @Invoker("getSlotAtPosition")
    Slot getSlot(final int p0, final int p1);
    
    @Invoker("handleMouseClick")
    void mouseClick(final Slot p0, final int p1, final int p2, final ClickType p3);
}
