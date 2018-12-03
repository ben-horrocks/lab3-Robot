package Robot;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Graphics;
import java.lang.*;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.io.*;
import java.util.*;
import java.net.*;

import static java.lang.Math.abs;


// This class draws the probability map and value iteration map that you create to the window
// You need only call updateProbs() and updateValues() from your theRobot class to update these maps
class mySmartMap extends JComponent implements KeyListener {
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    int currentKey;

    int winWidth, winHeight;
    double sqrWdth, sqrHght;
    Color gris = new Color(170,170,170);
    Color myWhite = new Color(220, 220, 220);
    World mundo;
    
    int gameStatus;

    double[][] probs;
    double[][] vals; //pretty sure this is our map
    
    public mySmartMap(int w, int h, World wld) {
        mundo = wld;
        probs = new double[mundo.width][mundo.height];
        vals = new double[mundo.width][mundo.height];
        winWidth = w;
        winHeight = h;
        
        sqrWdth = (double)w / mundo.width;
        sqrHght = (double)h / mundo.height;
        currentKey = -1;
        
        addKeyListener(this);
        
        gameStatus = 0;
    }
    
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }
    
    public void setWin() {
        gameStatus = 1;
        repaint();
    }
    
    public void setLoss() {
        gameStatus = 2;
        repaint();
    }
    
    public void updateProbs(double[][] _probs) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                probs[x][y] = _probs[x][y];
            }
        }
        
        repaint();
    }
    
    public void updateValues(double[][] _vals) {
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                vals[x][y] = _vals[x][y];
            }
        }
        
        repaint();
    }

    public void paint(Graphics g) {
        paintProbs(g);
        //paintValues(g);
    }

    public void paintProbs(Graphics g) {
        double maxProbs = 0.0;
        int mx = 0, my = 0;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (probs[x][y] > maxProbs) {
                    maxProbs = probs[x][y];
                    mx = x;
                    my = y;
                }
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    int col = (int)(255 * Math.sqrt(probs[x][y]));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth), (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(0, (int)(y * sqrHght), (int)winWidth, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth), 0, (int)(x * sqrWdth), (int)winHeight);
        }
        
        //System.out.println("repaint maxProb: " + maxProbs + "; " + mx + ", " + my);
        
        g.setColor(Color.green);
        g.drawOval((int)(mx * sqrWdth)+1, (int)(my * sqrHght)+1, (int)(sqrWdth-1.4), (int)(sqrHght-1.4));
        
        if (gameStatus == 1) {
            g.setColor(Color.green);
            g.drawString("You Won!", 8, 25);
        }
        else if (gameStatus == 2) {
            g.setColor(Color.red);
            g.drawString("You're a Loser!", 8, 25);
        }
    }
    
    public void paintValues(Graphics g) {
        double maxVal = -99999, minVal = 99999;
        int mx = 0, my = 0;
        
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] != 0)
                    continue;
                
                if (vals[x][y] > maxVal)
                    maxVal = vals[x][y];
                if (vals[x][y] < minVal)
                    minVal = vals[x][y];
            }
        }
        if (minVal == maxVal) {
            maxVal = minVal+1;
        }

        int offset = winWidth+20;
        for (int y = 0; y < mundo.height; y++) {
            for (int x = 0; x < mundo.width; x++) {
                if (mundo.grid[x][y] == 1) {
                    g.setColor(Color.black);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 0) {
                    //g.setColor(myWhite);
                    
                    //int col = (int)(255 * Math.sqrt((vals[x][y]-minVal)/(maxVal-minVal)));
                    int col = (int)(255 * (vals[x][y]-minVal)/(maxVal-minVal));
                    if (col > 255)
                        col = 255;
                    g.setColor(new Color(255-col, 255-col, 255));
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 2) {
                    g.setColor(Color.red);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
                else if (mundo.grid[x][y] == 3) {
                    g.setColor(Color.green);
                    g.fillRect((int)(x * sqrWdth)+offset, (int)(y * sqrHght), (int)sqrWdth, (int)sqrHght);
                }
            
            }
            if (y != 0) {
                g.setColor(gris);
                g.drawLine(offset, (int)(y * sqrHght), (int)winWidth+offset, (int)(y * sqrHght));
            }
        }
        for (int x = 0; x < mundo.width; x++) {
                g.setColor(gris);
                g.drawLine((int)(x * sqrWdth)+offset, 0, (int)(x * sqrWdth)+offset, (int)winHeight);
        }
    }

    
    public void keyPressed(KeyEvent e) {
        //System.out.println("keyPressed");
    }
    public void keyReleased(KeyEvent e) {
        //System.out.println("keyReleased");
    }
    public void keyTyped(KeyEvent e) {
        char key = e.getKeyChar();
        //System.out.println(key);
        
        switch (key) {
            case 'i':
                currentKey = NORTH;
                break;
            case ',':
                currentKey = SOUTH;
                break;
            case 'j':
                currentKey = WEST;
                break;
            case 'l':
                currentKey = EAST;
                break;
            case 'k':
                currentKey = STAY;
                break;
        }
    }
}


