package baritone.launch.mixins;

import baritone.utils.accessor.IGuiContainer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({ GuiContainer.class })
public abstract class MixinGuiContainer implements IGuiContainer {
  @Invoker("getSlotAtPosition")
  public abstract Slot getSlot(final int p0, final int p1);

  @Invoker("handleMouseClick")
  public abstract void mouseClick(final Slot p0, final int p1, final int p2, final ClickType p3);

}
