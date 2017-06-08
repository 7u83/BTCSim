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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

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

        readNodes("default.net", net);


        net.connectNodes();

        for (int i = 0; i < 1000; i++) {
            // Let nodes connect randomly
            net.connectNodes();
            // mine a  block and distribute its
            net.runStep();
        }

        System.out.printf("========================================\n");
        net.showChains();
      //  System.out.printf("----------------------------------------\n");
      //  net.showNodes();

    }

    /**
     * Read nodes from configuration file
     *
     * @param filename filename
     * @param net Net, where to add the nodes
     */
    static void readNodes(String filename, Net net) {
        try {
            FileInputStream stream = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));

            String line;
            int linenr = 0;
            while ((line = br.readLine()) != null) {
                linenr++;
                String[] sl = line.split(",");
                // ignore empty lines
                if (sl[0].trim().length() == 0) {
                    continue;
                }

                // ignore comments (lines starting with #)
                if (sl[0].charAt(0) == '#') {
                    System.out.printf("Comment\n");
                    continue;
                }

                // ignore lines with less than two entries
                if (sl.length < 2) {
                    continue;
                }

                // Create a node based on the first field
                Node node;
                node = getNode(sl[0].trim());
                if (node == null) {
                    System.err.printf("Error in %s at line %d: unknown node type '%s'\n", filename, linenr, sl[0].trim());
                    continue;
                }

                int num_nodes;
                try {
                    num_nodes = Integer.parseInt(sl[1].trim());
                } catch (Exception e) {
                    System.err.printf("Error in %s at line %d: integer expected, but got '%s'\n", filename, linenr, sl[1].trim());
                    continue;
                }

                // read out the name
                String name = null;
                if (sl.length > 2) {
                    name = sl[2].trim();
                }

                int hashpower = 0;
                if (sl.length > 3) {
                    try {
                        hashpower = Integer.parseInt(sl[3].trim());
                    } catch (Exception e) {
                        System.err.printf("Error in %s at line %d: integer expected, but got '%s'\n", filename, linenr, sl[1].trim());
                        continue;
                    }

                }

                System.out.printf("Adding %d %s-type nodes with hp %d\n",
                        num_nodes,
                        sl[0].trim(),
                        hashpower);

                // Add the nodes
                for (int i = 0; i < num_nodes; i++) {

                    node.setName(name);
                    if (hashpower > 0) {
                        net.addMiningNode(node, hashpower);
                    } else {
                        net.addNode(node);
                    }
                    node = getNode(sl[0].trim());
                }

            }

            br.close();

        } catch (Exception e) {
//            System.out.printf("FILE: %s\n", filename.getAbsoluteFile());
//            System.err.printf("%s: %s\n", fileNameDefined, e.getMessage());
            e.printStackTrace();
        }

    }

    static Node getNode(String name) {
        if (name.equals("Node")) {
            return new Node("hat");
        }
        if (name.equals("SegWitNode")) {
            return new SegWitNode();
        }
        if (name.equals("UASFNode")) {
            return new UASFNode();
        }

        return null;
    }
    
    
    

}
