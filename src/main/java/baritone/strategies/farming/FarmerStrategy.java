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

import baritone.Baritone;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.process.IBaritoneProcStrategy;
import baritone.api.utils.IPlayerContext;
import baritone.api.utils.StrategyResult;
import baritone.process.BuilderProcess;
import baritone.strategies.abstractions.BaseBaritoneStrategy;
import baritone.strategies.abstractions.IfElseStrategy;
import baritone.strategies.abstractions.IfStrategy;
import baritone.strategies.abstractions.PrioritySequentialStrategy;
import baritone.strategies.abstractions.ScanAndAct;
import baritone.strategies.abstractions.TryBreak;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockNetherWart;
import net.minecraft.block.BlockReed;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Farms.
 */
public class FarmerStrategy extends BaseBaritoneStrategy {

  public static final List<Item> FARMLAND_PLANTABLE = Arrays.asList(
      Items.BEETROOT_SEEDS,
      Items.MELON_SEEDS,
      Items.WHEAT_SEEDS,
      Items.PUMPKIN_SEEDS,
      Items.POTATO,
      Items.CARROT
  );
  public static final List<Item> PICKUP_DROPPED = Arrays.asList(
      Items.BEETROOT_SEEDS,
      Items.BEETROOT,
      Items.MELON_SEEDS,
      Items.MELON,
      Item.getItemFromBlock(Blocks.MELON_BLOCK),
      Items.WHEAT_SEEDS,
      Items.WHEAT,
      Items.PUMPKIN_SEEDS,
      Item.getItemFromBlock(Blocks.PUMPKIN),
      Items.POTATO,
      Items.CARROT,
      Items.NETHER_WART,
      Items.REEDS,
      Item.getItemFromBlock(Blocks.CACTUS)
  );
  private final int range;
  private final BlockPos center;
  private final IBaritoneProcStrategy compositeFarmer;

  public FarmerStrategy(Baritone baritone, int range, BlockPos preferedCenter) {
    super(baritone);
    if (preferedCenter == null) {
      center = baritone.getPlayerContext().playerFeet();
    } else {
      center = preferedCenter;
    }
    this.range = range;

    HashMap<Integer, BiFunction<BlockPos, IPlayerContext, Boolean>> isHarvestable = new HashMap<>();
    HashMap<Integer, BiFunction<BlockPos, IPlayerContext, Boolean>> isBoneMealable = new HashMap<>();
    HashMap<Integer, BiFunction<BlockPos, IPlayerContext, Boolean>> posFilterer = new HashMap<>();
    HashMap<Integer, BiFunction<BlockPos, IPlayerContext, IBaritoneProcStrategy>> onBlockStrategy = new HashMap<>();
    HashMap<Integer, BiFunction<BlockPos, IPlayerContext, Consumer<List<Goal>>>> goalMaker = new HashMap<>();

    BiFunction<BlockPos, IPlayerContext, Boolean> isAirAbove =
        (pos, ctx) ->
            !(this.range != 0
                && pos.getDistance(this.center.getX(), this.center.getY(), this.center.getZ())
                > range) && ctx.world().getBlockState(pos.up()).getBlock() instanceof BlockAir;

    Function<BlockPos, Predicate<IPlayerContext>> isBlockNotAir = pos -> ctx -> !(ctx.world()
        .getBlockState(pos).getBlock().equals(Blocks.AIR));



    // 1. farmland and soulsand need air above
    posFilterer.put(Block.getIdFromBlock(Blocks.FARMLAND), isAirAbove);
    posFilterer.put(Block.getIdFromBlock(Blocks.SOUL_SAND), isAirAbove);

    // 2. for each harvestable block, we filter if they are not ready for harvest(and if air is above).
    for (FarmerStrategy.Harvest harvest : FarmerStrategy.Harvest.values()) {
      isHarvestable.put(Block.getIdFromBlock(harvest.block),
          (pos, ctx) -> harvest
              .readyToHarvest(ctx.world(), pos, ctx.world().getBlockState(pos)));
    }

    // 3. for each growable block, we might accept it if it is bonemealable*

    BiFunction<BlockPos, IPlayerContext, Boolean> canBoneMeal = (pos, ctx) -> {
      IBlockState state = ctx.world().getBlockState(pos);
      if (state.getBlock() instanceof IGrowable) {
        IGrowable ig = (IGrowable) state.getBlock();
        return ig.canGrow(ctx.world(), pos, state, true) && ig
            .canUseBonemeal(ctx.world(), ctx.world().rand, pos, state);
      }
      return false;
    };

    for (FarmerStrategy.Harvest harvest : FarmerStrategy.Harvest.values()) {
      int blockid = Block.getIdFromBlock(harvest.block);
      isBoneMealable.put(blockid,
          canBoneMeal);
    }

    // merge the two predicates together
    for (FarmerStrategy.Harvest harvest : FarmerStrategy.Harvest.values()) {
      int blockid = Block.getIdFromBlock(harvest.block);
      posFilterer.put(blockid,
          (p, c) -> isAirAbove.apply(p, c) && (isBoneMealable.get(blockid).apply(p, c) || isHarvestable.get(blockid)
              .apply(p, c)));
    }

    // set up goals on each location hit.
    // if we have plantable aux then add a planting goal.
    goalMaker.put(Block.getIdFromBlock(Blocks.FARMLAND), (pos, ctx) -> (goalz) -> {
      if (baritone.getInventoryBehavior().throwaway(false, this::isPlantable)) {
        goalz.add(new GoalBlock(pos.up()));
      }
    });

    goalMaker.put(Block.getIdFromBlock(Blocks.SOUL_SAND), (pos, ctx) -> (goalz) -> {
      if (baritone.getInventoryBehavior().throwaway(false, this::isNetherWart)) {
        goalz.add(new GoalBlock(pos.up()));
      }
    });

    // 4 goals for harvesting:
    for (FarmerStrategy.Harvest harvest : FarmerStrategy.Harvest.values()) {
      final int blockid = Block.getIdFromBlock(harvest.block);
      goalMaker.put(blockid, (pos, ctx) -> (goalz) -> {
        if (isBoneMealable.get(blockid).apply(pos, ctx)  && baritone.getInventoryBehavior().throwaway(false, this::isBoneMeal)) {
          goalz.add(new GoalBlock(pos));
        }
        if (isHarvestable.get(blockid).apply(pos, ctx)) {
          goalz.add(new BuilderProcess.GoalBreak(pos));
        }
      });
    }

    // 5 Strategy on point reached.

    // farmland soulsand placing
    BiFunction<BlockPos, IPlayerContext, IBaritoneProcStrategy> replantStrategy
        = (pos, ctx) -> new ReplantStrategy(baritone, pos);

    BiFunction<BlockPos, IPlayerContext, IBaritoneProcStrategy> boneMealStrategy
        = (pos, ctx) -> new BoneMealStrategy(baritone, pos);

    BiFunction<BlockPos, IPlayerContext, IBaritoneProcStrategy> harvestStrategy
        = (pos, ctx) -> new TryBreak(baritone, pos);

    onBlockStrategy.put(Block.getIdFromBlock(Blocks.FARMLAND), replantStrategy);
    onBlockStrategy.put(Block.getIdFromBlock(Blocks.SOUL_SAND), replantStrategy);

    // at every tick, we evaluate whether to try breaking or spamming bonemeal based on if its harvestable or not.
    for (FarmerStrategy.Harvest harvest : FarmerStrategy.Harvest.values()) {
      final int blockid = Block.getIdFromBlock(harvest.block);
      onBlockStrategy.put(blockid,
          (pos, ignored) -> new IfStrategy(baritone, isBlockNotAir.apply(pos),
              new IfElseStrategy(baritone,
                  ctx -> isHarvestable.get(blockid).apply(pos, ctx),
                  harvestStrategy.apply(pos, ignored),
                  boneMealStrategy.apply(pos, ignored))));
    }

    //6 time for the magic

    ArrayList<Block> scan = new ArrayList<>();
    for (FarmerStrategy.Harvest harvest : FarmerStrategy.Harvest.values()) {
      scan.add(harvest.block);
    }
    if (Baritone.settings().replantCrops.value) {
      scan.add(Blocks.FARMLAND);
      if (Baritone.settings().replantNetherWart.value) {
        scan.add(Blocks.SOUL_SAND);
      }
    }

    IBaritoneProcStrategy pickUpItems = new PickUpItems(baritone,
        PICKUP_DROPPED.toArray(new Item[0]));

    IBaritoneProcStrategy farmer = new ScanAndAct(baritone, scan, posFilterer, goalMaker,
        onBlockStrategy);

    compositeFarmer = new PrioritySequentialStrategy(baritone, farmer, pickUpItems);
  }


