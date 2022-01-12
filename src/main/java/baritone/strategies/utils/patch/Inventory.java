package baritone.strategies.utils.patch;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Helps control inventories and movements inside them. Contains only static functions.
 */
public class Inventory {
  private static Minecraft mc = Minecraft.getMinecraft();
  /**
   * closes the currently opened gui
   */
  public static void closeGui() {

    mc.displayGuiScreen(null);
  }

  /**
   * opens the inventory of the player
   */
  public static void openInv() {
    mc.displayGuiScreen(new GuiInventory(mc.player));
  }


  /**
   * These function deal with in inventory control
   */
  public static void slotClick(SlotHelper slotHelper, int slot, int mouseButton, boolean shift){
    slotHelper.containerSlotClick(slot, mouseButton, shift);
  }

  public static void slotClick(SlotHelper slotHelper, int slot){
    slotHelper.containerSlotClick(slot, 0, false);
  }


  /**
   * gets information about the item in a specific slot.
   * @param slot
   * @return
   */
  public static ItemStack slotGet(SlotHelper slotHelper, int slot){
    return slotHelper.getSlotStack(slot);
  }

  public static String slotGetName(SlotHelper slotHelper,int slot){
    return slotHelper.getSlotStack(slot).getDisplayName();
  }

  public static int slotGetNumber(SlotHelper slotHelper,int slot){
    return slotHelper.getSlotStack(slot).getCount();
  }


  public static List<Integer> findItemLoc(SlotHelper slotHelper, int startPos, int endPos, String itemName){
    List<Integer> items = new ArrayList<>();
    for(int i = startPos; i < endPos; i++){
      if(Inventory.slotGetName(slotHelper,i).equals(itemName))
        items.add(i);
    }
    return items;
  }

  public static List<ItemStack> findItemInfo(SlotHelper slotHelper, int startPos, int endPos, String itemName){
    List<ItemStack> items = new ArrayList<>();
    for(int i = startPos; i < endPos; i++){
      if(Inventory.slotGetName(slotHelper,i).equals(itemName)) {
        items.add(Inventory.slotGet(slotHelper,i));
      }
    }
    return items;
  }



  public static List<String> readSlot(SlotHelper slotHelper, Minecraft mc, int slot){
    if(slotHelper.getSlotStack(slot).isEmpty()) return new ArrayList<>();
    return slotHelper.getSlotStack(slot).getTooltip(mc.player, () -> true);
  }


  /**
   * These functions deal with hotbar control.
   */
  public static void hotbarRight(){
    aimove(-1);
  }

  public static void hotbarLeft(){
    aimove(1);
  }

  public static void hotbarMove(int moveBy) {
    aimove(moveBy);
  }

  private static void aimove(int os){
    mc.player.inventory.changeCurrentItem(os);
  }

  /**
   * @param pos between 1 and 9. The number that hotbar corrosponds to.
   */
  public static void hotbarSlot(int pos) {
    EntityPlayerSP psp = mc.player;
    if(pos > 0 && pos < 10){
      psp.inventory.currentItem = pos-1;
    }
  }

  public static Item hotbarGet(int pos){
    Item i = Game.getItem(new ResourceLocation("name"));
    return i;
  }
  public static int hotbarCurrent(){
    return mc.player.inventory.currentItem + 1;
  }
}