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

package baritone.strategies.utils;

import baritone.api.utils.IPlayerContext;
import baritone.api.utils.RayTraceUtils;
import baritone.api.utils.Rotation;
import baritone.api.utils.RotationUtils;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class RotSearch {
  public static Vec3d ABOVE = new Vec3d(0.5,1,0.5);
  public static Vec3d UNDER = new Vec3d(0.5,0,0.5);
  public static Vec3d POSX = new Vec3d(1,0.5,0.5);
  public static Vec3d NEGX = new Vec3d(0,1,0.5);
  public static Vec3d POSZ = new Vec3d(0.5,0.5,1);
  public static Vec3d NEGZ = new Vec3d(0.5,0.5,0);

  public static Optional<Rotation> findVantage(IPlayerContext ctx, BlockPos target, SidePreference pref){
    if(pref == SidePreference.ANY){
      for(SidePreference s : SidePreference.values()) {
        if(s.equals(SidePreference.ANY)){
          continue;
        }
        Optional<Rotation> a1 = findVantage(ctx, target, s);
        if (a1.isPresent()) {
          return a1;
        }
      }
      return Optional.empty();
    }
    else{
      switch(pref){
        case TOP:
          return findVantage(ctx,target,
              (alpha,beta) -> new Vec3d(alpha,1,beta).add(bpToVec3d(target)), EnumFacing.UP);
        case BOTTOM:
          return findVantage(ctx,target,
              (alpha,beta) -> new Vec3d(alpha,0,beta).add(bpToVec3d(target)), EnumFacing.DOWN);
        case POSX_EAST:
          return findVantage(ctx,target,
              (alpha,beta) -> new Vec3d(1,alpha,beta).add(bpToVec3d(target)), EnumFacing.EAST);
        case NEGX_WEST:
          return findVantage(ctx,target,
              (alpha,beta) -> new Vec3d(0,alpha,beta).add(bpToVec3d(target)), EnumFacing.WEST);
        case POSZ_SOUTH:
          return findVantage(ctx,target,
              (alpha,beta) -> new Vec3d(alpha,beta,1).add(bpToVec3d(target)), EnumFacing.SOUTH);
        case NEGZ_NORTH:
          return findVantage(ctx,target,
              (alpha,beta) -> new Vec3d(alpha,beta,0).add(bpToVec3d(target)), EnumFacing.NORTH);
        default:
          throw new IllegalArgumentException("bad param");
      }
    }

  }

  private static Vec3d bpToVec3d(BlockPos target) {
    return new Vec3d(target.getX(), target.getY(), target.getZ());
  }

  private static Optional<Rotation> findVantage(IPlayerContext ctx, BlockPos target, BiFunction<Double,Double,Vec3d> biparam, EnumFacing check) {

    for (double o_alpha = 0.2; o_alpha < 0.8; o_alpha += 0.05) {
      for (double o_beta = 0.2; o_beta < 0.8; o_beta += 0.05) {
        Optional<Rotation> rot = RotationUtils
            .reachableOffset(ctx.player(), target, biparam.apply(o_alpha, o_beta),
                ctx.playerController().getBlockReachDistance(), false);

        if (rot.isPresent()) {
          RayTraceResult result = RayTraceUtils
              .rayTraceTowards(ctx.player(), rot.get(),
                  ctx.playerController().getBlockReachDistance());

          if (result.typeOfHit == RayTraceResult.Type.BLOCK && result.sideHit == check) {
            return rot;
          }
        }
      }
    }
    return Optional.empty();
  }

}
