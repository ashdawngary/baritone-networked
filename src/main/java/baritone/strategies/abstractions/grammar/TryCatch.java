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

package baritone.strategies.abstractions.grammar;

import baritone.Baritone;
import baritone.api.process.IBaritoneProcStrategy;
import baritone.api.utils.StrategyResult;
import baritone.strategies.abstractions.BaseBaritoneStrategy;

/**
 * Stateful try catch.
 */
public class TryCatch extends BaseBaritoneStrategy {

  private final IBaritoneProcStrategy toTryOn;
  private final IBaritoneProcStrategy toCatchWith;
  private boolean isCrashed;

  public TryCatch(Baritone b, IBaritoneProcStrategy toTryOn,
      IBaritoneProcStrategy toCatchWith) {
    super(b);
    this.toTryOn = toTryOn;
    this.toCatchWith = toCatchWith;
  }

  @Override
  public void reset() {
    toTryOn.reset();
    toCatchWith.reset();
    isCrashed = false;
  }

  @Override
  public StrategyResult execute(boolean calcFailed, boolean isSafeToCancel) {
    if(isCrashed){
      return toCatchWith.execute(calcFailed, isSafeToCancel);
    }
    else{
      StrategyResult res = toTryOn.execute(calcFailed, isSafeToCancel);
      if(!res.isFail()){
        return res;
      }

      isCrashed = true;
      // try to catch on the fly
      res = toCatchWith.execute(false, isSafeToCancel);
      return res;
    }
  }
}
