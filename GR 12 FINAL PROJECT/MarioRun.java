//Nafiz Hasan and Ashad Ahmed

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.MouseInfo;
import javax.swing.Timer;

public class MarioRun extends JFrame implements ActionListener
{
	Timer myTimer;   
	GamePanel game;
		
    public MarioRun()
    {
		super("Mario Run");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900,650);

		myTimer = new Timer(15, this);	 // trigger every 10 ms

	
		game = new GamePanel(this);
		add(game);

		setResizable(false);
		setVisible(true);
    }
	
	public void start()
	{
		myTimer.start();
	}

	public void actionPerformed(ActionEvent evt)
	{
		game.update();
		game.repaint();
	}

    public static void main(String[] arguments)
    {
		MarioRun frame = new MarioRun();
    }
}

class GamePanel extends JPanel implements KeyListener
{
	private boolean []keys;
	private MarioRun mainFrame;
	private String screen = "menu";
	
	private Image back, tmp, currPic, coinIconPic, lifeIconPic, coinPic, mushroomPic;
	
	private ArrayList<Image>marioLeftWalkPics = new ArrayList<Image>();
	private ArrayList<Image>marioRightWalkPics = new ArrayList<Image>();
	
	private int backX = 0;
	
	private int ground = 555-70;
	
	private int collectedCoins = 0;
	
	private boolean shiftLeft = false;
	private boolean shiftRight = false;
	
	private ArrayList<platform>platforms = new ArrayList<platform>();
	private ArrayList<coin>coins = new ArrayList<coin>();
	private ArrayList<mushroom>mushrooms = new ArrayList<mushroom>();
	
	private ArrayList<goomba> goombas = new ArrayList<goomba>();
	
	private int frames;
	
	private boolean jCooldown = false;
	private int jCooldownCount = 0;
	private boolean jumpWait = false;
	
	private boolean inv = false;
	private int invincibleCount = 0;
	private boolean invincible = false;
	
	private int lives = 5;

	private boolean right, left;
	
	private Font marioFont;
		
	player mario = new player(430,ground,0,false,false,70,45);
	
