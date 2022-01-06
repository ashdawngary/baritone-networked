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
import baritone.api.utils.StrategyResult;
import java.util.Objects;

public class PrioritySequentialStrategy extends BaseBaritoneStrategy implements IBaritoneProcStrategy {
  private final IBaritoneProcStrategy[] strategies;
  private int lastCalledIndex;
  private final boolean forceUpdate;

  public PrioritySequentialStrategy(Baritone b, IBaritoneProcStrategy... strategies) {
    this(b, false, strategies);
  }

  public PrioritySequentialStrategy(Baritone b,boolean forceUpdate, IBaritoneProcStrategy... strategies) {
    super(b);
    this.strategies = Objects.requireNonNull(strategies);
    lastCalledIndex = -1;
    this.forceUpdate = forceUpdate;

  }

  @Override
  public void reset() {
    for(IBaritoneProcStrategy strat : strategies){
      strat.reset();
    }
    lastCalledIndex = -1;
  }

  @Override
  public StrategyResult execute(boolean calcFailed, boolean isSafeToCancel) {
    if(forceUpdate) {
      this.reset();
    }
    for(int currentStratIndex = 0; currentStratIndex < strategies.length;currentStratIndex++){
      StrategyResult opt;

      // prevents sending mis-informed calculation responses to idemponent strategies.
      if(lastCalledIndex == currentStratIndex) {
        opt = strategies[currentStratIndex].execute(calcFailed, isSafeToCancel);
      }
      else{
        opt = strategies[currentStratIndex].execute(false, isSafeToCancel);
      }

      if (opt.isPresent()) {
        lastCalledIndex = currentStratIndex;
        return opt;
      }
    }



    return StrategyResult.SUCCESS;

  }
}