// This is the main class that you will add to in order to complete the lab
public class theRobot extends JFrame {

    class ValueItem {
        int direction;
        double value;
        boolean wall;

        public ValueItem(int direction, double value) {
            this.direction = direction;
            this.value = value;
            this.wall = false;
        }

        public ValueItem() {
            this.direction = NORTH;
            this.value = 0.0;
            this.wall = false;
        }
    }

    // Mapping of actions to integers
    public static final int NORTH = 0;
    public static final int SOUTH = 1;
    public static final int EAST = 2;
    public static final int WEST = 3;
    public static final int STAY = 4;

    public static final int BLANK_SPACE = 0;
    public static final int WALL = 1;
    public static final int LOSE = 2;
    public static final int WIN = 3;

    Color bkgroundColor = new Color(230, 230, 230);

    static mySmartMap myMaps; // instance of the class that draw everything to the GUI
    String mundoName;

    World mundo; // mundo contains all the information about the world.  See World.java
    double moveProb, sensorAccuracy;  // stores probabilies that the robot moves in the intended direction
    // and the probability that a sonar reading is correct, respectively

    // variables to communicate with the Server via sockets
    public Socket s;
    public BufferedReader sin;
    public PrintWriter sout;

    // variables to store information entered through the command-line about the current scenario
    boolean isManual = false; // determines whether you (manual) or the AI (automatic) controls the robots movements
    boolean knownPosition = false;
    int startX = -1, startY = -1;
    int decisionDelay = 250;

    // store your probability map (for position of the robot in this array
    double[][] probs;

    // store your reward of being in each state (x, y)
    double[][] Vs;

    //store val iteration of being in each state
    ValueItem[][] valIter;

    public theRobot(String _manual, int _decisionDelay) {
        // initialize variables as specified from the command-line
        if (_manual.equals("automatic"))
            isManual = false;
        else
            isManual = true;
        decisionDelay = _decisionDelay;

        // get a connection to the server and get initial information about the world
        initClient();

        // Read in the world
        mundo = new World(mundoName);

        // set up the GUI that displays the information you compute
        int width = 500;
        int height = 500;
        int bar = 20;
        setSize(width, height + bar);
        getContentPane().setBackground(bkgroundColor);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, width, height + bar);
        myMaps = new mySmartMap(width, height, mundo);
        getContentPane().add(myMaps);

        setVisible(true);
        setTitle("Probability and Value Maps");