	public GamePanel(MarioRun m)
	{
		keys = new boolean[KeyEvent.KEY_LAST+1];
		back = new ImageIcon("MarioBackground.png").getImage().getScaledInstance(10000,650,Image.SCALE_SMOOTH);
		coinPic = new ImageIcon("Mariopics/coin.gif").getImage().getScaledInstance(15,25,Image.SCALE_SMOOTH);
		mushroomPic = new ImageIcon("Mariopics/mushroom.png").getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH);
		coinIconPic = new ImageIcon("Mariopics/coinIcon.png").getImage().getScaledInstance(20,30,Image.SCALE_SMOOTH);
		lifeIconPic = new ImageIcon("Mariopics/lifeMushroom.png").getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH);
        for(int i=0; i<8; i++)
        {
        	tmp = new ImageIcon("MarioPics/mariowalk" +Integer.toString(i)+".png").getImage().getScaledInstance(mario.getWidth()+5,mario.getHeight()+5,Image.SCALE_SMOOTH);
    		if(i<=3)
    		{
    			for(int z=0; z<5; z++){
    				marioRightWalkPics.add(tmp);
    			}
    			
    		}
        	else
        	{
        		for(int z=0; z<5; z++){
    				marioLeftWalkPics.add(tmp);
    			}
        		
        	}
        }
        try
		{
			marioFont = Font.createFont(Font.TRUETYPE_FONT, new File("SuperMario.ttf")).deriveFont(48f);
		}
		catch(IOException ex)
		{
			System.out.println(ex);
			System.exit(1);
		}
		catch(FontFormatException ex)
		{
			System.out.println(ex);
			System.exit(1);
		}
        currPic = marioRightWalkPics.get(0);
		mainFrame = m;
		setSize(800,600);
        addKeyListener(this);
        loadPlatforms();
        loadCoins();
    	loadGoombas();
    	loadMushrooms();
	}
	
    public void addNotify()
    {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }
    
    public void update()
    {
    	if(screen == "menu")
    	{
    		menuStart();
    	}
    	if(screen == "level1")
    	{
	    	move();
	    	jump();
	    	jumpCooldown();
	    	invincibilityCooldown();
	    	checkDeath();
	    	checkCollectedCoins();
	    	checkPlatformCollide();
	    	checkCoinCollide();
	    	checkGoombaCollide();
	    	checkMushroomCollide();
	    	moveGoombas();
	    	moveMushrooms();
	    	System.out.println(mario.getJump());
    	}
    }
    
	public void menuStart()
	{
		if(keys[KeyEvent.VK_SPACE] )
		{
			screen = "level1";
		}
	}
	
    public void checkDeath()
    {
    	if(lives == 0)
    	{
    		System.exit(0);
    	}
    }
    
    public void checkCollectedCoins()
    {
    	int count = 0;
    	for(coin c : coins)
    	{
    		if(c.getCollected()==true)
    		{
    			count += c.getPoints();
    		}
    	}
    	collectedCoins = count;
    }
    
    public void invincibilityCooldown()
    {
    	if(inv == true)
    	{
    		invincible = true;
    		invincibleCount += 1;
    		if(invincibleCount == 60)
    		{
	    		invincible = false;
	    		invincibleCount = 0;
	    		inv = false;
    		}
    	}
    }
    
    public void moveBackLeft()
    {
		backX -= 4;
		for(platform p : platforms)
		{
			p.addX(-4);
		}
		for(coin c : coins)
		{
			c.addX(-4);
		}
		for(goomba g : goombas)
		{
			g.addX(-4);
			g.addMin(-4);
			g.addMax(-4);
		}
		for(mushroom m : mushrooms)
		{
			m.addX(-4);
			m.addMin(-4);
			m.addMax(-4);
		}
    }
    public void moveBackRight()
    {
		backX += 4;
		for(platform p : platforms)
		{
			p.addX(+4);
		}
		for(coin c : coins)
		{
			c.addX(+4);
		}
		for(goomba g : goombas)
		{
			g.addX(+4);
			g.addMin(+4);
			g.addMax(+4);
		}
		for(mushroom m : mushrooms)
		{
			m.addX(+4);
			m.addMin(+4);
			m.addMax(+4);
		}
    }
	
	public void move()
	{
		if(keys[KeyEvent.VK_RIGHT] )
		{
			right = true;
			left = false;
			if(shiftRight == false)
			{
				mario.addX(-10);
			}
			shiftRight = true;
			shiftLeft = false;
			moveBackLeft();
		}
		if(keys[KeyEvent.VK_LEFT] && backX <= 0)
		{
			right = false;
			left = true;
			if(shiftLeft == false)
			{
				mario.addX(10);
			}
			shiftRight = false;
			shiftLeft = true;
			moveBackRight();
		}
		
		Point mouse = MouseInfo.getPointerInfo().getLocation();
		Point offset = getLocationOnScreen();
		//System.out.println(mario.getY());
	}
	
	public void jump()
	{
		if(keys[KeyEvent.VK_UP] && mario.getJump()==false && jumpWait == false)
		{
			mario.setJump(true);
			mario.setVY(-20);
		}
		if(mario.getJump() == true)
		{
			mario.addY(mario.getVY());
					
			if(mario.getY() >= ground)
			{
				mario.setY(ground);
				mario.setVY(0);
				mario.setJump(false);
				jCooldown = true;
			}
			mario.addVY(1);
		}
	}
	
    public void jumpCooldown()
    {
    	if(jCooldown == true)
    	{
    		jumpWait = true;
    		jCooldownCount += 1;
    		if(jCooldownCount == 10)
    		{
	    		jumpWait = false;
	    		jCooldownCount = 0;
	    		jCooldown = false;
    		}
    	}
    }
  
    public void moveGoombas()
    {
		for(goomba g : goombas)
		{
			if(!g.getKilled())
			{
				if(g.getLeft() == true)
				{
					if(g.getX() >= g.getMin())
					{
						g.addX(-1);
					}
					else
					{
						g.setLeft(false);
						g.setRight(true);
					}
				}
				if(g.getRight() == true)
				{
					if(g.getX() <= g.getMax())
					{
						g.addX(+1);
					}
					else
					{
						g.setLeft(true);
						g.setRight(false);
					}
				}
			}
		}
    }
    
    public void moveMushrooms()
    {
		for(mushroom m : mushrooms)
		{
			if(!m.getCollected())
			{
				if(m.getLeft() == true)
				{
					if(m.getX() >= m.getMin())
					{
						m.addX(-2);
					}
					else
					{
						m.setLeft(false);
						m.setRight(true);
					}
				}
				if(m.getRight() == true)
				{
					if(m.getX() <= m.getMax())
					{
						m.addX(+2);
					}
					else
					{
						m.setLeft(true);
						m.setRight(false);
					}
				}
			}
		}
    }
    
    public void loadPlatforms()
    {
		int plx;
		int ply;
		int size;
    	boolean sameSpot = false;
    	Random rand = new Random();
    	for(int i=0;i<70;i++)
    	{
    		plx = rand.nextInt(9000) + 500;
    		ply = rand.nextInt(450) + 10;
    		size = rand.nextInt(220) + 70;
    		for(platform p : platforms)
    		{
    			Rectangle newRect = new Rectangle(plx,ply,size,10);
    			Rectangle oldRect = new Rectangle(p.getX()-10,p.getY()-40,p.getSizeX()+20,p.getSizeY()+80);
    			if(newRect.intersects(oldRect))
    			{
    				sameSpot = true;
    			}
    		}
    		if(sameSpot == false)
    		{
    			platforms.add(new platform(plx,ply,size,10,false));
    		}
    		else
    		{
    			sameSpot = false;
    		}
    	}
    }
    
    public void loadCoins()
    {
    	int r;
    	int x;
    	int rground;
    	boolean sameSpot = false;
    	Random rand = new Random();
    	
    	//coins on platforms
		for(platform p : platforms)
		{
			r = rand.nextInt(4);
			if(r == 1) // 1 in 4 chance
			{
				x = rand.nextInt(p.getSizeX() - 15);
				coins.add(new coin(p.getX() + x,p.getY() - 30,15,25,1,false));
			}
		}
		
		//coins on ground
		rground = rand.nextInt(10) + 5;
		for(int i=0;i<rground;i++)
		{
			x = rand.nextInt(9000) + 500;
    		for(coin c : coins)
    		{
    			Rectangle newRect = new Rectangle(x,555-30,15,25);
    			Rectangle oldRect = new Rectangle(c.getX(),c.getY(),c.getSizeX(),c.getSizeY());
    			if(newRect.intersects(oldRect))
    			{
					sameSpot = true;
    			}
    		}
      		if(sameSpot == false)
    		{
    			coins.add(new coin(x,555-30,15,25,1,false));
    		}
    		else
    		{
    			sameSpot = false;
    		}
		}
    }
    
    public void loadGoombas()
    {
    	int r;
    	int x;
    	int rground;
    	boolean sameSpot = false;
    	Random rand = new Random();
    	
		for(platform p : platforms)
		{
			if(p.getSomethingOn() == false)
			{
				r = rand.nextInt(6);
				if(r == 1)
				{
					x = rand.nextInt(p.getSizeX() - 40);
					goombas.add(new goomba(p.getX() + x,p.getY() - 40,40,40,p.getX(),p.getX()+p.getSizeX()-40,true,false,false));
					p.setSomethingOn(true);
				}	
			}
		}
		
		rground = rand.nextInt(5)+2;
		for(int i=0;i<rground;i++)
		{
			x = rand.nextInt(9000) + 500;
    		for(goomba g : goombas)
    		{
    			Rectangle newRect = new Rectangle(x,555-25,10,20);
    			Rectangle oldRect = new Rectangle(g.getX(),g.getY(),g.getSizeX()+20,g.getSizeY());
    			if(newRect.intersects(oldRect))
    			{
					sameSpot = true;
    			}
    		}
      		if(sameSpot == false)
    		{
				goombas.add(new goomba(x,555-40,40,40,x,x+500,true,false,false));
    		}
    		else
    		{
    			sameSpot = false;
    		}
		}
    }
    
    public void loadMushrooms()
    {
    	int r;
    	int x;
    	int rground;
    	boolean sameSpot = false;
    	Random rand = new Random();
    	
		for(platform p : platforms)
		{
			if(p.getSomethingOn() == false)
			{
				r = rand.nextInt(12);
				if(r == 1)
				{
					x = rand.nextInt(p.getSizeX() - 30);
					mushrooms.add(new mushroom(p.getX() + x,p.getY() - 30,30,30,p.getX(),p.getX()+p.getSizeX()-30,false,false,true));
					p.setSomethingOn(true);
				}	
			}
		}
    }
	
    public void checkPlatformCollide()
    {
    	boolean onPlatform = false;
    	for(platform p : platforms)
    	{
			Rectangle m = new Rectangle(mario.getX()-5,mario.getY()+40,mario.getWidth()+10,mario.getHeight()-40); //player has 10 pixel clearance for x position
			Rectangle plat = new Rectangle(p.getX(),p.getY(),p.getSizeX(),p.getSizeY());
			if(mario.getVY() >= 0 && mario.getJump() == true)
			{
	    		if(m.intersects(plat))
	    		{
	    			onPlatform = true;
					mario.setY(p.getY()-70);
					mario.setVY(0);
					mario.setJump(false);
					jCooldown = true;
	    		}
			}	
    	}
    	if(onPlatform == false && mario.getY() != ground)
    	{
    		mario.setJump(true);
    	}
    }
    
    public void checkCoinCollide()
    {
    	for(coin c : coins)
    	{
			Rectangle m = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
			Rectangle coinRect = new Rectangle(c.getX(),c.getY(),c.getSizeX(),c.getSizeY());
    		if(m.intersects(coinRect))
    		{
    			c.setCollected(true);
    		}
    	}
    }
    
    public void checkGoombaCollide()
    {
    	for(goomba g : goombas)
    	{
			Rectangle m = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
			Rectangle goombaRect = new Rectangle(g.getX(),g.getY(),g.getSizeX(),g.getSizeY());
    		if(m.intersects(goombaRect))
    		{
    			if(g.getKilled()==false)
    			{
    				if(mario.getVY() >= 2)
    				{
    					g.setKilled(true);
    					mario.setVY(-10);
    				}
    				else
    				{
    					if(invincible == false)
    					{
	    					lives -= 1;
							inv = true;
    					}
    				}
    			}
    		}
    	}
    }
    
    public void checkMushroomCollide()
    {
    	for(mushroom mu : mushrooms)
    	{
			Rectangle m = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
			Rectangle goombaRect = new Rectangle(mu.getX(),mu.getY(),mu.getSizeX(),mu.getSizeY());
    		if(m.intersects(goombaRect))
    		{
    			if(mu.getCollected()==false)
    			{
    				lives += 1;
    				mu.setCollected(true);
    			}
    		}
    	}
    }
	
    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e)
    {
        keys[e.getKeyCode()] = true;
    }
    
    public void keyReleased(KeyEvent e)
    {
    	right = false;
    	left = false;
        keys[e.getKeyCode()] = false;
    }

    public void paintComponent(Graphics g)
    { 	
    	if(screen == "menu")
    	{
    		g.setColor(Color.red);
	    	g.fillRect(0,0,getWidth(),getHeight()); //background
    	}
    	if(screen == "level1")
    	{
	    	Rectangle marioRect = new Rectangle(mario.getX(), mario.getY(), mario.getWidth(),mario.getHeight());
	    	g.drawImage(back,backX,0,null);
	    	g.drawRect(marioRect.x, marioRect.y, marioRect.width, marioRect.height);
			for(platform p : platforms)
			{
				Color platBottomColor = new Color(213,132,22);
				g.setColor(platBottomColor);  
				g.fillRect(p.getX(),p.getY()+10,p.getSizeX(),545 - p.getY());
			}
			for(platform p : platforms)
			{
				Color platTopColor = new Color(67,144,0);
				g.setColor(platTopColor);  
				g.fillRect(p.getX(),p.getY(),p.getSizeX(),p.getSizeY());
			}
			for(coin c : coins)
			{
				if(c.getCollected() == false)
				{
					g.drawImage(coinPic, c.getX(), c.getY(), null);
				}
			}
			for(goomba gb : goombas)
			{
				if(gb.getKilled() == false)
				{
					if(gb.getRight())
					{
						g.drawImage(gb.getRightImage(),gb.getX(),gb.getY(),null);
					}
					if(gb.getLeft())
					{
						g.drawImage(gb.getLeftImage(),gb.getX(),gb.getY(),null);
					}
				}
				
				gb.addFrames(1);
				if(gb.getFrames()==30)
				{
					gb.addFrames(-gb.getFrames()); // reset frames
				}
				
				if(gb.getKilled())
				{
					if(gb.getLeft())
					{ 
						if(gb.getKillTimer()<=10) // dead goomba appears for a bit
						{
							g.drawImage(gb.getDeadImage(0),gb.getX(),gb.getY()+gb.getSizeY()/2,null);
							gb.setKillTimer(gb.getKillTimer()+1);
						}
	
					}
					if(gb.getRight())
					{ 
						if(gb.getKillTimer()<=10)
						{
							g.drawImage(gb.getDeadImage(1),gb.getX(),gb.getY()+gb.getSizeY()/2,null);
							gb.setKillTimer(gb.getKillTimer()+1);
						}
	
					}
				}	
			}
	
			for(mushroom m : mushrooms)
			{
				if(m.getCollected() == false)
				{
					g.drawImage(mushroomPic, m.getX(), m.getY(), null);
				}
			}
			
			if(!right && !left)
			{
	        	g.drawImage(currPic, mario.getX(), mario.getY(), null);
	        }
			if(right)
			{
	        	currPic = marioRightWalkPics.get(0);
	            g.drawImage(marioRightWalkPics.get(frames),mario.getX(),mario.getY(),null);
	        }
	        if(left)
	        {
	        	currPic = marioLeftWalkPics.get(0);
	            g.drawImage(marioLeftWalkPics.get(frames),mario.getX(),mario.getY(),null);
	        }
			frames++;
			if(frames==20)
			{
				frames=0;	
			}
			
			g.setColor(Color.white);
			g.drawImage(lifeIconPic, 5, 7, null); 
			g.drawImage(coinIconPic, 10, 40, null);
			g.setFont(marioFont);
			g.drawString("x"+Integer.toString(lives), 37, 35);
			g.drawString(Integer.toString(collectedCoins), 33, 69);
    	}
	}
}	

