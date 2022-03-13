
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class AnaEkran extends JFrame {
    

    public AnaEkran(String title) throws HeadlessException {
        super(title);
    }

    public static void main(String[] args) {
        AnaEkran anaEkran = new AnaEkran("S.P.A.C.E.R.");
        
        
        anaEkran.setBounds(500,200,800,600);
        anaEkran.setResizable(false);
        anaEkran.setFocusable(false);
        anaEkran.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        OyunPanel oyunPanel = new OyunPanel();
        oyunPanel.requestFocus();//klavye işlemlerini frame değil panel alması için.
        oyunPanel.addKeyListener(oyunPanel); // klavye işlemleri için
        oyunPanel.setFocusTraversalKeysEnabled(false); // klavye işlemlerini yapmak için
        oyunPanel.setFocusable(true); // odağı frame e değil panele aktarmak için
        
        anaEkran.add(oyunPanel);
        
        anaEkran.setVisible(true);  
    }
    
    
    
}
