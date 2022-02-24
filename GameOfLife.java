import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder;

public class GameOfLife {
    //smart error printing
    public static void printError(String a_errorTxt) {
        //check how wide the error box should be
        int lineLength;
        if(a_errorTxt.length() < 100) lineLength = 30;
        else if(a_errorTxt.length() < 200) lineLength = 50;
        else lineLength = 70;
        //print the first part of the error message
        System.out.print("\n ");
        for(int i = 0; i < lineLength + 2; i++) System.out.print("_");
        System.out.println();

        System.out.print("| ");
        for(int i = 0; i < Math.floor(lineLength / 2) - 3; i++) System.out.print(" ");
        System.out.print("ERROR");
        for(int i = 0; i < Math.floor(lineLength / 2) - 2; i++) System.out.print(" ");
        System.out.println(" |");

        System.out.print("| ");
        for(int i = 0; i < lineLength; i++) System.out.print(" ");
        System.out.println(" |");

        //print the actual error depending sectioned off correctly
        if(a_errorTxt.length() <= lineLength) { //print error on one line
            System.out.print("| ");
            System.out.print(a_errorTxt);
            for(int i = 0; i < (lineLength - a_errorTxt.length()); i++) System.out.print(" ");
            System.out.println(" |");
        } else { //print error on multiple lines
            int charTracker = 0;
            Boolean isFinished = false;
            String currLine = "";
            String currWord = "";
            while(!isFinished) {
                //start of a new line
                System.out.print("| ");

                //build the line
                for(int i = 0; i < lineLength; i++) {
                    //check if we're at the end of the error message.
                    if(charTracker >= a_errorTxt.length()) {
                        isFinished = true;
                        currLine = currLine + currWord;
                        currWord = "";
                        break;
                    }

                    if(a_errorTxt.charAt(charTracker) != ' ') {
                        if(a_errorTxt.charAt(charTracker) == '\n') { //logic for a \n
                            currLine = currLine + currWord;
                            currWord = "";
                            charTracker++;
                            break;
                        }
                        currWord = currWord + a_errorTxt.charAt(charTracker);
                    } else {
                        currLine = currLine + currWord + " ";
                        currWord = "";
                    }
                    charTracker++;
                }

                //test if we have half a word in our currWord.  If we do, backtrack charTracker accordingly
                if(currWord.length() > 0) charTracker -= currWord.length();

                //print the line
                System.out.print(currLine);
                for(int i = 0; i < (lineLength - currLine.length()); i++) System.out.print(" ");
                System.out.println(" |");

                //reset currWord & currLine
                currWord = "";
                currLine = "";
            }
        }
        System.out.print("| ");
        for(int i = 0; i < lineLength; i++) System.out.print(" ");
        System.out.print(" |\n ");
        for(int i = 0; i < lineLength + 2; i++) System.out.print("-");
        System.out.println("\n\n");
    }
    
    //print the board in a formatted way (I realized that I could have used printf() functionality instead of all of this printing logic)
    public static void printBoard(Boolean[][] a_board, Boolean a_isEditing) {
        //build a single string that represents our board.
        int heightSpaceNum = Integer.toString(a_board.length).length();
        int widthSpaceNum = Integer.toString(a_board[0].length).length();
        String board = "";
        if(a_isEditing) board = board + "\n   ";
        else board = board + "\r";

        //only include the column headers and dashes if the user is editing the board
        if(a_isEditing) {
            //include the column headers
            for(int i = 0; i < heightSpaceNum; i++) board = board + " ";
            for(int i = 0; i < a_board[0].length; i++) {
                board = board + i + " "; //add column numbers
                for(int j = 0; j < widthSpaceNum - Integer.toString(i).length(); j++) board = board + " ";
            }

            //include a row of dashes to separate everything
            board = board + "\n  ";
            for(int i = 0; i < heightSpaceNum; i++) board = board + " "; //space left of dashes
            for(int i = 0; i < a_board[0].length; i++) {
                board = board + "-"; //default dashes
                for(int j = 0; j < widthSpaceNum; j++) board = board + "-"; //an extra dash for extra numbers
            }
            board = board + "\n";
        }
        

        //print the actual board
        for(int i = 0; i < a_board.length; i++) {
            //only print row headers if the user is editing the board
            if(a_isEditing) {
                //print row headers
                for(int j = 0; j < heightSpaceNum - Integer.toString(i).length(); j++) board = board + " ";
                board = board + i + " | ";
            }
            

            //print the row
            for(int j = 0; j < a_board[i].length; j++) {
                if(a_board[i][j]) board = board + "o"; //symbol of an alive cell
                else board = board + " "; //dead cells stay as blanks

                for(int k = 0; k < widthSpaceNum; k++) board = board + " ";
            }
            board = board + "\n";
        }
        board = board + "\n";
        
        //print the board on a new line if the user is editing the board, otherwise each print will replace the last.
        System.out.println(board);
    }
    
