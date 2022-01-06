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

package baritone.strategies.farming;

import baritone.Baritone;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalComposite;
import baritone.api.process.IBaritoneProcStrategy;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.utils.StrategyResult;
import baritone.strategies.abstractions.BaseBaritoneStrategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

/**
 * Goes for items on the ground (select your items).
 */
public class PickUpItems extends BaseBaritoneStrategy implements IBaritoneProcStrategy {

  private final List<Item> searching;

  public PickUpItems(Baritone b, Item... searching) {
    super(b);
    this.searching = Arrays.asList(searching);
  }

  @Override
  public void reset() {
  }

  @Override
  public StrategyResult execute(boolean calcFailed, boolean isSafeToCancel) {
    List<Goal> goalz = new ArrayList<>();
    for (Entity entity : ctx.world().loadedEntityList) {
      if (entity instanceof EntityItem && entity.onGround) {
        EntityItem ei = (EntityItem) entity;
        if (searching.contains(ei.getItem().getItem())) {
          // +0.1 because of farmland's 0.9375 dummy height lol
          goalz.add(new GoalBlock(new BlockPos(entity.posX, entity.posY + 0.1, entity.posZ)));
        }
      }
    }

    if(goalz.isEmpty()){
      return StrategyResult.SUCCESS;
    }
    else if(calcFailed){
      return StrategyResult.FAILURE;
    }

    return StrategyResult.of(new PathingCommand(new GoalComposite(goalz.toArray(new Goal[0])),
        PathingCommandType.SET_GOAL_AND_PATH));
  }

  @Override
  public String toString() {
    return String.format("(pickup-items)");
  }
}
