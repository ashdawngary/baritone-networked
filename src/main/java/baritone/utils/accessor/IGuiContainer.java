package baritone.utils.accessor;

import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;

public interface IGuiContainer
{
    Slot getSlot(final int p0, final int p1);

    void mouseClick(final Slot p0, final int p1, final int p2, final ClickType p3);
}
