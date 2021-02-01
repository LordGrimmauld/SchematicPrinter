package mod.grimmauld.schematicprinter.util;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.tags.ITagCollectionSupplier;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.ITickList;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.storage.ISpawnWorldInfo;
import net.minecraft.world.storage.MapData;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WrappedWorld extends World {
	protected final World world;

	public WrappedWorld(World world) {
		super((ISpawnWorldInfo) world.getWorldInfo(), world.getDimensionKey(), world.getDimensionType(),
			world::getProfiler, world.isRemote, world.isDebug(), 0);
		this.world = world;
	}

	public World getWorld() {
		return this.world;
	}

	public BlockState getBlockState(BlockPos pos) {
		return this.world.getBlockState(pos);
	}

	public boolean hasBlockState(BlockPos p_217375_1_, Predicate<BlockState> p_217375_2_) {
		return this.world.hasBlockState(p_217375_1_, p_217375_2_);
	}

	public TileEntity getTileEntity(BlockPos pos) {
		return this.world.getTileEntity(pos);
	}

	public boolean setBlockState(BlockPos pos, BlockState newState, int flags) {
		return this.world.setBlockState(pos, newState, flags);
	}

	public int getLight(BlockPos pos) {
		return 15;
	}

	public void notifyBlockUpdate(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
		this.world.notifyBlockUpdate(pos, oldState, newState, flags);
	}

	public ITickList<Block> getPendingBlockTicks() {
		return this.world.getPendingBlockTicks();
	}

	public ITickList<Fluid> getPendingFluidTicks() {
		return this.world.getPendingFluidTicks();
	}

	@Override
	public AbstractChunkProvider getChunkProvider() {
		return world.getChunkProvider();
	}

	public void playEvent(@Nullable PlayerEntity player, int type, BlockPos pos, int data) {
	}

	public List<? extends PlayerEntity> getPlayers() {
		return Collections.emptyList();
	}

	public void playSound(@Nullable PlayerEntity player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
	}

	public void playMovingSound(@Nullable PlayerEntity p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_, SoundCategory p_217384_4_, float p_217384_5_, float p_217384_6_) {
	}

	public Entity getEntityByID(int id) {
		return null;
	}

	public MapData getMapData(String mapName) {
		return null;
	}

	public boolean addEntity(Entity entityIn) {
		entityIn.setWorld(this.world);
		return this.world.addEntity(entityIn);
	}

	public void registerMapData(MapData mapDataIn) {
	}

	public int getNextMapId() {
		return 0;
	}

	public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
	}

	public Scoreboard getScoreboard() {
		return this.world.getScoreboard();
	}

	public RecipeManager getRecipeManager() {
		return this.world.getRecipeManager();
	}

	public ITagCollectionSupplier getTags() {
		return this.world.getTags();
	}

	public Biome getNoiseBiomeRaw(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
		return this.world.getNoiseBiomeRaw(p_225604_1_, p_225604_2_, p_225604_3_);
	}

	@Override
	public DynamicRegistries func_241828_r() {
		return world.func_241828_r();
	}

	@Override
	public float func_230487_a_(Direction p_230487_1_, boolean p_230487_2_) {
		return world.func_230487_a_(p_230487_1_, p_230487_2_);
	}
}