class player
{
	private int X;
	private int Y;
	private int VY;
	private boolean jump;
	private int height;
	private int width;
	
	public player(int px, int py, int pvy, boolean j, boolean f, int h, int w)
	{
		X = px;
		Y = py;
		VY = pvy;
		jump = j;
		height = h;
		width = w;
	}
	
	public int getHeight()
	{
	    return height;
	}
	
	public int getWidth()
	{
	    return width;
	}
	
	public boolean getJump()
	{
	    return jump;
	}
	public void setJump(boolean b)
	{
		jump = b;
	}
	
	public int getX()
	{
	    return X;
	}
	public void addX(int num)
	{
		X += num;
	}
	
	public int getY()
	{
	    return Y;
	}
	public void addY(int num)
	{
		Y += num;
	}
	public void setY(int num)
	{
		Y = num;
	}
	
	public int getVY()
	{
	    return VY;
	}
	public void addVY(int num)
	{
		VY += num;
	}
	public void setVY(int num)
	{
		VY = num;
	}
}

class platform
{
	private int X;
	private int Y;
	private int sizeX;
	private int sizeY;
	private boolean somethingOn;
	
	public platform(int plx, int ply, int sx, int sy, boolean on)
	{
		X = plx;
		Y = ply;
		sizeX = sx;
		sizeY = sy;
		somethingOn = on;
	}
	
