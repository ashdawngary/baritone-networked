package baritone.launch.mixins;

import baritone.utils.accessor.IContainerCreative;
import net.minecraft.client.gui.inventory.GuiContainerCreative.ContainerCreative;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ContainerCreative.class)
public abstract class MixinContainerCreative implements IContainerCreative {
  @Accessor("itemList")
  public abstract NonNullList<ItemStack> getItemsList();

  @Invoker("scrollTo")
  public abstract void scrollToPosition(final float p0);
}
