/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
