package baritone.launch.mixins;

import baritone.utils.accessor.IGuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ GuiContainerCreative.class })
public abstract class MixinGuiContainerCreative implements IGuiContainerCreative {
  @Accessor("basicInventory")
  static InventoryBasic getCreativeInventory() {
    throw new NotImplementedException("gci mixin not applied.");
  }

  @Accessor("destroyItemSlot")
  public abstract Slot getBinSlot();

  @Invoker("setCurrentCreativeTab")
  public abstract void setCreativeTab(final CreativeTabs p0);

  @Invoker("handleMouseClick")
  public abstract void mouseClick(final Slot p0, final int p1, final int p2, final ClickType p3);

  @Accessor("currentScroll")
  public abstract void setScrollPosition(final float p0);

}