    public static Boolean[][] nextGen(Boolean[][] a_board) {
        Boolean[][] board = new Boolean[a_board.length][a_board[0].length];

        /*alter the board according to these 3 rules
         *
         * For a space that is populated:
         * 1) Each cell with one or no neighbors dies, as if by solitude.
         * 2) Each cell with four or more neighbors dies, as if by overpopulation.
         * 3) Each cell with two or three neighbors survives.
         * 
         * For a space that is empty/unpopulated:
         * 1) Each cell with 3 neighbors becomes populated
         */

         for(int i = 0; i < board.length; i++) { //loop over columns
             for(int j = 0; j < board[i].length; j++) { //loop over rows
                int numNeighbors = 0;
                 //check how many living neighbors this particular cell has
                for(int m = i - 1; m <= i + 1; m++) { // check columns
                    for(int n = j - 1; n <= j + 1; n++) { //check rows
                        if (m >= 0 && n >= 0 && m < board.length && n < board[i].length && !(m == i && n == j)) { //don't go out of bounds
                            if(a_board[m][n]) numNeighbors++; //test for a living neighbor
                        }
                    }
                }
                 if(a_board[i][j]) { //for a space that is populated
                    
                    switch(numNeighbors) {
                        //if the cell has two or three neighbors it survives
                        case 2:
                        case 3:
                            board[i][j] = a_board[i][j];
                            break;
                        //all other scenarios end with the cell dying
                        default:
                            board[i][j] = !a_board[i][j];
                            break;
                    }
                 } else { //for a space that is empty/unpopulated
                    if(numNeighbors == 3) board[i][j] = !a_board[i][j];
                    else board[i][j] = a_board[i][j];
                 }
             }
         }
        return board;
    }
    public static void main(String[] args) {
        //declare local variables
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(System.in));
        Boolean correctInput = true;

        //get the width & height of the board
        int boardWidth = -1;
        int boardHeight = -1;
        if(args.length == 2) { //get the width and height through the command line
            try {
                boardWidth = Integer.parseInt(args[0]);
                boardHeight = Integer.parseInt(args[1]);
            } catch(Exception e) { //if they entered something in error
                printError("The input '" + args[0] + "', '" + args[1] + "' was not valid input.  Enter the width, then height of the board as integers in the command line...");
                System.out.println("Exiting...");
                System.exit(0);
            }
        } else if(args.length > 0) {
            printError("Your command line arguments didn't make sense.  Please enter the width, then height of the board as integers in the command line...");
        } else { //have them manually input the width and height of the board
            System.out.println("Enter the width and then the height of the board as Integers:");
            do { //get the width of the board
                System.out.print("Width: ");
                try {
                    boardWidth = Integer.parseInt(reader.readLine());
                    correctInput = true;
                } catch(Exception e) {
                    printError("Your input didn't make sense.  The width of your board should be an integer written as a number...");
                    correctInput = false;
                }
            } while(!correctInput);
            
            do {
                System.out.print("Height: ");
                try {
                    boardHeight = Integer.parseInt(reader.readLine());
                    correctInput = true;
                } catch(Exception e) {
                    printError("Your input didn't make sense.  The height of your board should be an integer written as a number...");
                    correctInput = false;
                }
            } while(!correctInput);
        }
        if(boardWidth < 0 || boardHeight < 0) { //if the width and height are still  -1, quit
            printError("The width and height may have not been initialized...");
            System.out.println("Exiting...");
            System.exit(0);
        }
        
        //build & initialize the board
        Boolean[][] board = new Boolean[boardHeight][boardWidth];
        for(int i = 0; i < board.length; i++) for(int j = 0; j < board[i].length; j++) board[i][j] = false;

