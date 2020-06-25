package com.klitjens.test.blocks;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class IndicatorBlock extends Block {

    //public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;
    //public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private boolean canProvidePower = true;
    private final Set<BlockPos> blocksNeedingUpdate = Sets.newHashSet();

    public IndicatorBlock() {
        super(Block.Properties.create(Material.IRON));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState blockstate = super.getStateForPlacement(context);
        if (blockstate != null) {
            blockstate = blockstate.with(FACING, context.getNearestLookingDirection()).with(POWER, Integer.valueOf(context.getWorld().getRedstonePowerFromNeighbors(context.getPos())));
        }
        return blockstate;
    }

    private BlockState updateSurroundingRedstone(World worldIn, BlockPos pos, BlockState state) {
        state = this.getRedstoneInput(worldIn, pos, state);
        List<BlockPos> list = Lists.newArrayList(this.blocksNeedingUpdate);
        this.blocksNeedingUpdate.clear();

        for(BlockPos blockpos : list) {
            worldIn.notifyNeighborsOfStateChange(blockpos, this);
        }

        return state;
    }

    private BlockState getRedstoneInput(World world, BlockPos pos, BlockState state){
        BlockState blockState = state;
        int i = state.get(POWER);
        int j = world.getRedstonePowerFromNeighbors(pos);
        int k = 0;
        if (j < 15) {
            for(Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos blockpos = pos.offset(direction);
                BlockState blockstate1 = world.getBlockState(blockpos);
                k = this.maxSignal(k, blockstate1);
                BlockPos blockpos1 = pos.up();
                if (blockstate1.isNormalCube(world, blockpos) && !world.getBlockState(blockpos1).isNormalCube(world, blockpos1)) {
                    k = this.maxSignal(k, world.getBlockState(blockpos.up()));
                } else if (!blockstate1.isNormalCube(world, blockpos)) {
                    k = this.maxSignal(k, world.getBlockState(blockpos.down()));
                }
            }
        }
        int l = k - 1;
        if (j > l) {
            l = j;
        }

        if (i != l) {
            state = state.with(POWER, Integer.valueOf(l));
            if (world.getBlockState(pos) == blockState) {
                world.setBlockState(pos, state, 2);
            }

            this.blocksNeedingUpdate.add(pos);

            for(Direction direction1 : Direction.values()) {
                this.blocksNeedingUpdate.add(pos.offset(direction1));
            }
        }
        return state;
    };

    private int maxSignal(int existingSignal, BlockState neighbor) {
        if (neighbor.getBlock() != this) {
            return existingSignal;
        } else {
            int i = neighbor.get(POWER);
            return i > existingSignal ? i : existingSignal;
        }
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!worldIn.isRemote) {
            if (state.isValidPosition(worldIn, pos)) {
                this.updateSurroundingRedstone(worldIn, pos, state);
            } else {
                spawnDrops(state, worldIn, pos);
                worldIn.removeBlock(pos, false);
            }

        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState>builder) {
        builder.add(FACING, POWER);
    }


}
