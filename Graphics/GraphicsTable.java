package Graphics;

import GameLogic.*;
import Graphics.GUI.GUI;
import Networking.ShittyAI;
import Networking.PlayerProfiles.PlayerProfile;

import java.awt.Graphics;
import java.util.*;

public class GraphicsTable extends Table {
    protected TableRenderer tr;
    GUI gui;
    Window win;
    boolean showCards = true;
    int repeated = 0;
    protected List<PlayerProfile> profiles;

    protected GraphicsTable(Window win, GUI gui){
        super();
        profiles = new ArrayList<PlayerProfile>();
        tr = new TableRenderer(win);
        this.win = win;
        this.gui = gui;
    }

    void start(){
        startGame();
    }

    protected void Render(Graphics g){
        tr.Render(g, pot, community, players, profiles, com_turnover, showCards ? turn : -1, end);
    }

    public void addPlayer(PlayerProfile pp, boolean ai){
        addPlayer(pp.username);
        players.get(players.size() - 1).AI = ai;
        profiles.add(pp);
    }

    @Override
    protected void round(int num){
        com_turnover = comt[num];
        repeated = 0;
        do {
            repeated++;
            for(turn = 0; turn < players.size(); turn++){
                if(players.get(turn).playing){
                    playerTurn(turn);
                }
            }
        } while(!allAdvance());
    }

    @Override
    protected void playerTurn(int turnNum){
        showCards = false;
        Player p = players.get(turnNum);
        if(!p.AI)
            new TurnGUI(p).takeTurn(gui, this);
        else
            ShittyAI.takeAITurn(p, repeated);
    }

    @Override
    protected void printEnd(Player winner){
        SoundEngine.playSound("res/aud/ohmygod.wav");
        
        Thread main = Thread.currentThread();
        gui.queueText(winner.name + " wins $" + pot, 0.5f, 0.3f, 0.02f, 0.1f);
        gui.queueButton(
            "Play Again", 
            0.5f, 0.5f, 0.3f, 0.1f,
            () -> { main.interrupt(); gui.applyQueue();}
        );
        gui.queueButton(
            "Menu", 
            0.5f, 0.65f, 0.3f, 0.1f,
            () -> { main.interrupt(); win.g.renderGame = false;}
        );
        gui.queueButton(
            "Quit", 
            0.5f, 0.8f, 0.3f, 0.1f,
            () -> { main.interrupt(); System.exit(0); }
        );
        gui.applyQueue();
        for(int i = 0; i < profiles.size(); i++){
            profiles.get(i).lifetimeChips += (players.get(i).money - 20);
        }
        try {
            Thread.sleep(Long.MAX_VALUE); //sleeps for 292.5 billion years
        } catch (InterruptedException e) {}
    }
}