  @Override
  public void reset() {
    compositeFarmer.reset();
  }

  @Override
  public StrategyResult execute(boolean calcFailed, boolean isSafeToCancel) {

    return compositeFarmer.execute(calcFailed, isSafeToCancel);
  }

  private boolean isPlantable(ItemStack stack) {
    return FARMLAND_PLANTABLE.contains(stack.getItem());
  }

  private boolean isBoneMeal(ItemStack stack) {
    return !stack.isEmpty() && stack.getItem() instanceof ItemDye
        && EnumDyeColor.byDyeDamage(stack.getMetadata()) == EnumDyeColor.WHITE;
  }


  private boolean isNetherWart(ItemStack stack) {
    return !stack.isEmpty() && stack.getItem().equals(Items.NETHER_WART);
  }

  private enum Harvest {
    WHEAT((BlockCrops) Blocks.WHEAT),
    CARROTS((BlockCrops) Blocks.CARROTS),
    POTATOES((BlockCrops) Blocks.POTATOES),
    BEETROOT((BlockCrops) Blocks.BEETROOTS),
    PUMPKIN(Blocks.PUMPKIN, state -> true),
    MELON(Blocks.MELON_BLOCK, state -> true),
    NETHERWART(Blocks.NETHER_WART, state -> state.getValue(BlockNetherWart.AGE) >= 3),
    SUGARCANE(Blocks.REEDS, null) {
      @Override
      public boolean readyToHarvest(World world, BlockPos pos, IBlockState state) {
        if (Baritone.settings().replantCrops.value) {
          return world.getBlockState(pos.down()).getBlock() instanceof BlockReed;
        }
        return true;
      }
    },
    CACTUS(Blocks.CACTUS, null) {
      @Override
      public boolean readyToHarvest(World world, BlockPos pos, IBlockState state) {
        if (Baritone.settings().replantCrops.value) {
          return world.getBlockState(pos.down()).getBlock() instanceof BlockCactus;
        }
        return true;
      }
    };
    public final Block block;
    public final Predicate<IBlockState> readyToHarvest;

    Harvest(BlockCrops blockCrops) {
      this(blockCrops, blockCrops::isMaxAge);
      // max age is 7 for wheat, carrots, and potatoes, but 3 for beetroot
    }

    Harvest(Block block, Predicate<IBlockState> readyToHarvest) {
      this.block = block;
      this.readyToHarvest = readyToHarvest;
    }

    public boolean readyToHarvest(World world, BlockPos pos, IBlockState state) {
      return readyToHarvest.test(state);
    }
  }

}
