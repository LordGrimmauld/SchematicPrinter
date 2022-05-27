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
		super((ISpawnWorldInfo) world.getLevelData(), world.dimension(), world.dimensionType(),
			world::getProfiler, world.isClientSide, world.isDebug(), 0);
		this.world = world;
	}

	public World getWorld() {
		return this.world;
	}

	public BlockState getBlockState(BlockPos pos) {
		return this.world.getBlockState(pos);
	}

	public boolean isStateAtPosition(BlockPos p_217375_1_, Predicate<BlockState> p_217375_2_) {
		return this.world.isStateAtPosition(p_217375_1_, p_217375_2_);
	}

	public TileEntity getBlockEntity(BlockPos pos) {
		return this.world.getBlockEntity(pos);
	}

	public boolean setBlock(BlockPos pos, BlockState newState, int flags) {
		return this.world.setBlock(pos, newState, flags);
	}

	public int getMaxLocalRawBrightness(BlockPos pos) {
		return 15;
	}

	public void sendBlockUpdated(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
		this.world.sendBlockUpdated(pos, oldState, newState, flags);
	}

	public ITickList<Block> getBlockTicks() {
		return this.world.getBlockTicks();
	}

	public ITickList<Fluid> getLiquidTicks() {
		return this.world.getLiquidTicks();
	}

	@Override
	public AbstractChunkProvider getChunkSource() {
		return world.getChunkSource();
	}

	public void levelEvent(@Nullable PlayerEntity player, int type, BlockPos pos, int data) {
	}

	public List<? extends PlayerEntity> players() {
		return Collections.emptyList();
	}

	public void playSound(@Nullable PlayerEntity player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch) {
	}

	public void playSound(@Nullable PlayerEntity p_217384_1_, Entity p_217384_2_, SoundEvent p_217384_3_, SoundCategory p_217384_4_, float p_217384_5_, float p_217384_6_) {
	}

	public Entity getEntity(int id) {
		return null;
	}

	public MapData getMapData(String mapName) {
		return null;
	}

	public boolean addFreshEntity(Entity entityIn) {
		entityIn.setLevel(this.world);
		return this.world.addFreshEntity(entityIn);
	}

	public void setMapData(MapData mapDataIn) {
	}

	public int getFreeMapId() {
		return 0;
	}

	public void destroyBlockProgress(int breakerId, BlockPos pos, int progress) {
	}

	public Scoreboard getScoreboard() {
		return this.world.getScoreboard();
	}

	public RecipeManager getRecipeManager() {
		return this.world.getRecipeManager();
	}

	public ITagCollectionSupplier getTagManager() {
		return this.world.getTagManager();
	}

	public Biome getUncachedNoiseBiome(int p_225604_1_, int p_225604_2_, int p_225604_3_) {
		return this.world.getUncachedNoiseBiome(p_225604_1_, p_225604_2_, p_225604_3_);
	}

	@Override
	public DynamicRegistries registryAccess() {
		return world.registryAccess();
	}

	@Override
	public float getShade(Direction p_230487_1_, boolean p_230487_2_) {
		return world.getShade(p_230487_1_, p_230487_2_);
	}
}
