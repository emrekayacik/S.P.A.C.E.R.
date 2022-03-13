
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
// GELİŞTİRME - AYNI ANDA EN FAZLA 3 MERMİ - MERMI RESMI - OYUN BITTIKTEN SONRA TEKRAR BASLAR - ARTIK DÜŞMAN DA ATEŞ EDİYOR - ARTIK DÜŞMAN MOLOTOF ATIYOR
// MOLOTOFLA MERMI ÇARPIŞIRSA YOK OLUYORLAR - YOK EDILEN MERMI SAYISI YAZILIYOR - SESLER EKLENDİ - KOLAY VE ZOR OYUN MODLARI GELDİ - PUAN SISTEMI GELDI - ARTIK HIGHSCORE TUTULUYOR

public class OyunPanel extends JPanel implements KeyListener, ActionListener{
    
    private int gecenSure = 0;
    private int harcananAtes = 0;
    private BufferedImage image;
    private ArrayList<Ates> ateslerList = new ArrayList<Ates>();
    private ArrayList<Ates> dusmanatesArrayList = new ArrayList<Ates>();
    private int dusmanHarcananAtes = 0;
    private int yokedilenMermi = 0;
            
    private int atesDirY = 1;
    
    private int topX = 0;
    private int topDirX = 2;
    
    private int silahX = 0;
    private int dirSilahX = 20;
    
    private int dusmanAtesDirY = 1;
    
    private BufferedImage bulletImage;
    private BufferedImage molotovImage;
    Object[] options = {"Easy","Hard"};
    private int response = 0;
    
    private int points ;
    
