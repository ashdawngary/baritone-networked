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

package baritone.command.defaults;

import baritone.Baritone;
import baritone.api.IBaritone;
import baritone.api.cache.IWaypoint;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.datatypes.ForWaypoints;
import baritone.api.command.exception.CommandException;
import baritone.api.command.exception.CommandInvalidStateException;
import baritone.api.pathing.goals.GoalNear;
import baritone.api.utils.BetterBlockPos;
import baritone.strategies.abstractions.AchieveGoalStrategy;
import baritone.strategies.abstractions.grammar.SequentialStrategy;
import baritone.strategies.utils.FillChest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;

public class Farm2Command extends Command {

  public Farm2Command(IBaritone baritone) {
    super(baritone, "farm2");
  }

  @Override
  public void execute(String label, IArgConsumer args) throws CommandException {
    args.requireMax(2);
    int range = 0;
    BetterBlockPos origin = null;
    //range
    if (args.has(1)) {
      range = args.getAs(Integer.class);
    }
    //waypoint
    if (args.has(1)) {
      IWaypoint[] waypoints = args.getDatatypeFor(ForWaypoints.INSTANCE);
      IWaypoint waypoint = null;
      switch (waypoints.length) {
        case 0:
          throw new CommandInvalidStateException("No waypoints found");
        case 1:
          waypoint = waypoints[0];
          break;
        default:
          throw new CommandInvalidStateException("Multiple waypoints were found");
      }
      origin = waypoint.getLocation();
    }
    Baritone _b = (Baritone)baritone;

    baritone.getFarmProcessNew().farm(range, origin,
        new SequentialStrategy(
            new AchieveGoalStrategy(_b, new GoalNear(new BlockPos(173,72,300), 2)),
            new FillChest(_b, new BlockPos(173,72,300), Items.CARROT::equals)));


    logDirect("Farming on the new farm2!");

  }

  @Override
  public Stream<String> tabComplete(String label, IArgConsumer args) {
    return Stream.empty();
  }

  @Override
  public String getShortDesc() {
    return "Farm nearby crops";
  }

  @Override
  public List<String> getLongDesc() {
    return Arrays.asList(
        "The farm command starts farming nearby plants. It harvests mature crops and plants new ones.",
        "",
        "Usage:",
        "> farm - farms every crop it can find.",
        "> farm <range> - farm crops within range from the starting position.",
        "> farm <range> <waypoint> - farm crops within range from waypoint."
    );
  }
}
