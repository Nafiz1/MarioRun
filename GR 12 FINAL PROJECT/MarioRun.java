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
	
	private Image back, tmp, currPic;
	
	private ArrayList<Image>marioLeftWalkPics = new ArrayList<Image>();
	private ArrayList<Image>marioRightWalkPics = new ArrayList<Image>();
	
	private int backX = 0;
	
	private int ground = 555-70;
	
	private int collectedCoins = 0;
	
	private boolean shiftLeft = false;
	private boolean shiftRight = false;
	
	private ArrayList<platform>platforms = new ArrayList<platform>();
	private ArrayList<coin>coins = new ArrayList<coin>();
	
	private ArrayList<goomba> goombas = new ArrayList<goomba>();
	
	private int plx;
	private int ply;
	private int size;
	private int frames;
	
	private boolean jCooldown = false;
	private int jCooldownCount = 0;
	private boolean jumpWait = false;
	private boolean right, left;
	
	player mario = new player(430,ground,0,false,false,70,45);
	
	public GamePanel(MarioRun m)
	{
		keys = new boolean[KeyEvent.KEY_LAST+1];
		back = new ImageIcon("MarioBackground.png").getImage().getScaledInstance(10000,650,Image.SCALE_SMOOTH);
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
        currPic = marioRightWalkPics.get(0);
		mainFrame = m;
		setSize(800,600);
        addKeyListener(this);
        loadPlatforms();
        loadCoins();
    	loadGoombas();
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
    	jumpCooldown();
    }
    
    public void update()
    {
    	checkCollectedCoins();
    	checkPlatformCollide();
    	checkCoinCollide();
    	checkGoombaCollide();
    	moveGoombas();
    	System.out.println(mario.getJump());
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
		for(goomba g : goombas)
		{
			g.addX(-4);
			g.addMin(-4);
			g.addMax(-4);
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
    
    public void loadPlatforms()
    {
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
		rground = rand.nextInt(10) + 5;
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
    
    public void loadGoombas()
    {
    	int r;
    	int x;
    	int rground;
    	boolean sameSpot = false;
    	Random rand = new Random();
    	
		for(platform p : platforms)
		{
			r = rand.nextInt(6);
			if(r == 1)
			{
				x = rand.nextInt(p.getSizeX() - 30);
				goombas.add(new goomba(p.getX() + x,p.getY() - 30,30,30,p.getX(),p.getX()+p.getSizeX()-30,true,false,false));
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
				goombas.add(new goomba(x,555-30,30,30,x,x+500,true,false,false));
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
    				}
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
				g.setColor(Color.yellow);  
				g.fillRect(c.getX(),c.getY(),c.getSizeX(),c.getSizeY());
			}
		}
		for(goomba gb : goombas)
		{
			if(gb.getKilled() == false)
			{
				g.setColor(Color.black);  
				g.fillRect(gb.getX(),gb.getY(),gb.getSizeX(),gb.getSizeY());
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
		g.setFont(new Font("Dialogue", Font.PLAIN, 25));
		g.drawString(Integer.toString(collectedCoins)+" COINS", 730, 35);
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

class goomba
{
	private int X;
	private int Y;
	private int sizeX;
	private int sizeY;
	private int minMove;
	private int maxMove;
	private boolean left;
	private boolean right;
	private boolean killed;
	
	public goomba(int plx, int ply, int sx, int sy, int mi, int ma, boolean l, boolean r, boolean k)
	{
		X = plx;
		Y = ply;
		sizeX = sx;
		sizeY = sy;
		minMove = mi;
		maxMove = ma;
	 	left = l;
	 	right = r;
	 	killed = k;
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
	
	public boolean getKilled()
	{
	    return killed;
	}
	
	public void setKilled(boolean b)
	{
	    killed = b;
	}
}
