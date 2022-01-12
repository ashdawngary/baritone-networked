package baritone.utils.accessor;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface IContainerCreative
{
    NonNullList<ItemStack> getItemsList();

    void scrollToPosition(final float p0);
}
