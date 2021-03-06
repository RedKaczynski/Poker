package Graphics;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.awt.geom.*;

import GameLogic.*;
import Networking.PlayerProfiles.PlayerProfile;

import javax.imageio.ImageIO;

class pPos {
    final static float[][] p = {
        {0.93f, 0.50f, -90.0f}, //right
        {0.67f, 0.13f, 180.0f}, //top r
        {0.50f, 0.13f, 180.0f}, //top c
        {0.33f, 0.13f, 180.0f}, //top l
        {0.07f, 0.50f,  90.0f}, //left
        {0.33f, 0.87f,   0.0f}, //bottom l
        {0.50f, 0.87f,   0.0f}, //bottom c
        {0.67f, 0.87f,   0.0f}  //bottom r
    };

    static int getX(int pos, int width){
        return (int)(width * p[pos][0]);
    }
    static int getY(int pos, int height){
        return (int)(height * p[pos][1]);
    }
    static float getR(int pos){
        return p[pos][2];
    }

    static int[] playerIndexes(int num){
        switch(num){
            case 2:
                return new int[]{2, 6};
            case 3:
                return new int[]{1, 4, 7};
            case 4:
                return new int[]{0, 2, 4, 6};
            case 5:
                return new int[]{1, 3, 4, 5, 7};
            case 6:
                return new int[]{0, 1, 3, 4, 5, 7};
            default:
                return null;
        }
    }
}

public class TableRenderer {
    BufferedImage chip;
    Window win;
    float[][] chipLoc;

    Random r;

    TableRenderer(Window w){
        r = new Random();
        new CardRenderer();
        win = w;
        try {
            chip = ImageIO.read(new File("res/tex/chip.png"));
        } catch (IOException e) {
            System.out.println("FAIL");
        }

        chipLoc = new float[50][2];
        for(int i = 0; i < 50; i++){
            float x = r.nextFloat(),
                y = r.nextFloat();  
            x *= x;
            x += 0.5f;
            x /= 1.5;
            x = r.nextBoolean() ? x : 1.0f - x;
            chipLoc[i][0] = 0.32f + x * 0.3f;
            chipLoc[i][1] = 0.29f + y * 0.2f;
        }
    }

    public void Render(Graphics g, int pot, java.util.List<Card> com, java.util.List<Player> players, java.util.List<PlayerProfile> ppfs, int turnover, int turn, boolean showall){
        int h = win.g.paneheight, w = win.g.panewidth;
        int t = 0;
        for(int i : pPos.playerIndexes(players.size())){
            renderPlayer((Graphics2D)g, pPos.getX(i, w), pPos.getY(i, h), pPos.getR(i), t==turn || showall, players.get(t), ppfs.get(t));
            t++;
        }

        if(com != null)
            for(int i = 0; i < 5; i++) {
                CardRenderer.drawCard(g, turnover > i, (int)(w * (0.25f + (0.08f * i))), (int)(h * 0.65f) - 70, com.get(i).suit, com.get(i).num, 100);
            }
        for(int i = 0; i < 10; i++)
            CardRenderer.drawCard(g, false, (int)(w * (0.65f)), (int)(h * 0.65f) - 70 - i, 0, 0, 100);
        drawPot(g, pot, w, h);
    };

    //renders a player's hand and their chips
    void renderPlayer(Graphics2D g, int x, int y, float r, boolean show, Player p, PlayerProfile ppf){
        AffineTransform bt = g.getTransform();
        g.translate(x, y);
        g.rotate(Math.toRadians(r));
        if(p.hand != null){
            CardRenderer.drawCard(g, show, -110, -70, p.hand.get(0).suit, p.hand.get(0).num, 100);
            CardRenderer.drawCard(g, show,  0, -70, p.hand.get(1).suit, p.hand.get(1).num, 100);
        }

        g.setColor(Color.WHITE);
        g.drawImage(ppf.avatar, -220, -50, 100, 100, null);
        Font font = new Font("Comic Sans MS", 0, 20);
        g.setFont(font);
        g.drawString(p.name, -220, 65);

        for(int i = 0; i < p.money; i++){
            drawChip(g, 0, 110 + ((i/7) * 50), -70 + ((i % 7) * 15), 50);
        }

        if(!p.playing) {
            g.setColor(Color.RED);
            Stroke s = g.getStroke();
            g.setStroke(new BasicStroke(5));
            g.drawLine(-150, -70,  150, 70);
            g.drawLine( 150, -70, -150, 70);
            g.setStroke(s);
        }

        g.setTransform(bt);
    }

    //draws a poker chip
    void drawChip(Graphics g, int type, int x, int y, int scale){
        //0 = 1 white
        //1 = 2 red
        //2 = 4 blue
        //3 = 8 yellow?
        final Color[] colors = 
            {new Color(255, 255, 255), new Color(255, 0, 0), new Color(0, 0, 125), new Color(200, 150, 0)};
        g.setColor(colors[type]);
        g.fillOval(x, y, scale, scale);
        g.drawImage(chip, x, y, scale, scale, null);
    }

    void drawPot(Graphics g, int amount, int width, int height){
        for(int i = 0; i < amount; i++){
            drawChip(g, 0, (int)(chipLoc[i][0] * width), (int)(chipLoc[i][1] * height), 50);
        }
    }
}
