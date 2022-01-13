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

package baritone.api.utils;


import baritone.api.process.PathingCommand;

public class StrategyResult {
  public static final StrategyResult SUCCESS = new StrategyResult();
  public static final StrategyResult FAILURE = new StrategyResult();

  private final PathingCommand inside;

  public StrategyResult(){
    inside = null;
  }
  public StrategyResult(PathingCommand p){
    this.inside = p;
  }

  public static StrategyResult of(PathingCommand pathingCommand) {
    return new StrategyResult(pathingCommand);
  }

  @Override
  public String toString() {
    if(this.equals(SUCCESS)){
      return "SUCCESS";
    }
    else if(this.equals(FAILURE)){
      return "FAIL";
    }
    else{
      return inside.toString();
    }
  }

  public boolean isPresent(){
    return this.inside != null;
  }

  public PathingCommand get(){
    return this.inside;
  }

  public boolean isSuccess(){
    return this.equals(SUCCESS);
  }

  public boolean isFail(){
    return this.equals(FAILURE);
  }
}
