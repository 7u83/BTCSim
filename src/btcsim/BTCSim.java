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

    static class Settings {

        String filename = null;
        int num_blocks = 1000;
        boolean verbose = false;
        boolean showall = false;
        boolean showblocks = false;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // Setup network
        Net net = new Net();

        Settings s = readSettings(args);

        if (s == null) {
            return;
        }

        if (!readNodes(s.filename, net,s.verbose)) {
            return;
        }

        net.connectNodes();

        for (int i = 0; i < s.num_blocks; i++) {

            if (!s.showblocks)
                System.out.printf("\rMining block %d from %d", i + 1, s.num_blocks);

            // Let nodes connect randomly
            net.connectNodes();
            // mine a  block and distribute its

            Block b = net.runStep();
            if (s.showblocks)
                System.out.printf("%s has mined a %s block\n", b.miner, b.type.toString());
            
        }
        System.out.printf("\n");

        System.out.printf("Result:\n");
        if (s.showall) {
            net.showNodes();
        } else {
            net.showChains();
        }

    }

    /**
     * Read nodes from configuration file
     *
     * @param filename filename
     * @param net Net, where to add the nodes
     */
    static boolean readNodes(String filename, Net net, boolean verbose) {
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

                if (verbose){
                    String s;
                    if (num_nodes==1)
                        s="";
                    else
                        s="s";
                    System.out.printf("Adding %d %s%s with hashing power %d\n",
                        num_nodes,
                        sl[0].trim(),
                        s,
                        hashpower);
                }
                
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
            System.err.printf("Error: %s\n", e.getMessage());
            return false;
        }
        return true;
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

    static void printUsage() {
        System.out.print(
                "Usage:\n"
                + " BTCSim [options] <filename>\n\n"
                + "filename = File defining the net.\n"
                + "Options:\n"
                + "  -n #: Number of blocks to mine, default is 1000\n"
                + "  -v: be verbose\n"
                + "  -a: show results for all nodes\n"
                + "  -b: show mined blocks\n"
        );
    }

    static Settings readSettings(String[] args) {
        if (args.length == 0) {
            printUsage();
            return null;
        }

        Settings s = new Settings();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-n")) {
                try {
                    s.num_blocks = Integer.parseInt(args[i + 1]);
                } catch (Exception e) {
                    System.err.printf("Error in option -n ##\n");
                    return null;

                }
                i = i + 1;
                continue;
            }
            if (arg.equals("-v")) {
                s.verbose = true;
                continue;
            }
            if (arg.equals("-b")) {
                s.showblocks = true;
                continue;
            }
            if (arg.equals("-a")) {
                s.showall
                        = true;
                continue;
            }

            
            s.filename = arg;
        }

        if (s.filename == null) {
            System.err.printf("Error: no filename given\n");
            return null;

        }

        return s;
    }

}
