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

import java.util.ArrayList;

import btcsim.Block.BlockType;

/**
 * Node class
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class Node {

    BlockChain chain;
    Block distrib;
    protected int id;
    protected Net net;
    String name = null;

    int rejected = 0;

    public Node() {
        chain = new BlockChain();
        distrib = null;
    }

    public Node(String name) {
        this();
        setName(name);
    }

    public final void setNetAndId(Net net, int id) {
        this.net = net;
        this.id = id;
    }

    /**
     * Set the name of the node
     *
     * @param name name to set
     */
    public final void setName(String name) {
        this.name = name;
    }

 
    /**
     * Verfiy a block Override this method to implement own verfifying such as
     * UASF
     *
     * @param block
     * @return true: accept the block, false: reject block
     */
    boolean checkBlock(Block block) {
        return true;
    }

    /**
     * Receive a (mined) block, frowarded from another node
     *
     * @param block the block sent to this node
     */
    public void receiveBlock(Block block) {

        if (!checkBlock(block)) {
            rejected++;
            return;
        }

        if (chain.getBlock(block.hash) != null) {
            return;
        }

        if (chain.addBlock(block)) {
            distrib = block;
        }

    }
    
    /**
     * Check if a block is in nodes data base
     * @param hash Hash of block
     * @return true if block is in database, false if not.
     */
    public boolean hasBlock(int hash){
        Block b = chain.getBlock(hash);
        return b!=null;
    }

    /**
     * Distribute a block to other nodes, to which this node is connected to.
     *
     * The block to distribute is taken from {@link this.distrib} If
     * this.distrib
     *
     * @return
     */
    boolean distributeBlock() {
        if (distrib == null) {
            return false;
        }
        for (Node node : connections) {
            node.receiveBlock(distrib);
        }
        distrib = null;
        return true;
    }

    Block mineBlock() {
        Block b = net.mineBlock(BlockType.Std, chain.front_block);
//        receiveBlock(b);
        return b;
    }

    // We store "connections" to other nodes here
    private ArrayList<Node> connections;

    /**
     * Connect this node to other nodes
     *
     * @param n Number of connectoins to create
     */
    public void connect(int n) {
        connections = new ArrayList();
        for (int i = 0; i < n; i++) {
            int cid = net.random.nextInt(net.nodes.size());
            if (cid == id) {
                continue;
            }
            connections.add(net.nodes.get(cid));
        }
    }
}
