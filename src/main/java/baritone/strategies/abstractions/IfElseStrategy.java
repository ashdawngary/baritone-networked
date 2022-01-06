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
import baritone.api.utils.IPlayerContext;
import baritone.api.utils.StrategyResult;
import java.util.function.Predicate;

public class IfElseStrategy extends BaseBaritoneStrategy{
  private final Predicate<IPlayerContext> whichOne;
  private final IBaritoneProcStrategy iftrue, iffalse;
  private boolean lastAnswer = false;

  public IfElseStrategy(
      Baritone b, Predicate<IPlayerContext> whichOne,
      IBaritoneProcStrategy iftrue, IBaritoneProcStrategy iffalse) {
    super(b);
    this.whichOne = whichOne;
    this.iftrue = iftrue;
    this.iffalse = iffalse;
  }

  @Override
  public void reset() {
    iftrue.reset();
    iffalse.reset();
  }

  @Override
  public StrategyResult execute(boolean calcFailed, boolean isSafeToCancel) {
    boolean result = whichOne.test(ctx);
    StrategyResult answer;
    if(result){
       answer = iftrue.execute(lastAnswer == result && calcFailed, isSafeToCancel );
    }
    else{
      answer = iffalse.execute(lastAnswer == result && calcFailed, isSafeToCancel);
    }

    lastAnswer = result;
    return answer;
  }

  @Override
  public String toString() {
    return String.format("(if/else %s %s)", iftrue, iffalse);
  }
}
