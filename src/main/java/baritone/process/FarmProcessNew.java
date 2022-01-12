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

package baritone.process;

import baritone.Baritone;
import baritone.api.process.IBaritoneProcStrategy;
import baritone.api.process.IFarmProcess;
import baritone.api.process.PathingCommand;
import baritone.api.process.PathingCommandType;
import baritone.api.utils.StrategyResult;
import baritone.strategies.farming.FarmerStrategy;
import baritone.strategies.utils.FillChest;
import baritone.utils.BaritoneProcessHelper;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;

public final class FarmProcessNew extends BaritoneProcessHelper implements IFarmProcess {

    private boolean active;
    private IBaritoneProcStrategy farmingStrategy;

    public FarmProcessNew(Baritone baritone) {
        super(baritone);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void farm(int range, BlockPos pos) {
        farmingStrategy = new FarmerStrategy(baritone, range, pos);


        active = true;
    }



    @Override
    public PathingCommand onTick(boolean calcFailed, boolean isSafeToCancel) {
        baritone.getInputOverrideHandler().clearAllKeys();
        StrategyResult maybeCommand = farmingStrategy.execute(calcFailed, isSafeToCancel);
        if(maybeCommand.isPresent()){
            // strategy has something to do we are good
            return maybeCommand.get();
        }

        onLostControl();
        logDirect("done");
        return new PathingCommand(null, PathingCommandType.REQUEST_PAUSE);
    }

    @Override
    public void onLostControl() {
        active = false;
    }

    @Override
    public String displayName0() {
        return "Farming";
    }
}
