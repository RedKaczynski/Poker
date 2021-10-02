import java.util.*;

public class Table {
    List<Player> players;
    List<Card> community;
    Deck deck;
    int pot;
    int turn;
    int com_turnover;
    final int[] comt = {0, 3, 4, 5};
    Scanner scanner;
    
    Table() {
        players = new ArrayList<Player>();
        deck = new Deck();
        reset();
        scanner = new Scanner(System.in);
    }

    //resets the table
    void reset() {
        deck.reset();
        pot = 0;
    }

    //adds a player to the list of players
    void addPlayer(String name){
        players.add(new Player(name, this));
    }

    //deals from the deck to a player
    void dealToPlayer(int pnum){
        players.get(pnum).dealTo(deck.copyAndRemove(2));
    }

    //starts the game
    void startGame() {
        reset();
        for(int i = 0; i < players.size(); i++){
            dealToPlayer(i);
        }
        community = deck.copyAndRemove(5);
    }

    //runs a game, which is defined as a single deal
    void game(){
        startGame(); //starts the game
        com_turnover = 5;
        //first round
        players.forEach(p->{p.deficit = 2; p.playing = true;});
        for(int i = 0; i < 4; i++){
            round(i);
        }
        Player winner = determineWinner();
        winner.money += pot;
        winner.wins++;
        printEnd(winner);
        pot = 0;
    }

    //finds the winner
    Player determineWinner(){
        Player p = null;
        handValue current_highest = new handValue(0, 0);
        for(int i = 0; i < players.size(); i++){
            if(players.get(i).playing){
                handValue hv = players.get(i).handvalue();
                if(hv.value > current_highest.value){
                    p = players.get(i);
                    current_highest = p.handvalue();
                }
                if(hv.value == current_highest.value){
                    if(hv.highcard > current_highest.highcard){
                        p = players.get(i);
                        current_highest = p.handvalue();
                    }
                }
            }
        }
        return p;
    }

    //round
    void round(int num){
        System.out.println("Round " + (num + 1) + ", press enter to continue");
        scanner.nextLine();
        com_turnover = comt[num];
        do {
            for(int i = 0; i < players.size(); i++){
                if(players.get(i).playing){
                    playerTurn(i);
                }
            }
        } while(!allAdvance());
    }

    boolean canAdvance(int pnum) {
        return players.get(pnum).deficit == 0 || !players.get(pnum).playing;
    }

    boolean allAdvance() {
        for(int i = 0; i < players.size(); i++){
            if (!canAdvance(i)) return false;
        }
        return true;
    }
        
    public void raiseDeficit(int i) {
        players.forEach(player -> {player.deficit += i;});
    }

    //whoooo boy
    void playerTurn(int turnNum){
        Player p = players.get(turnNum);
        System.out.println(
            "It is now " + p.name + "'s turn, press enter to continue"
        );
        scanner.nextLine();
        printTable(turnNum);
        p.takeTurn(scanner);
        clearConsole();
    }

    //prints the table
    void printTable(int turn){
        clearConsole();

        System.out.println("Pot: $" + pot + "\n");
        System.out.println("community:");
        printCommunity();
        System.out.println("\n");

        //prints each player
        for(int i = 0; i < players.size(); i++){
            players.get(i).printHand(i == turn);
            System.out.println();
        }
    }

    //for end of game
    void printEnd(Player winner){
        clearConsole();
        System.out.println("community:");
        printCommunity();
        System.out.println("\n");
        for(int i = 0; i < players.size(); i++){
            players.get(i).printHand(true);
            System.out.println();
        }
        System.out.println(winner.name + " wins $" + pot + "!!");
    }

    //prints the community cards
    void printCommunity(){
        for(int i = 0; i < com_turnover; i++){
            community.get(i).print();
        }
        for(int i = com_turnover; i < 5; i++){
            System.out.print("███");
        }
    }

    //helper method i just put here
    void clearConsole(){
        System.out.print("\033[H\033[2J");  
        System.out.flush();
    }
}