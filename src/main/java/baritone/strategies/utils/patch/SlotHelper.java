package baritone.strategies.utils.patch;//


import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class SlotHelper {
  private final Minecraft mc;

  public SlotHelper( Minecraft mc) {
    this.mc = mc;
  }

  public boolean currentScreenIsContainer() {
    return this.mc.currentScreen != null && this.mc.currentScreen instanceof GuiContainer;
  }

  public boolean currentScreenIsInventory() {
    return this.mc.currentScreen != null && this.mc.currentScreen instanceof GuiInventory;
  }

  private boolean noScreenInGame() {
    return this.mc.currentScreen == null && this.mc.player != null && this.mc.player.inventory != null && this.mc.player.inventory.mainInventory != null;
  }

  public GuiContainer getGuiContainer() {
    return this.mc.currentScreen instanceof GuiContainer ? (GuiContainer)this.mc.currentScreen : null;
  }

  public int getContainerSize() {
    if (!this.currentScreenIsContainer()) {
      return 0;
    } else {
      GuiContainer containerGui = this.getGuiContainer();
      return containerGui instanceof GuiContainerCreative ? 600 : containerGui.inventorySlots.inventorySlots.size();
    }
  }

  protected void survivalInventorySlotClick(SlotClick click) {
    this.survivalInventorySlotClick(click.craftingGui, click.slot, click.slotNumber, click.button, click.clickType);
  }

  public void survivalInventorySlotClick(GuiContainer craftingGui, Slot slot, int slotNumber, int button, boolean shift) {
    this.survivalInventorySlotClick(craftingGui, slot, slotNumber, button, shift ? ClickType.QUICK_MOVE : ClickType.PICKUP);
  }

  private void survivalInventorySlotClick(GuiContainer craftingGui, Slot slot, int slotNumber, int button, ClickType clickType) {
    try {
      if (craftingGui == null || craftingGui instanceof GuiContainerCreative) {
        return;
      }

      ((IGuiContainer)craftingGui).mouseClick(slot, slotNumber, button, clickType);
    } catch (Exception var7) {
    }

  }

  public void containerSlotClick(int slotNumber, int button, boolean shift) {
    try {
      if (!this.currentScreenIsContainer()) {
        if (this.noScreenInGame() && slotNumber >= 1 && slotNumber <= 9) {
          this.mc.player.inventory.currentItem = slotNumber - 1;
        }

        return;
      }

      GuiContainer containerGui = this.getGuiContainer();
      Container slots = containerGui.inventorySlots;
      Slot slot;
      if (containerGui instanceof GuiContainerCreative) {
        throw new IllegalArgumentException("creative gui");
      } else if (slotNumber >= 0 && slotNumber < slots.inventorySlots.size() || slotNumber == -999) {
        slot = slotNumber == -999 ? null : slots.getSlot(slotNumber);
        ((IGuiContainer)containerGui).mouseClick(slot, slotNumber, button, shift ? ClickType.QUICK_MOVE : ClickType.PICKUP);
      }
    } catch (Exception var8) {
      var8.printStackTrace();
    }

  }





  public int getSlotContaining(ItemID itemId, int startSlot) {
    try {
      if (this.currentScreenIsContainer()) {
        GuiContainer containerGui = this.getGuiContainer();
        if (containerGui instanceof GuiContainerCreative) {
          int inventoryIndex = this.searchInventoryFor(itemId, startSlot, this.mc.player.inventoryContainer);
          if (inventoryIndex < 0) {
            return this.searchCreativeTabsFor(itemId, startSlot);
          }

          return inventoryIndex;
        }

        return this.searchInventoryFor(itemId, startSlot, containerGui.inventorySlots);
      }

      if (this.noScreenInGame()) {
        for(int slot = 0; slot < 9; ++slot) {
          if (stackMatchesID(itemId, (ItemStack)this.mc.player.inventory.mainInventory.get(slot))) {
            return slot + 1;
          }
        }
      }
    } catch (Exception var5) {
      var5.printStackTrace();
    }

    return -1;
  }

  protected int searchInventoryFor(ItemID itemId, int startSlot, Container inventorySlots) {
    List<Slot> itemStacks = inventorySlots.inventorySlots;

    for(int slotContaining = startSlot; slotContaining < itemStacks.size(); ++slotContaining) {
      ItemStack slotStack = ((Slot)itemStacks.get(slotContaining)).getStack();
      if (stackMatchesID(itemId, slotStack)) {
        return slotContaining;
      }
    }

    return -1;
  }

  protected int searchCreativeTabsFor(ItemID itemId, int startSlot) {
    int pageNumber = 100;
    CreativeTabs[] var4 = CreativeTabs.CREATIVE_TAB_ARRAY;
    int var5 = var4.length;

    for(int var6 = 0; var6 < var5; ++var6) {
      CreativeTabs creativeTab = var4[var6];
      if (creativeTab != CreativeTabs.INVENTORY && creativeTab != CreativeTabs.SEARCH) {
        NonNullList<ItemStack> itemStacks = NonNullList.create();
        creativeTab.displayAllRelevantItems(itemStacks);

        for(int stackIndex = 0; stackIndex < itemStacks.size(); ++stackIndex) {
          if (stackMatchesID(itemId, (ItemStack)itemStacks.get(stackIndex)) && pageNumber + stackIndex >= startSlot) {
            return pageNumber + stackIndex;
          }
        }
      }

      pageNumber += 100;
    }

    return -1;
  }

  private static boolean stackMatchesID(ItemID itemId, ItemStack slotStack) {
    return slotStack == null && itemId.item == null || slotStack != null && slotStack.getItem() == itemId.item && (itemId.damage == -1 || itemId.damage == slotStack.getMetadata());
  }

  public ItemStack getSlotStack(int slotId) {
    try {
      if (this.currentScreenIsContainer()) {
        GuiContainer containerGui = this.getGuiContainer();
        if (containerGui instanceof GuiContainerCreative && slotId >= 100) {
          return this.getStackFromCreativeTabs(slotId);
        }

        return this.getStackFromSurvivalInventory(slotId, containerGui.inventorySlots);
      }

      if (this.noScreenInGame() && slotId >= 1 && slotId <= 9) {
        return (ItemStack)this.mc.player.inventory.mainInventory.get(slotId - 1);
      }
    } catch (Exception var3) {
      var3.printStackTrace();
    }

    return null;
  }

  protected ItemStack getStackFromCreativeTabs(int slotId) {
    int pageNumber = 100;
    CreativeTabs[] var3 = CreativeTabs.CREATIVE_TAB_ARRAY;
    int var4 = var3.length;

    for(int var5 = 0; var5 < var4; ++var5) {
      CreativeTabs creativeTab = var3[var5];
      if (creativeTab != CreativeTabs.INVENTORY && creativeTab != CreativeTabs.SEARCH) {
        NonNullList<ItemStack> itemStacks = NonNullList.create();
        creativeTab.displayAllRelevantItems(itemStacks);

        for(int stackIndex = 0; stackIndex < itemStacks.size(); ++stackIndex) {
          int virtualSlotId = pageNumber + stackIndex;
          if (virtualSlotId == slotId) {
            return (ItemStack)itemStacks.get(stackIndex);
          }

          if (virtualSlotId > slotId) {
            return null;
          }
        }
      }

      pageNumber += 100;
    }

    return null;
  }

  protected ItemStack getStackFromSurvivalInventory(int slotId, Container survivalInventory) {
    List<Slot> itemStacks = survivalInventory.inventorySlots;
    if (slotId >= 0 && slotId < itemStacks.size()) {
      ItemStack slotStack = ((Slot)itemStacks.get(slotId)).getStack();
      if (slotStack != null) {
        return slotStack;
      }
    }

    return null;
  }

  public Slot getMouseOverSlot(GuiContainer guiContainer, int mouseX, int mouseY) {
    try {
      Slot slot = ((IGuiContainer)guiContainer).getSlot(mouseX, mouseY);
      if (guiContainer instanceof GuiContainerCreative) {
        GuiContainerCreative creativeContainerGui = (GuiContainerCreative)guiContainer;
        if (creativeContainerGui.getSelectedTabIndex() == CreativeTabs.INVENTORY.getIndex() && slot instanceof ICreativeSlot) {
          return ((ICreativeSlot)slot).getInnerSlot();
        }
      }

      return slot;
    } catch (Exception var6) {
      return null;
    }
  }

  public static int getSlotIndex(GuiContainer guiContainer, Slot mouseOverSlot) {
    if (mouseOverSlot == null) {
      return -1;
    } else {
      int slotNumber = mouseOverSlot.slotNumber;
      if (guiContainer instanceof GuiContainerCreative) {
        if (slotNumber < 45) {
          throw new IllegalArgumentException("creative gui cringe");
        } else {
          slotNumber -= 9;
        }
      }

      return slotNumber;
    }
  }



  private static boolean isInRange(int value, int start, int rangeLength) {
    return value >= start && value < start + rangeLength;
  }

  public static class SlotClick {
    public GuiContainer craftingGui;
    public Slot slot;
    public int slotNumber;
    public int button;
    public ClickType clickType;

    public SlotClick(GuiContainer craftingGui, Slot slot, int slotNumber, int button, boolean shift) {
      this(craftingGui, slot, slotNumber, button, shift ? ClickType.QUICK_MOVE : ClickType.PICKUP);
    }

    public SlotClick(GuiContainer craftingGui, Slot slot, int slotNumber, int button, ClickType clickType) {
      this.craftingGui = craftingGui;
      this.slot = slot;
      this.slotNumber = slotNumber;
      this.button = button;
      this.clickType = clickType;
    }

    public void execute(SlotHelper slots) {
      slots.survivalInventorySlotClick(this);
    }

    public String toString() {
      return String.format("SlotClick[%s,%s,%s,%s]", this.slot != null ? this.slot.slotNumber : "-", this.slotNumber, this.button, this.clickType);
    }
  }
}
