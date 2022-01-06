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
import baritone.api.process.IBaritoneProcStrategy;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import baritone.api.utils.StrategyResult;
import baritone.api.utils.input.Input;
import baritone.strategies.abstractions.BaseBaritoneStrategy;
import java.util.Optional;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class BoneMealStrategy extends BaseBaritoneStrategy implements IBaritoneProcStrategy {

  protected final BlockPos pos;

  protected BoneMealStrategy(Baritone b, BlockPos toSpamOn) {
    super(b);
    this.pos = toSpamOn;
  }

  @Override
  public void reset() {

  }

  @Override
  public StrategyResult execute(boolean calcFailed, boolean isSafeToCancel) {
    Optional<Rotation> rot = RotationUtils.reachable(ctx, pos);
    if (rot.isPresent() && isSafeToCancel && baritone.getInventoryBehavior()
        .throwaway(true, this::isBoneMeal)) {
      baritone.getLookBehavior().updateTarget(rot.get(), true);
      if (ctx.isLookingAt(pos)) {
        baritone.getInputOverrideHandler().setInputForceState(Input.CLICK_RIGHT, true);
      }
      return StrategyResult.of(new PathingCommand(null, PathingCommandType.REQUEST_PAUSE));
    }
    return StrategyResult.FAILURE;
  }

  private boolean isBoneMeal(ItemStack stack) {
    return !stack.isEmpty() && stack.getItem() instanceof ItemDye
        && EnumDyeColor.byDyeDamage(stack.getMetadata()) == EnumDyeColor.WHITE;
  }

  @Override
  public String toString() {
    return String.format("(bonemeal %s)", pos);
  }
}