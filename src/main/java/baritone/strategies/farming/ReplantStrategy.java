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

package baritone.strategies.farming;

import static baritone.strategies.farming.FarmerStrategy.FARMLAND_PLANTABLE;

import baritone.Baritone;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.utils.RayTraceUtils;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import baritone.api.utils.StrategyResult;
import baritone.api.utils.input.Input;
import baritone.strategies.abstractions.BaseBaritoneStrategy;
import java.util.Optional;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class ReplantStrategy extends BaseBaritoneStrategy {
  private final BlockPos pos;
  private boolean hitRC = false;

  protected ReplantStrategy(Baritone b, BlockPos p) {
    super(b);
    this.pos = p;
  }

  @Override
  public void reset() {
    this.hitRC = false;
  }

  @Override
  public StrategyResult execute(boolean calcFailed, boolean isSafeToCancel) {
    IBlockState state = ctx.world().getBlockState(pos);
    boolean soulsand  = state.getBlock().equals(Blocks.SOUL_SAND);

    if(hitRC){
      return StrategyResult.SUCCESS;
    }
    Optional<Rotation> rot = RotationUtils
        .reachableOffset(ctx.player(), pos, new Vec3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5), ctx.playerController().getBlockReachDistance(), false);
    if (rot.isPresent() && isSafeToCancel && baritone.getInventoryBehavior().throwaway(true, soulsand ? this::isNetherWart : this::isPlantable)) {
      RayTraceResult result = RayTraceUtils
          .rayTraceTowards(ctx.player(), rot.get(), ctx.playerController().getBlockReachDistance());
      if (result.typeOfHit == RayTraceResult.Type.BLOCK && result.sideHit == EnumFacing.UP) {
        baritone.getLookBehavior().updateTarget(rot.get(), true);
        if (ctx.isLookingAt(pos)) {
          baritone.getInputOverrideHandler().setInputForceState(Input.CLICK_RIGHT, true);
          hitRC = true;
        }
        return StrategyResult.of(new PathingCommand(null, PathingCommandType.REQUEST_PAUSE));
      }
    }
    return StrategyResult.FAILURE;
  }

  private boolean isPlantable(ItemStack stack) {
    return FARMLAND_PLANTABLE.contains(stack.getItem());
  }

  private boolean isNetherWart(ItemStack stack) {
    return !stack.isEmpty() && stack.getItem().equals(Items.NETHER_WART);
  }
}