	public int getX()
	{
	    return X;
	}
	public void addX(int num)
	{
		X += num;
	}
	
	public int getY()
	{
	    return Y;
	}
	
	public int getSizeX()
	{
	    return sizeX;
	}
	
	public int getSizeY()
	{
	    return sizeY;
	}
	
	public boolean getSomethingOn()
	{
	    return somethingOn;
	}
	
	public void setSomethingOn(boolean b)
	{
	    somethingOn = b;
	}
}

class coin
{
	private int X;
	private int Y;
	private int sizeX;
	private int sizeY;
	private int points;
	private boolean collected;
	
	public coin(int cx, int cy, int sx, int sy, int p, boolean c)
	{
		X = cx;
		Y = cy;
		sizeX = sx;
		sizeY = sy;
		points = p;
		collected = c;
	}
	
	public int getX()
	{
	    return X;
	}
	
	public void addX(int num)
	{
		X += num;
	}
	
	public int getY()
	{
	    return Y;
	}
	
	public int getSizeX()
	{
	    return sizeX;
	}
	
	public int getSizeY()
	{
	    return sizeY;
	}
	
	public int getPoints()
	{
	    return points;
	}
	
	public boolean getCollected()
	{
	    return collected;
	}
	public void setCollected(boolean b)
	{
	    collected = b;
	}
}

