/*
 * The MIT License
 *
 * Copyright 2017 7u83 <7u83@mail.ru>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package btcsim;

import java.util.HashMap;

import btcsim.Block.BlockType;

/**
 * Holds the block chain for each node
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class BlockChain {

    // Kind of gnenessis block 
    static final Block FIRST_BLOCK = new Block(BlockType.Std, 1, 0, 1000);

    // we keep our blocks here
    HashMap<Integer, Block> blocks;
    int reorgs = 0;

    // current block 
    Block front_block;

    BlockChain() {
        blocks = new HashMap<>();
        blocks.put(FIRST_BLOCK.hash, FIRST_BLOCK);
        front_block = FIRST_BLOCK;
    }

    /**
     * Get a block by its hash
     *
     * @param hash the hash
     * @return the block or null if not found
     */
    Block getBlock(int hash) {
        return blocks.get(hash);
    }

    /**
     * Add a new block to the block chain
     *
     * @param block block to add
     * @return true if adding was successful, otherwise false
     */
    boolean addBlock(Block block) {
        Block prevblock = getBlock(block.prevhash);
        if (prevblock == null) {
            return false;
        }
        blocks.put(block.hash, block);
        if (block.height > front_block.height) {
            if (block.prevhash != front_block.hash) {
                reorgs++;
            }

            front_block = block;
        }
        return true;
    }

}
