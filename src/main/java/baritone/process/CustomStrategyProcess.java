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

package baritone.process;

import baritone.Baritone;
import baritone.api.process.IBaritoneProcStrategy;
import baritone.api.process.ICustomStrategyProcess;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.utils.StrategyResult;
import baritone.utils.BaritoneProcessHelper;

public class CustomStrategyProcess extends BaritoneProcessHelper implements ICustomStrategyProcess {

  private boolean isActive;
  private IBaritoneProcStrategy currentStrtaegy;
  private final PathingCommand NOOP = new PathingCommand(null, PathingCommandType.CANCEL_AND_SET_GOAL);

  public CustomStrategyProcess(Baritone delegate) {
    super(delegate);
    isActive = false;
  }

  @Override
  public boolean isActive() {
    return isActive;
  }

  @Override
  public PathingCommand onTick(boolean calcFailed, boolean isSafeToCancel) {
    if (currentStrtaegy == null) {
      onLost();
      return NOOP;
    }

    StrategyResult s = currentStrtaegy.execute(calcFailed, isSafeToCancel);
    if (s.isFail()) {
      // failure not handled by strategy, we dont have control.
      onLost();
      return NOOP;
    }
    else if(s.isSuccess()){
      // the mission is done!  We got em
      onSuccess();
      return NOOP;
    }
    else if(s.isPresent()){
      // get the next pathing packet
      return s.get();
    }
    else{
      throw new IllegalStateException("Strategy result is none of FAIL, SUCCESS, or custom GOAL (SR= "+s+")");
    }
  }

  private void onSuccess() {
    logDirect("strategy yielded on success.");
    onLostControl();
  }

  private void onLost() {
    logDirect("strategy yielded a failure.");
    onLostControl();
  }

  @Override
  public void onLostControl() {
    isActive = false;
  }

  @Override
  public String displayName0() {
    if (currentStrtaegy == null) {
      return "Strategy Executor (current empty)";
    }
    return "Strategy: " + this.currentStrtaegy;
  }

  @Override
  public void initStrategy(IBaritoneProcStrategy strategy) {
    currentStrtaegy = strategy;
    isActive = true;
  }
}
