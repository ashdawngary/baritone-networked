package baritone.utils.accessor;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import org.apache.commons.lang3.NotImplementedException;


public interface IGuiContainerCreative
{

    static InventoryBasic getCreativeInventory() {
        throw new NotImplementedException("gci mixin not applied.");
    }


    Slot getBinSlot();
    

    void setCreativeTab(final CreativeTabs p0);
    

    void mouseClick(final Slot p0, final int p1, final int p2, final ClickType p3);
    

    void setScrollPosition(final float p0);
}
