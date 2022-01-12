package baritone.strategies.utils.patch;

import net.minecraft.client.gui.inventory.GuiContainerCreative.ContainerCreative;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin({ ContainerCreative.class })
public interface IContainerCreative
{
    @Accessor("itemList")
    NonNullList<ItemStack> getItemsList();
    
    @Invoker("scrollTo")
    void scrollToPosition(final float p0);
}
