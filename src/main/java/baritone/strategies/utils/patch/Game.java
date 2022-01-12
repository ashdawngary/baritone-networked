/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package baritone.strategies.utils.patch;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public final class Game {
    public static final Item ITEM_AIR_VIRTUAL = (new Item() {
        public Item init() {
            return this;
        }
        public String getUnlocalizedName() {
            return "Air";
        }
    }).init();

    public Game() {
    }

    public static void addChatMessage(String message) {
        addChatMessage((ITextComponent)(new TextComponentString(message)));
    }

    public static void addChatMessage(ITextComponent message) {
        Minecraft minecraft = Minecraft.getMinecraft();
        GuiNewChat chatGUI = minecraft.ingameGUI.getChatGUI();
        chatGUI.printChatMessage(message);
    }

    public static EntityPlayer getPlayerMP() {
        Minecraft mc = Minecraft.getMinecraft();
        IntegratedServer server = mc.getIntegratedServer();
        return (EntityPlayer)(server != null ? server.getPlayerList().getPlayerByUsername(server.getServerOwner()) : mc.player);
    }



    public static Item getItem(ResourceLocation name) {
        return "air".equals(name) ? ITEM_AIR_VIRTUAL : Item.REGISTRY.getObject(name);
    }

    public static Block getBlock(ResourceLocation name) {
        return (Block)Block.REGISTRY.getObject(name);
    }

    public static String getItemName(Item item) {
        if (item == ITEM_AIR_VIRTUAL) {
            return "air";
        } else {
            ResourceLocation itemName = Item.REGISTRY.getNameForObject(item);
            return itemName == null ? "air" : stripNamespace(itemName.toString());
        }
    }

    public static String getBlockName(Block block) {
        ResourceLocation blockName = Block.REGISTRY.getNameForObject(block);
        return blockName == null ? "air" : stripNamespace(blockName.toString());
    }

    private static String stripNamespace(String itemName) {
        return itemName.startsWith("minecraft:") ? itemName.substring(10) : itemName;
    }

    public static GuiIngame getIngameGui() {
        return Minecraft.getMinecraft().ingameGUI;
    }


}
