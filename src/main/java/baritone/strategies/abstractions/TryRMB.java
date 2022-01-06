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

package baritone.strategies.abstractions;

import baritone.Baritone;
import baritone.api.process.IBaritoneProcStrategy;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import baritone.api.utils.StrategyResult;
import baritone.api.utils.input.Input;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;

public class TryRMB extends BaseBaritoneStrategy implements IBaritoneProcStrategy {

  protected final BlockPos pos;
  protected boolean hasRC = false;

  protected TryRMB(Baritone b, BlockPos toBreak) {
    super(b);
    this.pos = toBreak;
  }

  @Override
  public void reset() {
    hasRC = false;
  }

  @Override
  public StrategyResult execute(boolean calcFailed, boolean isSafeToCancel) {
    if(hasRC){
      return StrategyResult.SUCCESS;
    }
    Optional<Rotation> rot = RotationUtils.reachable(ctx, pos);
    if (rot.isPresent() && isSafeToCancel ) {
      baritone.getLookBehavior().updateTarget(rot.get(), true);
      if (ctx.isLookingAt(pos)) {
        baritone.getInputOverrideHandler().setInputForceState(Input.CLICK_RIGHT, true);
        hasRC = true;
      }
      return StrategyResult.of(new PathingCommand(null, PathingCommandType.REQUEST_PAUSE));
    }
    return StrategyResult.FAILURE;
  }
}
