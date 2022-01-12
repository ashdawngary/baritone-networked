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

package baritone.strategies.utils;

import baritone.Baritone;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.utils.Rotation;
import baritone.api.utils.StrategyResult;
import baritone.api.utils.input.Input;
import baritone.strategies.abstractions.BaseBaritoneStrategy;
import baritone.strategies.utils.patch.Inventory;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;

// 172 72 300

public class FillChest extends BaseBaritoneStrategy {

  private StateTimer<DepositState> cState;
  private BlockPos pos;
  private Predicate<Item> toFill;
  public FillChest(Baritone b, BlockPos chestPos, Predicate<Item> toDeposit) {
    super(b);
    cState = new StateTimer<>(DepositState.TARGETING);
    pos = chestPos;
    toFill = Objects.requireNonNull(toDeposit);

  }

  @Override
  public void reset() {
    cState = new StateTimer<>(DepositState.TARGETING);
  }

  @Override
  public StrategyResult execute(boolean calcFailed, boolean isSafeToCancel) {
    if(calcFailed){
      return StrategyResult.FAILURE;
    }

    switch (cState.getState()){
      case TARGETING:
        Optional<Rotation> rot = RotSearch.findVantage(ctx, pos,SidePreference.ANY); //RotationUtils.reachable(ctx, pos);
        if (rot.isPresent() && isSafeToCancel ) {
          baritone.getLookBehavior().updateTarget(rot.get(), true);
          cState.transition(DepositState.OPENING);
          return StrategyResult.of(new PathingCommand(null, PathingCommandType.REQUEST_PAUSE));
        }
        return StrategyResult.FAILURE;

      case OPENING:
        if (ctx.isLookingAt(pos)) {
          baritone.getInputOverrideHandler().setInputForceState(Input.CLICK_RIGHT, true);
          cState.transition(DepositState.TRANSFER);
        }
        return StrategyResult.of(new PathingCommand(null, PathingCommandType.REQUEST_PAUSE));
      case TRANSFER:
        cState.transition(DepositState.CLOSING);
        return StrategyResult.of(new PathingCommand(null, PathingCommandType.REQUEST_PAUSE));
      case CLOSING:
        Inventory.closeGui();
        return StrategyResult.SUCCESS;
    }

    throw new IllegalStateException("fell thru");

  }

  private enum DepositState{
    TARGETING,
    OPENING,
    TRANSFER,
    CLOSING
  }

}
