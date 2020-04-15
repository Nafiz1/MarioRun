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

		myTimer = new Timer(10, this);	 // trigger every 10 ms


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
		game.userActions();
		game.update();
		game.repaint();
	}

    public static void main(String[] arguments)
    {
		MarioRun frame = new MarioRun();		
    }
}

class GamePanel extends JPanel implements KeyListener{
	private boolean []keys;
	private MarioRun mainFrame;
	
	private Image back;
	private Image tmp;
	
	private ArrayList<Image>marioLeftWalkPics = new ArrayList<Image>();
	private ArrayList<Image>marioRightWalkPics = new ArrayList<Image>();
	
	private int backX = 0;
	
	private int ground = 555-65;
	
	private int collectedCoins = 0;
	
	private boolean shiftLeft = false;
	private boolean shiftRight = false;
	
	private ArrayList<platform>platforms = new ArrayList<platform>();
	private ArrayList<coin>coins = new ArrayList<coin>();
	
	private boolean jCooldown = false;
	private int jCooldownCount = 0;
	private boolean jumpWait = false;
	
	player mario = new player(430,ground,0,false,false,70,45);
	
	public GamePanel(MarioRun m)
	{
		keys = new boolean[KeyEvent.KEY_LAST+1];
		back = new ImageIcon("MarioBackground.png").getImage().getScaledInstance(10000,650,Image.SCALE_SMOOTH);
        for(int i=0; i<5; i++)
        {
        	tmp = new ImageIcon("MarioPics/mariowalk" +Integer.toString(i)+".png").getImage().getScaledInstance(mario.getWidth(),mario.getHeight(),Image.SCALE_SMOOTH);
    		if(i<=2)
    		{
    			marioRightWalkPics.add(tmp);
    		}
        	else
        	{
        		marioLeftWalkPics.add(tmp);
        	}
        }
		mainFrame = m;
		setSize(800,600);
        addKeyListener(this);
        loadPlatforms();
        loadCoins();
	}
	
    public void addNotify()
    {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }
    
    public void userActions()
    {
    	move();
    	jump();
    }
    
    public void update()
    {
    	System.out.println(collectedCoins);
    	checkCollectedCoins();
    	checkPlatformCollide();
    	checkCoinCollide();
    	jumpCooldown();
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
    }
	
	public void move()
	{
		if(keys[KeyEvent.VK_RIGHT] )
		{
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
		//System.out.println("("+(mouse.x-offset.x)+", "+(mouse.y-offset.y)+")");
	}
	
	public void jump()
	{
		if(keys[KeyEvent.VK_UP] && mario.getJump()==false && jumpWait == false){
			mario.setJump(true);
			mario.setVY(-21);
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
    			platforms.add(new platform(plx,ply,size,10));
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
				x = rand.nextInt(p.getSizeX() - 10);
				coins.add(new coin(p.getX() + x,p.getY() - 25,10,20,1,false));
			}
		}
		
		//coins on ground
		rground = rand.nextInt(7) + 5;
		for(int i=0;i<rground;i++)
		{
			x = rand.nextInt(9000) + 500;
    		for(coin c : coins)
    		{
    			Rectangle newRect = new Rectangle(x,555-25,10,20);
    			Rectangle oldRect = new Rectangle(c.getX(),c.getY(),c.getSizeX(),c.getSizeY());
    			if(newRect.intersects(oldRect))
    			{
					sameSpot = true;
    			}
    		}
      		if(sameSpot == false)
    		{
    			coins.add(new coin(x,555-25,10,20,1,false));
    		}
    		else
    		{
    			sameSpot = false;
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
	
    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e)
    {
        keys[e.getKeyCode()] = true;
    }
    
    public void keyReleased(KeyEvent e)
    {
        keys[e.getKeyCode()] = false;
    }

    public void paintComponent(Graphics g)
    { 	
    	g.drawImage(back,backX,0,null);
		for(platform p : platforms)
		{
			Color platBottomColor = new Color(213,132,22);
			g.setColor(platBottomColor);  
			g.fillRect(p.getX(),p.getY()+10,p.getSizeX(),555-10 - p.getY());
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
				g.setColor(Color.yellow);  
				g.fillRect(c.getX(),c.getY(),c.getSizeX(),c.getSizeY());
			}
		}
		g.setColor(Color.red);  
		g.fillRect(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
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
	
	public platform(int plx, int ply, int sx, int sy)
	{
		X = plx;
		Y = ply;
		sizeX = sx;
		sizeY = sy;
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
