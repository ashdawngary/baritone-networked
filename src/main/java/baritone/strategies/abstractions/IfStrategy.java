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

public class IfStrategy extends BaseBaritoneStrategy implements IBaritoneProcStrategy {

  private final Predicate<IPlayerContext> question;
  private final IBaritoneProcStrategy iftrue;
  private boolean lastAnswer = false;

  public IfStrategy(Baritone b, Predicate<IPlayerContext> shouldApply,
      IBaritoneProcStrategy iftrue) {
    super(b);
    this.question = shouldApply;
    this.iftrue = iftrue;
  }

  @Override
  public void reset() {
    iftrue.reset();
    lastAnswer = false;
  }

  @Override
  public StrategyResult execute(boolean calcFailed, boolean isSafeToCancel) {
    if (question.test(ctx)) {
      StrategyResult tr = iftrue.execute(lastAnswer && calcFailed, isSafeToCancel);
      lastAnswer = true;
      return tr;
    }
    lastAnswer = false;
    return StrategyResult.FAILURE;
  }

  @Override
  public String toString() {
    return String.format("(if/else %s no-op)", iftrue);
  }
}
