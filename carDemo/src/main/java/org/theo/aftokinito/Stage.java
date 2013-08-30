    package org.theo.aftokinito;
    
    import java.awt.image.ImageObserver;
    import org.cove.ape.Vector;
    
    public interface Stage extends ImageObserver {
      public static final int SCREEN_WIDTH=660;
      public static final int SCREEN_HEIGHT=480;
      public static final int PLAY_HEIGHT = 400; 
      public static final int SPEED=10;

      public static final int PLATFORM_WIDTH=4000;
      public static final int PLATFORM_HEIGHT=4000;
      public Vector PLATFORM_POS = new Vector(0,0);
      
      //public SpriteCache getSpriteCache();
      //public SoundCache getSoundCache();
      //public void addActor(Actor a);
      //public Player getPlayer();
      //public void gameOver();
  
      //public double offSetX(double x) {}
    }