    private String sonHighScore;
    private int highScores;
    Timer timer = new Timer(5, this);
    Timer timer2 = new Timer(500,new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            if(response == 1){
                Ates dusmanAtes = new Ates(topX+5, 5);
                dusmanatesArrayList.add(dusmanAtes);
                dusmanHarcananAtes++;
            timer2.start();
            }
        }
    });
    
    

   
    public int kontroL(){
        response = JOptionPane.showOptionDialog(null,"Select a difficulty","Difficulty selection",JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE,null,options,options[0]);
        return response;
    }
    
    public OyunPanel() {
        
        try {
            kontroL();
            bulletImage =ImageIO.read(new FileImageInputStream(new File("images/bullet.png")));
            molotovImage = ImageIO.read(new FileImageInputStream(new File("images/molotov.png")));
            image = ImageIO.read(new FileImageInputStream(new File("images/silah.png")));            
            timer2.start();
            setBackground(Color.BLACK);
            
        } catch (IOException ex) {
            Logger.getLogger(OyunPanel.class.getName()).log(Level.SEVERE, null, ex);
        } 
        timer.start();
    }
    public boolean kontrol(){
        for(Ates ates : ateslerList){
            if(new Rectangle(ates.getX(),ates.getY(),10,20).intersects(new Rectangle(topX,0,20,20))){
                return true;
            }
        }
        return false;
    }
    public boolean dusmanKontrol(){
            for(Ates ates : dusmanatesArrayList){
            if(new Rectangle(ates.getX(),ates.getY(),10,20).intersects(new Rectangle(silahX,505,image.getWidth()/10,image.getHeight()/10))){
                return true;
            }
        }
            return false;
    }
    public boolean carpismaKontrol(){
        
        for(Ates ates : ateslerList){
            for(Ates ates1 : dusmanatesArrayList){
                if(new Rectangle(ates.getX(),ates.getY(),10,20).intersects(new Rectangle(ates1.getX(),ates1.getY(),20,40))){
                    ateslerList.remove(ates);
                    dusmanatesArrayList.remove(ates1);
                    yokedilenMermi++;
                    return true;
                }
                
            }
        }
        return false;      
    }

    @Override
    public void paint(Graphics g) {
        gecenSure +=5;
        try{
            super.paint(g);
        g.setColor(Color.red);
        
        g.fillOval(topX, 0, 20, 20);
        
        g.drawImage(image, silahX, 505, image.getWidth()/10, image.getHeight()/10,this);
        
        for(Ates ates : ateslerList){
            if(ates.getY() < 0){
                ateslerList.remove(ates);
            }
        }
        for(Ates ates : dusmanatesArrayList){
            if(ates.getY() > 780){
                dusmanatesArrayList.remove(ates);
            }
        }
        g.setColor(Color.BLUE);
        for(Ates ates : ateslerList){
            g.drawImage(bulletImage, ates.getX(), ates.getY(),bulletImage.getWidth()/30,bulletImage.getHeight()/15 , this);
        }
        
        for(Ates ates : dusmanatesArrayList){
            g.drawImage(molotovImage, ates.getX(), ates.getY(), molotovImage.getWidth()/20,molotovImage.getHeight()/10,this);
        }
        if(kontrol()){
            timer.stop();
            AudioInputStream winAudio = AudioSystem.getAudioInputStream(new File("sounds/win.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(winAudio);
            clip.start();
            points = yokedilenMermi*10 -(harcananAtes/3) - gecenSure/2000 +200 ;
            if(points < 0){
                points = 0;
            }
            else{
                
            }
            
            String s = "";
            try(Scanner scanner = new Scanner(new BufferedReader(new FileReader("jar/bin/txt/saves/save.txt")))){
                
                while(scanner.hasNextLine()){
                    s+=scanner.nextLine();
                }
                if(points > Integer.parseInt(s)){
                    try(BufferedWriter writer = new BufferedWriter(new FileWriter("jar/bin/txt/saves/save.txt"))){
                        writer.write(String.valueOf(points));
                        highScores = points;
                    }
                    catch(IOException ex){
                System.out.println("io");
            }
            catch(Exception ex){
                System.out.println("exception");
            }
                    
                }
                else{
                    highScores = Integer.valueOf(s);
                }
                
            }
            catch(IOException ex){
                System.out.println("io");
            }
            catch(Exception ex){
                System.out.println("exception");
            }

            JOptionPane.showMessageDialog(this,"Victory!\n"
                    +"Time: "+ gecenSure /1000.0+" s\n"+
                            "Shots used: "+harcananAtes+
                    "\nMolotovs destroyed: " + yokedilenMermi+
                    "\nPoints: " + points +
                    "\nHigh Score: " + highScores );
            
            
            response = JOptionPane.showOptionDialog(null,"Select a difficulty","Difficulty selection",JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE,null,options,options[0]);
            timer.start();
            
            for(int i = 0 ; i<ateslerList.size() ; i++){
                ateslerList.remove(i);
            }
            for(int i = 0 ; i<ateslerList.size() ; i++){
                ateslerList.remove(i);
            }
            for(int i = 0 ; i<ateslerList.size() ; i++){
                ateslerList.remove(i);
            }
            for(int i = 0 ; i<ateslerList.size() ; i++){
                ateslerList.remove(i);
            }
            for(int i = 0 ; i<ateslerList.size() ; i++){
                ateslerList.remove(i);
            }
            for(int i = 0 ; i<ateslerList.size() ; i++){
                ateslerList.remove(i);
            }
            for(int i = 0 ; i<dusmanatesArrayList.size() ; i++){
                dusmanatesArrayList.remove(i);
            }
            for(int i = 0 ; i<dusmanatesArrayList.size() ; i++){
                dusmanatesArrayList.remove(i);
            }
            for(int i = 0 ; i<dusmanatesArrayList.size() ; i++){
                dusmanatesArrayList.remove(i);
            }
            for(int i = 0 ; i<dusmanatesArrayList.size() ; i++){
                dusmanatesArrayList.remove(i);
            }
            for(int i = 0 ; i<dusmanatesArrayList.size() ; i++){
                dusmanatesArrayList.remove(i);
            }
            for(int i = 0 ; i<dusmanatesArrayList.size() ; i++){
                dusmanatesArrayList.remove(i);
            }
            
            gecenSure = 0;
            harcananAtes =0;
            dusmanHarcananAtes = 0;
            yokedilenMermi = 0;
            
            repaint();
            
        }       
        
        if(dusmanKontrol()){
            timer.stop();
            AudioInputStream loseAudio = AudioSystem.getAudioInputStream(new File("sounds/lose.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(loseAudio);
            clip.start();
            points = yokedilenMermi*10 -(harcananAtes/3) - gecenSure/2000 - 100;
            if(points < 0){
                points = 0;
            }
            else{
                
            }
            String s = "";
            try(Scanner scanner = new Scanner(new BufferedReader(new FileReader("jar/bin/txt/saves/save.txt")))){
                
                while(scanner.hasNextLine()){
                    s+=scanner.nextLine();
                }
                if(points > Integer.parseInt(s)){
                    try(BufferedWriter writer = new BufferedWriter(new FileWriter("jar/bin/txt/saves/save.txt"))){
                        writer.write(String.valueOf(points));
                        highScores = points;
                    }
                    catch(IOException ex){
                System.out.println("io");
            }
            catch(Exception ex){
                System.out.println("exception");
            }
                    
                }
                else{
                    highScores = Integer.valueOf(s);
                }
                
            }
            catch(IOException ex){
                System.out.println("io");
            }
            catch(Exception ex){
                System.out.println("exception");
            }
            
            
            JOptionPane.showMessageDialog(this,"Defeat!\n"
                    +"Time: "+ gecenSure /1000.0+" s\n"+
                            "Molotovs used by the enemy: "+dusmanHarcananAtes+
                    "\nMolotovs destroyed: " + yokedilenMermi+
                    "\nPoints: " + points+
                    "\nHigh Score: " + highScores);
            response = JOptionPane.showOptionDialog(null,"Select a difficulty","Difficulty selection",JOptionPane.DEFAULT_OPTION,JOptionPane.PLAIN_MESSAGE,null,options,options[0]);
            timer.start();
            for(int i = 0 ; i<ateslerList.size() ; i++){
                ateslerList.remove(i);
            }
            for(int i = 0 ; i<ateslerList.size() ; i++){
                ateslerList.remove(i);
            }
            for(int i = 0 ; i<ateslerList.size() ; i++){
                ateslerList.remove(i);
            }
            for(int i = 0 ; i<ateslerList.size() ; i++){
                ateslerList.remove(i);
            }
            for(int i = 0 ; i<ateslerList.size() ; i++){
                ateslerList.remove(i);
            }
            for(int i = 0 ; i<ateslerList.size() ; i++){
                ateslerList.remove(i);
            }
            
            for(int i = 0 ; i<dusmanatesArrayList.size() ; i++){
                dusmanatesArrayList.remove(i);
            }
            for(int i = 0 ; i<dusmanatesArrayList.size() ; i++){
                dusmanatesArrayList.remove(i);
            }
            for(int i = 0 ; i<dusmanatesArrayList.size() ; i++){
                dusmanatesArrayList.remove(i);
            }
            for(int i = 0 ; i<dusmanatesArrayList.size() ; i++){
                dusmanatesArrayList.remove(i);
            }
            for(int i = 0 ; i<dusmanatesArrayList.size() ; i++){
                dusmanatesArrayList.remove(i);
            }
            for(int i = 0 ; i<dusmanatesArrayList.size() ; i++){
                dusmanatesArrayList.remove(i);
            }
            
            
            
            gecenSure = 0;
            harcananAtes =0;
            dusmanHarcananAtes = 0;
            yokedilenMermi = 0;
            
            repaint();
        }
        if(carpismaKontrol()){
            AudioInputStream poofAudio = AudioSystem.getAudioInputStream(new File("sounds/glass.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(poofAudio);
            clip.start();
        }
        
        
        }
        
        
        catch(Exception e){
            
        }
    }
    @Override
    public void repaint() {
        super.repaint();
    }
    @Override
    public void keyTyped(KeyEvent e) {  
    }
    @Override
    public void keyPressed(KeyEvent e) {
        int c = e.getKeyCode();// hangi tuşa bastgımızın kodunu al demek
        if(c == KeyEvent.VK_LEFT){
            if(silahX > 0){
                silahX -= dirSilahX;
            }
            else{
                silahX =0;
            }
        }
        else if(c == KeyEvent.VK_RIGHT){
            if(silahX < 740){
               silahX += dirSilahX; 
            }
            else{
                silahX = 740;
            }
        }
        else if(c == KeyEvent.VK_UP){
            if(ateslerList.size() <3){
                AudioInputStream fireAudio;
                try {
                    fireAudio = AudioSystem.getAudioInputStream(new File("sounds/revolver.wav"));
                    Clip clip = AudioSystem.getClip();
                    clip.open(fireAudio);
                    clip.start();
                } catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(OyunPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(OyunPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(OyunPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                Ates ates = new Ates(silahX+5, 490);
                ateslerList.add(ates);
                harcananAtes++;
            }
            if(dusmanatesArrayList.size() < 3){
                Ates dusmanAtes = new Ates(topX+5, 5);
                dusmanatesArrayList.add(dusmanAtes);
                dusmanHarcananAtes++;
            }
        } 
    }
    @Override
    public void keyReleased(KeyEvent e) {
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        for(Ates ates : ateslerList){
            ates.setY(ates.getY() - atesDirY);
        }
        for(Ates ates : dusmanatesArrayList){
            ates.setY(ates.getY()+dusmanAtesDirY);
        }
        
        
        topX += topDirX;
        if(topX >= 750){
            topDirX = -topDirX;
        }
        if(topX <= 0){
            topDirX = -topDirX;
        }
        timer.start();
        repaint();
    }
    
}
