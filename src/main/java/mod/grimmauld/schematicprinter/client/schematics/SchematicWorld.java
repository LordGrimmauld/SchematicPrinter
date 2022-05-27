package mod.grimmauld.schematicprinter.client.schematics;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.schematicprinter.SchematicPrinter;
import mod.grimmauld.schematicprinter.util.WrappedWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Predicate;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public class SchematicWorld extends WrappedWorld implements IServerWorld {

	public final BlockPos anchor;
	private final Map<BlockPos, BlockState> blocks;
	private final Map<BlockPos, TileEntity> tileEntities;
	private final List<TileEntity> renderedTileEntities;
	private final List<Entity> entities;
	private final MutableBoundingBox bounds;
	public boolean renderMode;

	public SchematicWorld(World original) {
		this(BlockPos.ZERO, original);
	}

	public SchematicWorld(BlockPos anchor, World original) {
		super(original);
		this.blocks = new HashMap<>();
		this.tileEntities = new HashMap<>();
		this.bounds = new MutableBoundingBox();
		this.anchor = anchor;
		this.entities = new ArrayList<>();
		this.renderedTileEntities = new ArrayList<>();
	}

	public Set<BlockPos> getAllPositions() {
		return blocks.keySet();
	}

	@Override
	public boolean addFreshEntity(Entity entityIn) {
		if (entityIn instanceof ItemFrameEntity)
			((ItemFrameEntity) entityIn).getItem()
				.setTag(null);
		if (entityIn instanceof ArmorStandEntity) {
			ArmorStandEntity armorStandEntity = (ArmorStandEntity) entityIn;
			armorStandEntity.getAllSlots()
				.forEach(stack -> stack.setTag(null));
		}

		return entities.add(entityIn);
	}

	public List<Entity> getEntities() {
		return entities;
	}

	@Override
	public TileEntity getBlockEntity(BlockPos pos) {
		if (isOutsideBuildHeight(pos))
			return null;
		if (tileEntities.containsKey(pos))
			return tileEntities.get(pos);
		if (!blocks.containsKey(pos.subtract(anchor)))
			return null;

		BlockState blockState = getBlockState(pos);
		if (blockState.hasTileEntity()) {
			try {
				TileEntity tileEntity = blockState.createTileEntity(this);
				if (tileEntity != null) {
					tileEntity.setLevelAndPosition(this, pos);
					tileEntities.put(pos, tileEntity);
					renderedTileEntities.add(tileEntity);
				}
				return tileEntity;
			} catch (Exception e) {
				SchematicPrinter.LOGGER.debug("Could not create TE of block " + blockState + ": " + e);
			}
		}
		return null;
	}

	@Override
	public BlockState getBlockState(BlockPos globalPos) {
		BlockPos pos = globalPos.subtract(anchor);

		if (pos.getY() - bounds.y0 == -1 && !renderMode)
			return Blocks.GRASS_BLOCK.defaultBlockState();
		if (getBounds().isInside(pos) && blocks.containsKey(pos)) {
			BlockState blockState = blocks.get(pos);
			if (blockState.getOptionalValue(BlockStateProperties.LIT).isPresent())
				blockState = blockState.setValue(BlockStateProperties.LIT, false);
			return blockState;
		}
		return Blocks.AIR.defaultBlockState();
	}

	public Map<BlockPos, BlockState> getBlockMap() {
		return blocks;
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return getBlockState(pos).getFluidState();
	}

	@Override
	public int getBrightness(LightType lightTypeIn, BlockPos blockPosIn) {
		return 10;
	}

	@Override
	public List<Entity> getEntities(@Nullable Entity arg0, AxisAlignedBB arg1, @Nullable Predicate<? super Entity> arg2) {
		return Collections.emptyList();
	}

	@Override
	public <T extends Entity> List<T> getEntitiesOfClass(Class<? extends T> arg0, AxisAlignedBB arg1,
															@Nullable Predicate<? super T> arg2) {
		return Collections.emptyList();
	}

	@Override
	public List<? extends PlayerEntity> players() {
		return Collections.emptyList();
	}

	@Override
	public int getSkyDarken() {
		return 0;
	}

	@Override
	public boolean isStateAtPosition(BlockPos pos, Predicate<BlockState> predicate) {
		return predicate.test(getBlockState(pos));
	}

	@Override
	public boolean destroyBlock(BlockPos arg0, boolean arg1) {
		return setBlock(arg0, Blocks.AIR.defaultBlockState(), 3);
	}

	@Override
	public boolean removeBlock(BlockPos arg0, boolean arg1) {
		return setBlock(arg0, Blocks.AIR.defaultBlockState(), 3);
	}

	@Override
	public boolean setBlock(BlockPos pos, BlockState arg1, int arg2) {
		pos = pos.subtract(anchor);
		bounds.expand(new MutableBoundingBox(pos, pos.offset(1, 1, 1)));
		blocks.put(pos, arg1);
		return true;
	}

	@Override
	public ITickList<Block> getBlockTicks() {
		return EmptyTickList.empty();
	}

	@Override
	public ITickList<Fluid> getLiquidTicks() {
		return EmptyTickList.empty();
	}

	public MutableBoundingBox getBounds() {
		return bounds;
	}

	public Iterable<TileEntity> getRenderedTileEntities() {
		return renderedTileEntities;
	}

	@Override
	public ServerWorld getWorld() {
		if (this.world instanceof ServerWorld) {
			return (ServerWorld) this.world;
		}
		throw new IllegalStateException("Cannot use IServerWorld#getWorld in a client environment");
	}

}
