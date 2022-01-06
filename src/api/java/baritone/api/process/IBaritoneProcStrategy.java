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

package baritone.api.process;

import baritone.api.utils.StrategyResult;

/**
 * Represents a node in the behavior tree.  A task to be completed or a computation to choose a task.
 */
public interface IBaritoneProcStrategy {

  /**
   * Resets a strategy
   */
  void reset();

  /**
   * Produces a Pathing command if the strategy permits, returns empty if no strategy yielded.
   * @param calcFailed whether calculation failed.
   * @param isSafeToCancel if it is safe to cancel.
   * @return A command or Option.empty if nothing suffices.
   */
  StrategyResult execute(boolean calcFailed, boolean isSafeToCancel);
}
