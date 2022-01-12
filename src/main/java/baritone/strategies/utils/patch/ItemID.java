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

package baritone.strategies.utils.patch;


import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemID
{
    public final String identifier;
    public final int damage;
    public final boolean hasDamage;
    public final Item item;
    public final String name;
    
    public ItemID(final String itemIdString) {
        if (itemIdString.matches("^[a-z0-9_]+:\\d{1,5}$")) {
            final String[] idStringParts = itemIdString.split(":");
            this.identifier = idStringParts[0];
            this.damage = Integer.parseInt(idStringParts[0]);
            this.hasDamage = true;
        }
        else {
            this.identifier = itemIdString;
            this.damage = -1;
            this.hasDamage = false;
        }
        this.item = Game.getItem(new ResourceLocation(this.identifier));
        this.name = Game.getItemName(this.item);
    }
    
    public ItemID(final String itemIdString, final int damage) {
        if (itemIdString.contains(":")) {
            throw new RuntimeException("Debug? Why are you here with [" + itemIdString + "] and [" + damage + "]?");
        }
        this.identifier = itemIdString;
        this.damage = damage;
        this.hasDamage = (damage > 0);
        this.item = Game.getItem(new ResourceLocation(this.identifier));
        this.name = Game.getItemName(this.item);
    }
    
    public boolean isValid() {
        return this.item != null;
    }
    
    public boolean hasValidDamage() {
        return this.damage >= 0;
    }
    
    public ItemStack toItemStack(final int stackSize) {
        return new ItemStack(this.item, stackSize, (this.damage > -1) ? this.damage : 0);
    }
    
    public String getDamage() {
        return String.valueOf((this.damage >= 0) ? this.damage : 0);
    }
    
    @Override
    public String toString() {
        return String.format("%s:%s", (this.identifier == null) ? Game.getItemName(null) : this.identifier, Math.max(0, this.damage));
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ItemID) {
            final ItemID otherItem = (ItemID)other;
            return otherItem.item == this.item;
        }
        if (other instanceof Item) {
            return other == this.item;
        }
        if (other instanceof ItemStack) {
            final ItemStack itemStack = (ItemStack)other;
            return ItemStack.areItemStacksEqual(itemStack, this.toItemStack(itemStack.getCount()));
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