class goomba
{
	private int X;
	private int Y;
	private int vx;
	private int sizeX;
	private int sizeY;
	private int minMove;
	private int maxMove;
	private int frames;
	private int killTimer;
	private boolean left;
	private boolean right;
	private boolean killed;
	private ArrayList<Image> goombaLeftPics = new ArrayList<Image>();
	private ArrayList<Image> goombaRightPics = new ArrayList<Image>();
	private ArrayList<Image> goombaDeadPics = new ArrayList<Image>();
	
	public goomba(int gx, int gy, int sx, int sy, int mi, int ma, boolean l, boolean r, boolean k)
	{
		X = gx;
		Y = gy;
		sizeX = sx;
		sizeY = sy;
		minMove = mi;
		maxMove = ma;
	 	left = l;
	 	right = r;
	 	killed = k;
	 	frames=0;
	 	for(int i=0; i<12; i++)
        {
        	Image tmp = new ImageIcon("MarioPics/goombawalk" +Integer.toString(i)+".png").getImage().getScaledInstance(sizeX, sizeY,Image.SCALE_SMOOTH);
        	for(int x=0;x<5;x++)
        	{
        		if(i<6)
        		{
        			goombaLeftPics.add(tmp);
        		}
        		else
        		{
        			goombaRightPics.add(tmp);
        		}
        	}
    		
        }
        for(int i=0; i<2; i++)
        {
        	goombaDeadPics.add(new ImageIcon("MarioPics/deadgoomba" +Integer.toString(i)+".png").getImage().getScaledInstance(sizeX, sizeY/2,Image.SCALE_SMOOTH));
        }
	}
	
