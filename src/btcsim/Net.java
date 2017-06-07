/*
 * The MIT License
 *
 * Copyright 2017 7u83 <7u83@mail.ru>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persoconnectns to whom the Software is
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
import java.util.Random;

import btcsim.Block.BlockType;

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class Net {
    
    // Number of outgoing connections a node sould establish
    private final int NUM_CONNS = 8;

    Random random = new Random();
    ArrayList<Node> nodes;

    Net() {
        nodes = new ArrayList();
    }

    private int nexthash = 7;

    Block mineBlock(BlockType type, Block pr) {
        Block b = new Block(type, pr.height + 1, nexthash, pr.hash);
        nexthash++;
        return b;
    }

    void addNode(Node node) {
        int id = nodes.size();
        node.setNetAndId(this, id);
        nodes.add(id, node);
    }

    // class to provide the list of mining nodes
    private class Miner {

        Node node;
        int hashpower;

        Miner(Node node, int hashpower) {
            this.node = node;
            this.hashpower = hashpower;
        }
    }

    ArrayList<Miner> miners = new ArrayList<>();
    int hashpowersum = 0;

    void addMiningNode(Node node, int hashpower) {
        addNode(node);
        Miner miner = new Miner(node, hashpowersum + hashpower);
        hashpowersum += hashpower;
        miners.add(miner);
    }
    
    void distributeBlocks(){
        boolean dist;
        do {
            dist = false;
            System.out.printf("Distribute round over %d \n",nodes.size());
            for (Node n: nodes){
                if (n.distributeBlock()){
                    dist=true;
                }
            }
        }while (dist);
        
    }

    /**
     * Mine a block and distribute it
     */
    public void runStep() {
        // dice the next node to mine a block
        int r = random.nextInt(hashpowersum);

        // find the lucky node
        Miner miner = null;
        for (Miner m : miners) {
            if (r < m.hashpower) {
                miner = m;
                break;
            }
        }
        
        // Let em create a block
        Block b = miner.node.mineBlock();
        
        // Send em the block
        miner.node.receiveBlock(b);
        
        distributeBlocks();
        
        System.out.printf("%s has mined a %s block\n", miner.node.name, b.type.toString());

    }

    void connectNodes() {
        for (Node node : nodes) {
            node.connect(NUM_CONNS);
        }
    }
    
    
    public void showChains(){
        for (Miner m: miners){
            BlockChain ch = m.node.chain;
            System.out.printf("%s: block %d, hash %d, rejected %d, reorgs %d\n",
                    m.node.name,
                    ch.front_block.height,
                    ch.front_block.hash,
                    m.node.rejected,
                    ch.reorgs);
            
        }
        
    }


    public void showNodes(){
        for (Node n: nodes){
            BlockChain ch = n.chain;
            String name;
            if (n.name==null)
                name="Anon";
            else
                name=n.name;
            System.out.printf("%s: block %d, hash %d, rejected %d, reorgs %d\n",
                    name,
                    ch.front_block.height,
                    ch.front_block.hash,
                    n.rejected,
                    ch.reorgs);
            
        }
        
    }
    
    
}
