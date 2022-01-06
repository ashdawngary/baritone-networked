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
import baritone.api.pathing.goals.GoalComposite;
import baritone.api.process.IBaritoneProcStrategy;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.utils.IPlayerContext;
import baritone.api.utils.StrategyResult;
import baritone.cache.WorldScanner;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class ScanAndAct extends BaseBaritoneStrategy implements IBaritoneProcStrategy {

  private final List<Block> scan;
  private final HashMap<Integer, BiFunction<BlockPos, IPlayerContext, Boolean>> posFilterer;
  private final HashMap<Integer, BiFunction<BlockPos, IPlayerContext, IBaritoneProcStrategy>> onBlockStrategy;
  private final HashMap<Integer, BiFunction<BlockPos, IPlayerContext, Consumer<List<Goal>>>> goalMaker;
  private List<IBaritoneProcStrategy> currentPriorities;
  private PrioritySequentialStrategy scanner;
  private int tickCount;
  private List<BlockPos> locations;

  public ScanAndAct(Baritone b, List<Block> scan,
      HashMap<Integer, BiFunction<BlockPos, IPlayerContext, Boolean>> positionFilterMap,
      HashMap<Integer, BiFunction<BlockPos, IPlayerContext, Consumer<List<Goal>>>> goalAggregator,
      HashMap<Integer, BiFunction<BlockPos, IPlayerContext, IBaritoneProcStrategy>> onTheFlyStrategy) {
    super(b);

    currentPriorities = new LinkedList<>();
    this.scan = scan;
    this.posFilterer = positionFilterMap;
    this.onBlockStrategy = onTheFlyStrategy;
    this.goalMaker = goalAggregator;

  }

  @Override
  public void reset() {
    locations = null;
    currentPriorities = new LinkedList<>();
  }

  @Override
  public StrategyResult execute(boolean calcFailed, boolean isSafeToCancel) {

    if (Baritone.settings().mineGoalUpdateInterval.value != 0
        && tickCount++ % Baritone.settings().mineGoalUpdateInterval.value == 0) {
      Baritone.getExecutor()
          .execute(() -> {
            locations = WorldScanner.INSTANCE.scanChunkRadius(ctx, scan, 256, 10, 10);
            currentPriorities.clear();
          });
    }

    if (locations == null) {
      // we are spinning our wheels.
      return StrategyResult.of(new PathingCommand(null, PathingCommandType.REQUEST_PAUSE));
    }

    if (currentPriorities.size() == 0) {

      locations = locations.stream().filter(pos -> {
        IBlockState state = ctx.world().getBlockState(pos);
        int id = Block.getIdFromBlock(state.getBlock());
        BiFunction<BlockPos, IPlayerContext, Boolean> isFilter = posFilterer.get(id);
        if (isFilter == null) {
          System.err.println("unable to process block(no filter strategy applied): " + id);
          return false;
        }
        return isFilter.apply(pos, ctx);
      }).collect(Collectors.toList());

      if (locations.size() == 0) {
        return StrategyResult.SUCCESS; // mission success everything f a r m e d
      }
    }

    currentPriorities = locations.stream().map(pos -> {
      IBlockState state = ctx.world().getBlockState(pos);
      int id = Block.getIdFromBlock(state.getBlock());
      BiFunction<BlockPos, IPlayerContext, IBaritoneProcStrategy> op = onBlockStrategy.get(id);
      if (op == null) {
        System.err.println(
            "unable to process block(no action strategy applied): " + id);
        return new IdStrategy();
      }
      return op.apply(pos, ctx);
    }).collect(Collectors.toList());

    scanner = new PrioritySequentialStrategy(baritone,
        currentPriorities.toArray(new IBaritoneProcStrategy[0]));

    if (calcFailed) {
      return StrategyResult.FAILURE;
    }

    List<Goal> tempGoals = new LinkedList<>();

    locations.forEach(pos -> {
      IBlockState state = ctx.world().getBlockState(pos);
      int id = Block.getIdFromBlock(state.getBlock());
      BiFunction<BlockPos, IPlayerContext, Consumer<List<Goal>>> goalOp =
          goalMaker.get(id);
      if (goalOp == null) {
        System.err.println(
            "Unable to determine how to path to a block of id: " + id + " known as: " + state
                .getBlock().getLocalizedName());
        return;
      }
      goalOp.apply(pos, ctx).accept(tempGoals);
    });

    StrategyResult nextMove = scanner.execute(false, isSafeToCancel);

    if (nextMove.isPresent()) {
      return nextMove;
    }

    return StrategyResult.of(new PathingCommand(new GoalComposite(tempGoals.toArray(new Goal[0])),
        PathingCommandType.SET_GOAL_AND_PATH));


  }


}
