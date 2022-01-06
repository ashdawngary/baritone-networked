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
import baritone.api.pathing.goals.Goal;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.utils.StrategyResult;

public class AchieveGoalStrategy extends BaseBaritoneStrategy {

  private final Goal target;

  public AchieveGoalStrategy(Baritone b, Goal target) {
    super(b);
    this.target = target;
  }

  @Override
  public void reset() {

  }

  @Override
  public StrategyResult execute(boolean calcFailed, boolean isSafeToCancel) {

    if (target.isInGoal(ctx.playerFeet())) {
      return StrategyResult.SUCCESS;
    }
    if(calcFailed){
      return StrategyResult.FAILURE;
    }
    return StrategyResult.of(new PathingCommand(target,
        PathingCommandType.SET_GOAL_AND_PATH));

  }
}