	public int getX()
	{
	    return X;
	}
	public void addX(int num)
	{
		X += num;
	}
	
	public int getY()
	{
	    return Y;
	}
	
	public int getSizeX()
	{
	    return sizeX;
	}
	
	public int getSizeY()
	{
	    return sizeY;
	}
	
	public int getMin()
	{
	    return minMove;
	}
	
	public int getMax()
	{
	    return maxMove;
	}
	
	public int getKillTimer()
	{
	    return killTimer;
	}
	
	public Image getRightImage()
	{
		return goombaRightPics.get(frames);
	}
	
	public Image getLeftImage()
	{
		return goombaLeftPics.get(frames);
	}
	
	public Image getDeadImage(int n)
	{
		return goombaDeadPics.get(n);
	}
	
	public void addMin(int num)
	{
		minMove += num;
	}
	public void addMax(int num)
	{
		maxMove += num;
	}
	
	public boolean getLeft()
	{
	    return left;
	}
	
	public boolean getRight()
	{
	    return right;
	}
	
	public int getFrames()
	{
		return frames;
	}
	public void setLeft(boolean b)
	{
	    left = b;
	}
	
	public void setRight(boolean b)
	{
	    right = b;
	}
	
	public void setKillTimer(int n)
	{
	    killTimer = n;
	}
	
	public void addFrames(int n)
	{
		frames += n;	
	}
	
	public boolean getKilled()
	{
	    return killed;
	}
	
	public void setKilled(boolean b)
	{
	    killed = b;
	}
}

class mushroom
{
	private int X;
	private int Y;
	private int vx;
	private int sizeX;
	private int sizeY;
	private int minMove;
	private int maxMove;
	private boolean collected;
	private boolean left;
	private boolean right;
	
	public mushroom(int gx, int gy, int sx, int sy, int mi, int ma, boolean c, boolean l, boolean r)
	{
		X = gx;
		Y = gy;
		sizeX = sx;
		sizeY = sy;
		minMove = mi;
		maxMove = ma;
		collected = c;
		left = l;
		right = r;
	}
	
	public int getX()
	{
	    return X;
	}
	public void addX(int num)
	{
		X += num;
	}
	
	public int getY()
	{
	    return Y;
	}
	
	public int getSizeX()
	{
	    return sizeX;
	}
	
	public int getSizeY()
	{
	    return sizeY;
	}
	
	public int getMin()
	{
	    return minMove;
	}
	
	public int getMax()
	{
	    return maxMove;
	}
	
	public void addMin(int num)
	{
		minMove += num;
	}
	public void addMax(int num)
	{
		maxMove += num;
	}

	public boolean getCollected()
	{
	    return collected;
	}
	public void setCollected(boolean b)
	{
	    collected = b;
	}

	public boolean getLeft()
	{
	    return left;
	}
	
	public boolean getRight()
	{
	    return right;
	}
	
	public void setLeft(boolean b)
	{
	    left = b;
	}
	
	public void setRight(boolean b)
	{
	    right = b;
	}
}