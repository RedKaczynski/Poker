package Networking.PlayerProfiles;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import javax.imageio.ImageIO;

import utils.ByteUtils;

/** Player profile file:
 *  x50 x50 x46 x00 | x00 x00 x00 x00 <- Header and spare bytes
 *  id, 4 bytes     | Name ->>>>> x00
 *  number of wins  | pfp bytes
 *  EOF
 */

public class PlayerProfile {
    //"PPF01000"
    static byte[] headerstr = {0x50, 0x50, 0x46, 0x30, 0x31, 0x30, 0x30, 0x30};

    public BufferedImage avatar;
    public String username;
    public int lifetimeChips;
    int id;
    File saveFile;

    //Default constructor.
    public PlayerProfile(BufferedImage avi, String name, int id, int lifeChips){
        avatar = avi;
        lifetimeChips = lifeChips;
        username = name;
        this.id = id;
    }

    //alternate constructor, probably useful later
    public PlayerProfile(final String location){
        load(location);
    }

    //copies a profile to this object
    private void copyProfileHere(PlayerProfile ppf){
        avatar = ppf.avatar;
        lifetimeChips = ppf.lifetimeChips;
        username = ppf.username;
        this.id = ppf.id;
    }
    
    //loads from a file
    void load(final String location){
        try {
            saveFile = new File(location);
            copyProfileHere(loadFromStream(new FileInputStream(saveFile)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //saves profile
    public void save(){
        try{
            if(saveFile == null) saveNewToFile(this);
            else saveOldToFile(this, saveFile);
        }
        catch(IOException h){h.printStackTrace();}
    }

    //creates a profile if none can be found
    public static PlayerProfile newDefault(){
        BufferedImage buff;
        try {
            buff = ImageIO.read(new File("res/profiles/avatars/default.png"));
        } catch (IOException e) {
            buff = new BufferedImage(256, 256, 2);
            e.printStackTrace();
        }
        PlayerProfile ppf = new PlayerProfile(buff, "Guest", 0, 0);
        return ppf;
    }

    //creates a profile if none can be found
    public static PlayerProfile AIprofile(){
        BufferedImage buff;
        try {
            buff = ImageIO.read(new File("res/profiles/avatars/1_AI.png"));
        } catch (IOException e) {
            buff = new BufferedImage(256, 256, 2);
            e.printStackTrace();
        }
        PlayerProfile ppf = new PlayerProfile(buff, "AI", 0, 0);
        return ppf;
    }

    //loading from input stream
    static PlayerProfile loadFromStream(InputStream in) throws IOException{
        String name = "";
        int id;
        int chips;

        byte[] header = in.readNBytes(8);
        for(int i = 0; i < 8; i++){
            if(header[i] != headerstr[i]) {
                in.close();
                throw new IOException("Not a PPF file, header is: " +
                    header[0] + " " + header[1] + " " + header[2] + " " + header[3] + " " +
                    header[4] + " " + header[5] + " " + header[6] + " " + header[7]    
                );
            }   
        }
        
        id = ByteUtils.bytesToInt(in.readNBytes(4));
        int i = 0;
        while((i = in.read()) != 0){
            name += (char)i;
        }
        chips = ByteUtils.bytesToInt(in.readNBytes(4));

        in.close();
        PlayerProfile pp = new PlayerProfile(ImageIO.read(new File("res/profiles/avatars/" + name + ".png")), name, id, chips);
        return pp;
    }
    
    //saves to a new file
    static void saveOldToFile(PlayerProfile pp, File old) throws IOException {
        old.delete();
        saveNewToFile(pp);
    }
    
    //saves to a new file
    static void saveNewToFile(PlayerProfile pp) throws IOException {
        File output = new File("res/profiles/" + pp.username + ".ppf");
        FileOutputStream os = new FileOutputStream(output);

        os.write(pp.getAllBytes(false));
        ImageIO.write(pp.avatar, "png", new File("res/profiles/avatars/" + pp.username + ".png"));
        os.close();
    }

    //gets all of the bytes of the profile with a boolean toggle to output the image
    public byte[] getAllBytes(boolean store_avi){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try{
            bos.write(headerstr);
            bos.write(ByteUtils.intToBytes(id));
            bos.write(username.getBytes(Charset.forName("ASCII")));
            
            bos.write(0x00);
            bos.write(ByteUtils.intToBytes(lifetimeChips));
            ImageIO.write(avatar, "PNG", bos);
        } catch(IOException e){
            e.printStackTrace();
        }
        
        return bos.toByteArray();
    }
}