        doStuff(); // Function to have the robot move about its world until it gets to its goal or falls in a stairwell
    }

    // this function establishes a connection with the server and learns
    //   1 -- which world it is in
    //   2 -- it's transition model (specified by moveProb)
    //   3 -- it's sensor model (specified by sensorAccuracy)
    //   4 -- whether it's initial position is known.  if known, its position is stored in (startX, startY)
    public void initClient() {
        int portNumber = 3333;
        String host = "localhost";

        try {
            s = new Socket(host, portNumber);
            sout = new PrintWriter(s.getOutputStream(), true);
            sin = new BufferedReader(new InputStreamReader(s.getInputStream()));

            mundoName = sin.readLine();
            moveProb = Double.parseDouble(sin.readLine());
            sensorAccuracy = Double.parseDouble(sin.readLine());
            System.out.println("Need to open the mundo: " + mundoName);
            System.out.println("moveProb: " + moveProb);
            System.out.println("sensorAccuracy: " + sensorAccuracy);

            // find out of the robots position is know
            String _known = sin.readLine();
            if (_known.equals("known")) {
                knownPosition = true;
                startX = Integer.parseInt(sin.readLine());
                startY = Integer.parseInt(sin.readLine());
                System.out.println("Robot's initial position is known: " + startX + ", " + startY);
            } else {
                System.out.println("Robot's initial position is unknown");
            }
        } catch (IOException e) {
            System.err.println("Caught IOException: " + e.getMessage());
        }
    }

    // function that gets human-specified actions
    // 'i' specifies the movement up
    // ',' specifies the movement down
    // 'l' specifies the movement right
    // 'j' specifies the movement left
    // 'k' specifies the movement stay
    int getHumanAction() {
        System.out.println("Reading the action selected by the user");
        while (myMaps.currentKey < 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        int a = myMaps.currentKey;
        myMaps.currentKey = -1;

        System.out.println("Action: " + a);

        return a;
    }

    void initialize_values() {
        for (int x = 0; x < mundo.width; x++) {
            for (int y = 0; y < mundo.height; y++) {
                valIter[x][y] = new ValueItem();
                switch (mundo.grid[x][y]) {
                    case BLANK_SPACE:
                    case WALL:
                        Vs[x][y] = 0;
                        break;
                    case LOSE:
                        Vs[x][y] = -10;
                        break;
                    case WIN:
                        Vs[x][y] = 10;
                        break;
                }
            }
        }
    }

    // initializes the probabilities of where the AI is
    void initializeProbabilities() {
        probs = new double[mundo.width][mundo.height];
        Vs = new double[mundo.width][mundo.height];
        valIter = new ValueItem[mundo.width][mundo.height];
        // if the robot's initial position is known, reflect that in the probability map
        initialize_values();
        if (knownPosition) {
            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if ((x == startX) && (y == startY))
                        probs[x][y] = 1.0;
                    else
                        probs[x][y] = 0.0;
                }
            }
        } else {  // otherwise, set up a uniform prior over all the positions in the world that are open spaces
            int count = 0;

            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        count++;
                }
            }

            for (int y = 0; y < mundo.height; y++) {
                for (int x = 0; x < mundo.width; x++) {
                    if (mundo.grid[x][y] == 0)
                        probs[x][y] = 1.0 / count;
                    else
                        probs[x][y] = 0;
                }
            }
        }

        myMaps.updateProbs(probs);
    }

    double[][] Discrete_Bayes_Filter(double[][] previous_belief, int action, String reading) {
//        for all xt in Xt do
//        Bel’(xt) = Σxt-1 p(xt | at, xt-1) Bel(xt-1)
//        Bel(xt) = η p(zt | xt) Bel’(xt)
//            endfor
//        return Bel(Xt)
        double[][] new_belief = new double[previous_belief.length][previous_belief[0].length];
        double normalizing_factor = 0;
        for (int x = 1; x < previous_belief.length - 1; x++) {
            for (int y = 1; y < previous_belief[x].length - 1; y++) {
                double belief_bar = 0.0;
                if (mundo.grid[x][y] == 0) {
                    belief_bar += previous_belief[x][y] * (action == STAY ? moveProb : (1 - moveProb) / 4);//*p(state| action, previous_state
                    if (mundo.grid[x + 1][y] == 0) {
                        belief_bar += previous_belief[x + 1][y] * (action == WEST ? moveProb : (1 - moveProb) / 4);
                    }
                    if (mundo.grid[x - 1][y] == 0) {
                        belief_bar += previous_belief[x - 1][y] * (action == EAST ? moveProb : (1 - moveProb) / 4);
                    }
                    if (mundo.grid[x][y + 1] == 0) {
                        belief_bar += previous_belief[x][y + 1] * (action == NORTH ? moveProb : (1 - moveProb) / 4);
                    }
                    if (mundo.grid[x][y - 1] == 0) {
                        belief_bar += previous_belief[x][y - 1] * (action == SOUTH ? moveProb : (1 - moveProb) / 4);
                    }
                }
                double accuracy = (mundo.grid[x][y - 1] == Character.getNumericValue(reading.charAt(0)) ? sensorAccuracy : (1 - sensorAccuracy)) *
                        (mundo.grid[x][y + 1] == Character.getNumericValue(reading.charAt(1)) ? sensorAccuracy : (1 - sensorAccuracy)) *
                        (mundo.grid[x + 1][y] == Character.getNumericValue(reading.charAt(2)) ? sensorAccuracy : (1 - sensorAccuracy)) *
                        (mundo.grid[x - 1][y] == Character.getNumericValue(reading.charAt(3)) ? sensorAccuracy : (1 - sensorAccuracy));
                new_belief[x][y] = belief_bar * accuracy;
                normalizing_factor += new_belief[x][y];
            }
        }

        for (int x = 0; x < previous_belief.length; x++) {
            for (int y = 0; y < previous_belief[x].length; y++) {

                new_belief[x][y] = new_belief[x][y] / normalizing_factor;
            }
        }
        return new_belief;

    }


    boolean is_expected(int x, int y, String sonar) {
        return mundo.grid[x][y - 1] == Character.getNumericValue(sonar.charAt(0)) &&
                mundo.grid[x][y + 1] == Character.getNumericValue(sonar.charAt(1)) &&
                mundo.grid[x + 1][y] == Character.getNumericValue(sonar.charAt(2)) &&
                mundo.grid[x - 1][y] == Character.getNumericValue(sonar.charAt(3));
    }

    // TODO: update the probabilities of where the AI thinks it is based on the action selected and the new sonar readings
    //       To do this, you should update the 2D-array "probs"
    // Note: sonars is a bit string with four characters, specifying the sonar reading in the direction of North, South, East, and West
    //       For example, the sonar string 1001, specifies that the sonars found a WALL in the North and West directions, but not in the South and East directions
    void updateProbabilities(int action, String sonars) {
        // your code
        double[][] temp = Discrete_Bayes_Filter(probs, action, sonars);
        double total = 0.0;
        for (int x = 1; x < temp.length; x++) {
            for (int y = 1; y < temp[x].length; y++) {
                probs[x][y] = temp[x][y];
            }
        }
        System.out.println(total);
        myMaps.updateProbs(probs); // call this function after updating your probabilities so that the
        //  new probabilities will show up in the probability map on the GUI
    }


    // This is the function you'd need to write to make the robot move using your AI;
    // You do NOT need to write this function for this lab; it can remain as is
    int automaticAction() {

        if (knownPosition) {
            return moveOptimalKnown(); //just follows value map since I know where I am.
        } else {
            return moveOptimalUnknown(); //calcuates utility for each move according to my belief in my position and
            //the value map, returning the highest.
        }
    }

    double computeProbValSum(int x, int y, double penalty, int action) {
        //CURERNTLY, THIS DOESN'T ACCOUNT FOR WALLS.
        double probability_sum_total = 0.0;
        double move_wrong_probability = (1 - moveProb) / 4;
        if (action == STAY) {
            probability_sum_total += (moveProb * valIter[x][y].value);
        } else {
            probability_sum_total += (move_wrong_probability * valIter[x][y].value);
        }
        if (action == NORTH) {
            probability_sum_total += (moveProb * valIter[x][y - 1].value);
        } else {
            probability_sum_total += (move_wrong_probability * valIter[x][y - 1].value);
        }
        if (action == EAST) {
            probability_sum_total += (moveProb * valIter[x + 1][y].value);
        } else {
            probability_sum_total += (move_wrong_probability * valIter[x + 1][y].value);
        }
        if (action == SOUTH) {
            probability_sum_total += (moveProb * valIter[x][y + 1].value);
        } else {
            probability_sum_total += (move_wrong_probability * valIter[x][y + 1].value);
        }
        if (action == WEST) {
            probability_sum_total += (moveProb * valIter[x - 1][y].value);
        } else {
            probability_sum_total += (move_wrong_probability * valIter[x - 1][y].value);
        }
        return Vs[x][y] + (penalty * probability_sum_total);
    }

    void valueIteration() {
        double penalty = .9;
        double iteration_delta;
        boolean first_pass = true;
        do {
            iteration_delta = 0;
            ValueItem newValIter[][] = new ValueItem[mundo.width][mundo.height];
            for (int x = 0; x < mundo.grid.length; x++) {
                for (int y = 0; y < mundo.grid.length; y++) {
                    newValIter[x][y] = new ValueItem();
                    //check if i'm on a blank square.
                    if (mundo.grid[x][y] == BLANK_SPACE) {
                        //STAY
                        double best_value_so_far = computeProbValSum(x, y, penalty, NORTH);
                        int best_direction_so_far = NORTH;
                        //NORTH
                        double current_sum = computeProbValSum(x, y, penalty, SOUTH);

                        if (best_value_so_far < current_sum) {
                            //check if moving south will go to wall if so skip two lines
                            best_value_so_far = current_sum;
                            best_direction_so_far = SOUTH;
                        }
                        //EAST
                        current_sum = computeProbValSum(x, y, penalty, EAST);
                        if (best_value_so_far < current_sum) {
                            //check if moving east will go to wall if so skip two lines
                            best_value_so_far = current_sum;
                            best_direction_so_far = EAST;
                        }
                        //WEST
                        current_sum = computeProbValSum(x, y, penalty, WEST);
                        if (best_value_so_far < current_sum) {
                            //check if moving west will go to wall if so skip two lines
                            best_value_so_far = current_sum;
                            best_direction_so_far = WEST;
                        }
                        newValIter[x][y].value = best_value_so_far;
                        newValIter[x][y].direction = best_direction_so_far;
                        double current_iteration_delta = abs(valIter[x][y].value - newValIter[x][y].value);
                        if (iteration_delta < current_iteration_delta) {
                            iteration_delta = current_iteration_delta;
                        }
                    } else if (mundo.grid[x][y] == WIN || mundo.grid[x][y] == LOSE) {
                        newValIter[x][y].value = Vs[x][y];
                        newValIter[x][y].direction = STAY;
                    } else {
                        newValIter[x][y].value = Vs[x][y];
                        newValIter[x][y].direction = STAY;
                        newValIter[x][y].wall = true;
                    }
                }
            }
            valIter = newValIter;
            if (first_pass) {
                iteration_delta = 1;
                first_pass = false;
            }
        } while (iteration_delta > .1);
        //create value iteration for the given map
        //for all states
        //  U_t+1(s) = R(s)+ ()[max_a=A(s)sum of s' in S{P(s'|s,a)U_t(s')}]
        //repeat for loop till no changes greater than threshold X

        //Policy_t(s) = arg max a in A sum of s in S{P(s'|s,a)U(s')}

    }

    int moveOptimalKnown() {
        //move in direction you're supposed to if your at this spot. Since we aren't really paying attention to walls
        //I should check if the move I select would head to a WALL, and if it does, pick the next highest utility.
        for (int x = 1; x < mundo.width - 1; x++) {
            for (int y = 1; y < mundo.height - 1; y++) {
                if (probs[x][y] == 1) {
                    return valIter[x][y].direction;
                }
            }
        }
        System.out.println("WE DON'T KNOW THE ROBOT'S POSITION AND WE SHOULD!");
        return -1;
    }

    //returns utility from performing action in posisiton x and y. I think I have my x+-1 and y+-1's messed up. NOT WORKING
    double util_from_action(int x, int y, int action) {
        if (action == NORTH)
            return valIter[x][y-1].value;
        else if (action == SOUTH)
            return valIter[x][y+1].value;
        else if (action == EAST)
            return valIter[x-1][y].value;
        else
            return valIter[x+1][y].value;
    }

    int moveOptimalUnknown() {
       // ValueItem best_so_far = null;
        double best_probabiltiy_so_far = 0;
        
        //TRIES TO DO: V(a) = Sum of all states BEL(s)* Q(s,a) <- expected util from making action a in state s
        //maximizes V(a) for all actions a. NOT WORKING RIGHT NOW. 
//        double best_util_so_far = 0;
//        int policy = STAY;
//        for (int action = 0; action < 4; action++) {
//            int expected_util = 0;
//            for (int x = 1; x < mundo.width - 1; x++) {
//                for (int y = 1; y < mundo.height - 1; y++) {
//                    expected_util += probs[x][y] * util_from_action(x, y, action);
//                }
//            }
//            if (expected_util > best_util_so_far) {
//                policy = action;
//            }
//        }
//        return policy;
        
        //THIS JUST RETURNS THE BEST ACTION FOR THE HIGHEST PROBABILITY POSITION
//        for (int x = 1; x < mundo.width - 1; x++) {
//            for (int y = 1; y < mundo.height - 1; y++) {
//                if (best_so_far == null)
//                {
//                    best_so_far = valIter[x][y];
//                    best_probabiltiy_so_far = probs[x][y];
//                }
//                else
//                {
//                    if (probs[x][y] > best_probabiltiy_so_far)
//                    {
//                        best_probabiltiy_so_far = probs[x][y];
//                        best_so_far = valIter[x][y];
//                    }
//                }
//            }
//        }
//        if(best_probabiltiy_so_far > .2)
//            return best_so_far.direction;
//        else
//            return STAY;

        //CURRENT BEST RETURN, Adds of the probs of each state that says north/south/east/west is their best action and
        //chooses the one with the highest.
        double north_util = 0;
        double south_util = 0;
        double east_util = 0;
        double west_util = 0;
        for (int x = 1; x < mundo.width - 1; x++) {
            for (int y = 1; y < mundo.height - 1; y++) {
                switch(valIter[x][y].direction)
                {
                    case NORTH:
                        north_util += probs[x][y];
                        break;
                    case SOUTH:
                        south_util += probs[x][y];
                        break;
                    case EAST:
                        east_util += probs[x][y];
                        break;
                    case WEST:
                        west_util += probs[x][y];
                        break;
                    default:
                        break;
                }
            }
        }
        best_probabiltiy_so_far = north_util;
        int best_action = NORTH;
        if(south_util > best_probabiltiy_so_far)
        {
            best_probabiltiy_so_far = south_util;
            best_action = SOUTH;
        }
        if(east_util > best_probabiltiy_so_far)
        {
            best_probabiltiy_so_far = east_util;
            best_action = EAST;
        }
        if(west_util > best_probabiltiy_so_far)
        {
            best_action = WEST;
        }
        return best_action;
    }

    void doStuff() {
        int action;

        initializeProbabilities();  // Initializes the location (probability) map
        valueIteration();  // TODO: function you will write in Part II of the lab
        System.out.println(Arrays.deepToString(valIter));
        while (true) {
            try {
                if (isManual)
                    action = getHumanAction();  // get the action selected by the user (from the keyboard)
                else
                    action = automaticAction(); // TODO: get the action selected by your AI;
                                                // you'll need to write this function for part III
                
                sout.println(action); // send the action to the Server
                
                // get sonar readings after the robot moves
                String sonars = sin.readLine();
                System.out.println("Sonars: " + sonars);
                //sonars[0] = top
                //sonars[0] = bottom
                //sonars[0] = right
                //sonars[0] = left
            

                
                if (sonars.length() > 4) {  // check to see if the robot has reached its goal or fallen down stairs
                    if (sonars.charAt(4) == 'w') {
                        System.out.println("I won!");
                        myMaps.setWin();
                        break;
                    }
                    else if (sonars.charAt(4) == 'l') {
                        System.out.println("I lost!");
                        myMaps.setLoss();
                        break;
                    }
                }
                else {
                    updateProbabilities(action, sonars); // TODO: this function should update the probabilities of where the AI thinks it is
                    // here, you'll want to update the position probabilities
                    // since you know that the result of the move as that the robot
                    // was not at the goal or in a stairwell
                }
                Thread.sleep(decisionDelay);  // delay that is useful to see what is happening when the AI selects actions
                                              // decisionDelay is specified by the send command-line argument, which is given in milliseconds
            }
            catch (IOException e) {
                System.out.println(e);
            }
            catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // java theRobot [manual/automatic] [delay]
    public static void main(String[] args) {
        theRobot robot = new theRobot(args[0], Integer.parseInt(args[1]));  // starts up the robot
    }
}