        //start editing
        Boolean stillEditing = true;
        int xCoord = -1;
        int yCoord = -1;
        while(stillEditing) {
            //print the board
            printBoard(board, true);
            
            //prompt for change
            
                System.out.println("Specify the coordinates of the cell you want to toggle by entering the x coordinate, and then the y coordinate");
                System.out.println("Or type 'f' to finish editing the board...");
            do {
                System.out.print("x: ");
                try {
                    String xCoord_str = reader.readLine();
                    if(xCoord_str.toLowerCase().equals("f")) stillEditing = false;
                    else {
                        xCoord = Integer.parseInt(xCoord_str);

                        System.out.print("y: ");
                        String yCoord_str = reader.readLine();
                        if(yCoord_str.toLowerCase().equals("f")) stillEditing = false;
                        else {
                            yCoord = Integer.parseInt(yCoord_str);
                            correctInput = true;

                            //toggle the corresponding cell
                            board[yCoord][xCoord] = !board[yCoord][xCoord];
                        }
                    }
                } catch(Exception e) {
                    correctInput = false;
                    printError("An exception has occured.  Please enter just the specified coordinate as a positive integer...");
                }
            } while(!correctInput);
        }
        //now display the menu of what to do with the board
        float timeInterval = 1;
        int numIntervals = 100;
        String menuOpt = "";
        while(true) {
            System.out.println("\nChoose from the following menu options...");
            System.out.println("Q : quit program");
            System.out.printf("T : select the time interval for printing the board (current: %4.2f second(s))\n", timeInterval);
            System.out.printf("I : select the number of intervals to play through (current: %d)\n", numIntervals);
            System.out.println("P : play the game of life\n");

            do {
                try{
                    System.out.print("Menu Option: ");
                    menuOpt = reader.readLine();
                    correctInput = true;

                    if(!(menuOpt.toLowerCase().equals("q") ||
                         menuOpt.toLowerCase().equals("t") ||
                         menuOpt.toLowerCase().equals("i") ||
                         menuOpt.toLowerCase().equals("p"))) throw new Exception();
                } catch (Exception e) {
                    correctInput = false;
                    printError("That input didn't make sense.  Input should be either a 'q', 't', 'i', 'p' as input...");
                }
            } while(!correctInput);

            switch(menuOpt) {
                case "q":
                    System.out.println("\nThank you for playing Conways Game of Life");
                    System.exit(0);
                    break;
                case "t":
                    System.out.println("Specify how long in seconds each generation takes to pass: ");
                    do {
                        try {
                            System.out.print("Seconds: ");
                            timeInterval = Float.parseFloat(reader.readLine());
                            correctInput = true;

                            if(timeInterval <= 0) throw new Exception();
                        } catch (Exception e) {
                            correctInput = false;
                            printError("That input didn't make sense.  Enter a positive number...");
                        }
                    } while(!correctInput);
                    break;
                case "i":
                    System.out.println("Select the number of intervals to play through:");
                    do {
                        try {
                            System.out.print("Intervals: ");
                            numIntervals = Integer.parseInt(reader.readLine());
                            correctInput = true;

                            if(numIntervals < 0) throw new Exception();
                        } catch(Exception e) {
                            correctInput = false;
                            printError("That input didn't make sense.  Enter a non-negative integer as the number of intervals...");
                        }
                    } while(!correctInput);
                    break;
                case "p":
                    System.out.printf("Playing through %d intervals at a rate of 1 generation per %4.2f second(s)...\n\n", numIntervals, timeInterval);
                    //countdown timer to begin
                    for(int i = 0; i < 5; i++) {
                        try{
                            System.out.print(" " + i + "\r");
                            Thread.sleep(1000);
                        } catch(Exception e) {
                            printError("Unknown error occurred while counting down...");
                            System.exit(0);
                        }
                    }

                    for(int i = 0; i < numIntervals; i++) {
                        //print the board
                        printBoard(board, false);

                        //alter the board according to the rules
                        board = nextGen(board);

                        //wait the allotted amount of time to print a new generation
                        try {
                            Thread.sleep((long)timeInterval * 1000);

                            //clear the board
                            String currentOS = System.getProperty("os.name");
                            if(currentOS.contains("Windows")) { //clear on Windows OS
                                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
                                Process startProcess = pb.inheritIO().start();
                                startProcess.waitFor();
                            } else { //clear on any other OS
                                ProcessBuilder pb = new ProcessBuilder("clear");
                                Process startProcess = pb.inheritIO().start();
                                startProcess.waitFor();
                            }
                        } catch(Exception e) {
                            printError("There was an error in waiting " + timeInterval + " seconds...");
                        }
                    }
                    break;
            }
        }
    }
}
