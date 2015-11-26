package zmaster587.advancedRocketry.api.util;

import java.util.HashSet;
import java.util.Stack;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.api.AtmosphereHandler;
import zmaster587.libVulpes.util.BlockPosition;

public class AtmosphereBlob extends AreaBlob {

	public AtmosphereBlob(IBlobHandler blobHandler) {
		super(blobHandler);
	}


	@Override
	public void removeBlock(int x, int y, int z) {
		BlockPosition blockPos = new BlockPosition(x, y, z);
		graph.remove(blockPos);
		graph.contains(blockPos);

		for(ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {

			BlockPosition newBlock = blockPos.getPositionAtOffset(direction.offsetX, direction.offsetY, direction.offsetZ);
			if(graph.contains(newBlock) && !graph.doesPathExist(newBlock, blobHandler.getRootPosition()))
				graph.removeAllNodesConnectedTo(newBlock);
		}
	}

	public boolean isBlockSealed(Block block) {
		return block.isOpaqueCube() || block == Blocks.glass;
	}
	
	@Override
	public void addBlock(BlockPosition blockPos) {
		//super.addBlock(blockPos);

		if(blobHandler.canFormBlob()) {

			int maxSize = this.getBlobMaxRadius();

			HashSet<BlockPosition> addableBlocks = new HashSet<BlockPosition>();
			if(!this.contains(blockPos)) {

				Stack<BlockPosition> stack = new Stack<BlockPosition>();
				stack.push(blockPos);


				while(!stack.isEmpty()) {
					BlockPosition stackElement = stack.pop();
					addableBlocks.add(stackElement);

					for(ForgeDirection dir2 : ForgeDirection.VALID_DIRECTIONS) {
						BlockPosition searchNextPosition = stackElement.getPositionAtOffset(dir2.offsetX, dir2.offsetY, dir2.offsetZ);

						if(!isBlockSealed(blobHandler.getWorldObj().getBlock(searchNextPosition.x, searchNextPosition.y, searchNextPosition.z)) && !graph.contains(searchNextPosition) && !addableBlocks.contains(searchNextPosition)) {
							if(searchNextPosition.getDistance(this.getRootPosition()) <= maxSize) {
								stack.push(searchNextPosition);
								addableBlocks.add(searchNextPosition);
							}
							else {
								//TODO: for limits
								clearBlob();
								return;
							}
						}
					}
				}
			}

			for(BlockPosition blockPos2 : addableBlocks) {
				super.addBlock(blockPos2);
			}

		}
	}
}
