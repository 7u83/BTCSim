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

/**
 *
 * @author 7u83 <7u83@mail.ru>
 */
public class BTCSim {



    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        // Setup network
        Net net = new Net();
        
        
        // Add some non-mining fullnodes
        for (int i = 0; i < 1000; i++) {
            net.addNode(new Node());
        }
        
        // Add some UASF nodes
        for (int i = 0; i < 9000; i++) {
            net.addNode(new UASFNode());
        }
        
        
        // Add some mining nodes with apropriate hashing power
        net.addMiningNode(new Node("AntPool"), 159);
        net.addMiningNode(new Node("BTC.TOP"), 142);        
        
        
        net.addMiningNode(new SegWitNode("F2Pool"), 97);
        net.addMiningNode(new SegWitNode("BTCC"), 76);        
        net.addMiningNode(new SegWitNode("Bitfury"), 73);

        net.addMiningNode(new Node("Bixibn"), 73);        
        net.addMiningNode(new Node("BTC.com"), 64);
        net.addMiningNode(new Node("Slush"), 53);
        net.addMiningNode(new Node("ViaBTC"), 51);
        
        net.addMiningNode(new Node("BW.COM"), 45);
        net.addMiningNode(new SegWitNode("BitClub"), 38);
        
        net.addMiningNode(new Node("OtherNodes"), 250);
        

        net.addMiningNode(new UASFNode("UASFPool"), 7);

        
                      
        // Let nodes connect randomly
        net.connectNodes();
        
 
        for (int i=0; i<4320; i++){
            net.runStep();
        }

        System.out.printf("========================================\n");
        net.showChains();

    }

}
