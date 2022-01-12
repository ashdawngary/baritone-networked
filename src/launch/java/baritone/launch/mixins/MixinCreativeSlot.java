package baritone.launch.mixins;

import baritone.utils.accessor.ICreativeSlot;
import net.minecraft.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = { "net.minecraft.client.gui.inventory.GuiContainerCreative$CreativeSlot" })
public abstract class MixinCreativeSlot implements ICreativeSlot {
  @Accessor("slot")
  public abstract Slot getInnerSlot();
}
