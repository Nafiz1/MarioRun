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

class GamePanel extends JPanel implements KeyListener, MouseListener
{
	private String screen = "level2";
	
	private boolean []keys;
	private MarioRun mainFrame;
	private Point mouse;
	private Rectangle menuPlay;
	private Rectangle intermissionStore;
	private Rectangle intermissionNext;
	private Rectangle storePrev;
	private int totalCoins;
	private Image back, menuback, intermissionback, storeback, currPic, coinIconPic, lifeIconPic, coinPic, mushroomPic, fireballLPic, fireballRPic, brickPic, shroomPic, questionPic;
	private ArrayList<Image>marioLeftWalkPics = new ArrayList<Image>();
	private ArrayList<Image>marioRightWalkPics = new ArrayList<Image>();
	private ArrayList<Image>marioBigLeftWalkPics = new ArrayList<Image>();
	private ArrayList<Image>marioBigRightWalkPics = new ArrayList<Image>();
	private int backX = -20;
	private int ground = 555-70;
	private int collectedCoins = 0;
	private boolean shiftLeft = false;
	private boolean shiftRight = false;
	private ArrayList<platform>platforms = new ArrayList<platform>();
	private ArrayList<coin>coins = new ArrayList<coin>();
	private ArrayList<mushroom>mushrooms = new ArrayList<mushroom>(); //green mushrooms
	private ArrayList<goomba> goombas = new ArrayList<goomba>();
	private ArrayList<brick> currBrick = new ArrayList<brick>();
	private ArrayList<ArrayList<brick>>bricks = new ArrayList<ArrayList<brick>>();
	private ArrayList<marioShroom> marioMushrooms = new ArrayList<marioShroom>(); //red mushrooms
	private int frames;
	private int jCooldownCount = 0;
	private boolean jumpWait = false;
	private boolean inv = false;
	private int invincibleCount = 0;
	private boolean invincible = false;
	private int lives = 5;
	private boolean right, left, collideL, collideR, marioBig;
	private Font marioFont;
	private boolean fireflowerPower = true;

	private Image back2;
	private int backX2 = -20;
	private ArrayList<coin>coins2 = new ArrayList<coin>();
	private ArrayList<platform>platforms2 = new ArrayList<platform>();
	private ArrayList<goomba> goombas2 = new ArrayList<goomba>();
	private ArrayList<spiny> spinys = new ArrayList<spiny>();
	
	player mario = new player(430,ground,0,false,false,70,40);
	fireball fball = new fireball(mario.getX(),mario.getY(),90,40,false,false,false);
	
	public GamePanel(MarioRun m)
	{
		menuPlay = new Rectangle(590,200,200,50);
		addMouseListener(this);
		
		intermissionStore = new Rectangle(100,200,200,50);
		intermissionNext = new Rectangle(590,200,200,50);
		
		storePrev = new Rectangle(610,30,200,50);
		
		
		back2 = new ImageIcon("MarioBackground2.png").getImage().getScaledInstance(10500,650,Image.SCALE_SMOOTH);
		
		
		keys = new boolean[KeyEvent.KEY_LAST+1];
		back = new ImageIcon("MarioBackground.png").getImage().getScaledInstance(10500,650,Image.SCALE_SMOOTH);
		storeback = new ImageIcon("storeBackground.jpg").getImage();
		intermissionback = new ImageIcon("intermissionBackground.jpg").getImage();
		menuback = new ImageIcon("MenuBackground.png").getImage();
		coinPic = new ImageIcon("Mariopics/coin.gif").getImage().getScaledInstance(15,25,Image.SCALE_SMOOTH);
		mushroomPic = new ImageIcon("Mariopics/mushroom.png").getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH);
		coinIconPic = new ImageIcon("Mariopics/coinIcon.png").getImage().getScaledInstance(20,30,Image.SCALE_SMOOTH);
		lifeIconPic = new ImageIcon("Mariopics/lifeMushroom.png").getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH);
		fireballLPic = new ImageIcon("Mariopics/fireball.png").getImage().getScaledInstance(40,30,Image.SCALE_SMOOTH);
		fireballRPic = new ImageIcon("Mariopics/fireballR.png").getImage().getScaledInstance(40,30,Image.SCALE_SMOOTH);
		brickPic = new ImageIcon("Mariopics/mariobrick.png").getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
		questionPic = new ImageIcon("Mariopics/questionblock.png").getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
		shroomPic = new ImageIcon("Mariopics/redShroom.png").getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
        for(int i=0; i<8; i++)
        {
        	Image tmp = new ImageIcon("MarioPics/mariowalk" +Integer.toString(i)+".png").getImage().getScaledInstance(40,50,Image.SCALE_SMOOTH);
        	Image tmp2 = new ImageIcon("MarioPics/mariowalk" +Integer.toString(i)+".png").getImage().getScaledInstance(40,70,Image.SCALE_SMOOTH);
    		if(i<=3)
    		{
    			for(int z=0; z<5; z++)
    			{
    				marioRightWalkPics.add(tmp);
    				marioBigRightWalkPics.add(tmp2);
    			}
    			
    		}
        	else
        	{
        		for(int z=0; z<5; z++)
        		{
    				marioLeftWalkPics.add(tmp);
    				marioBigLeftWalkPics.add(tmp2);
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
    	loadBricks();
    	
        loadCoins2();
        loadPlatforms2();
    	loadGoombas2();
    	loadSpinys();
	}
	
    public void addNotify()
    {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }
    
    public void update()
    {
    	if(screen == "level1")
    	{
	    	move();
	    	jump();
	    	shoot();
	    	jumpCooldown();
	    	invincibilityCooldown();
	    	checkDeath();
	    	checkFinish();
	    	checkCollectedCoins();
	    	checkPlatformCollide();
	    	checkCoinCollide();
	    	checkGoombaCollide();
	    	checkMushroomCollide();
	    	checkShroomCollide();
	    	checkBrickCollide();
	    	moveGoombas();
	    	moveMushrooms();
	    	//System.out.println(mario.getJump());
	    	//System.out.println(mario.getVY());
    	}
    	if(screen == "level2")
    	{
	    	move();
	    	jump();
	    	shoot();
	    	jumpCooldown();
	    	invincibilityCooldown();
	    	checkDeath();
	    	checkFinish();
	    	checkCollectedCoins();
	    	checkPlatformCollide();
	    	checkCoinCollide();
	    	checkGoombaCollide();
	    	moveGoombas();
	    	checkSpinyCollide();
	    	moveSpinys();
    	}
		mouse = MouseInfo.getPointerInfo().getLocation();
		Point offset = getLocationOnScreen();
		mouse.translate(-offset.x, -offset.y);
    }
	
    public void checkDeath()
    {
    	if(lives == 0)
    	{
    		System.exit(0);
    	}
    }
    
    public void checkFinish()
    {
    	if(screen == "level1")
    	{
	    	if(backX < -9560)
	    	{
	    		totalCoins = collectedCoins;
	    		screen = "intermission";
	    	}	
    	}
    	if(screen == "level2")
    	{
	    	if(backX2 < -9560)
	    	{
	    		totalCoins = collectedCoins;
	    		screen = "intermission2";
	    	}	
    	}
    }
    
    public void checkCollectedCoins()
    {
    	if(screen == "level1")
    	{
	    	int count = 0;
	    	for(coin c : coins)
	    	{
	    		if(c.getCollected()==true)
	    		{
	    			count += c.getPoints();
	    		}
	    	}
	    	collectedCoins = count + totalCoins;
    	}
    	if(screen == "level2")
    	{
	    	int count = 0;
	    	for(coin c : coins2)
	    	{
	    		if(c.getCollected()==true)
	    		{
	    			count += c.getPoints();
	    		}
	    	}
	    	collectedCoins = count + totalCoins;
    	}
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
    	if(screen == "level1")
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
			for(ArrayList<brick> b2 : bricks)
			{
				for(brick b: b2)
				{
					b.addX(-4);
				}
			}
			for(marioShroom mushroom: marioMushrooms)
			{
				mushroom.addX(-4);
			}	
    	}
    	if(screen == "level2")
    	{
			backX2 -= 4;
			for(platform p : platforms2)
			{
				p.addX(-4);
			}
			for(coin c : coins2)
			{
				c.addX(-4);
			}
			for(goomba g : goombas2)
			{
				g.addX(-4);
				g.addMin(-4);
				g.addMax(-4);
			}
			for(spiny s : spinys)
			{
				s.addX(-4);
				s.addMin(-4);
				s.addMax(-4);
			}
    	}
    }
    public void moveBackRight()
    {
    	if(screen == "level1")
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
			for(ArrayList<brick> b2 : bricks)
			{
				for(brick b: b2)
				{
					b.addX(4);
				}
			}
			for(marioShroom mushroom: marioMushrooms)
			{
				mushroom.addX(4);
			}
    	}
    	if(screen == "level2")
    	{
			backX2 += 4;
			for(platform p : platforms2)
			{
				p.addX(+4);
			}
			for(coin c : coins2)
			{
				c.addX(+4);
			}
			for(goomba g : goombas2)
			{
				g.addX(+4);
				g.addMin(+4);
				g.addMax(+4);
			}
			for(spiny s : spinys)
			{
				s.addX(+4);
				s.addMin(+4);
				s.addMax(+4);
			}
    	}
    }
	
	public void move()
	{
		if(screen == "level1")
		{
			if(!collideL)
			{
				if(keys[KeyEvent.VK_RIGHT])
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
			}
			
			if(!collideR)
			{
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
			}
			
			Point m = MouseInfo.getPointerInfo().getLocation();
			Point offset = getLocationOnScreen();
			//System.out.println(m);
		}
		if(screen == "level2")
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
			if(keys[KeyEvent.VK_LEFT] && backX2 <= 0)
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
			
			Point m = MouseInfo.getPointerInfo().getLocation();
			Point offset = getLocationOnScreen();
			//System.out.println(m);
		}
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
			}
			mario.addVY(1);
		}
	}
	
	public void shoot()
	{
		if(keys[KeyEvent.VK_SPACE] && fireflowerPower == true && fball.getUsed() == false)
		{
			fball.setLeft(shiftLeft);
			fball.setRight(shiftRight);
			fball.setX(mario.getX());
			fball.setY(mario.getY()+20);
			fball.setUsed(true);
		}
		if(fball.getUsed() == true)
		{
			if(fball.getLeft() == true)
			{
				fball.addX(-5);
			}
			else if(fball.getRight() == true)
			{
				fball.addX(+5);
			}
			else
			{
				fball.addX(+5);
			}
		}
		if((fball.getX() < mario.getX() - 380) || (fball.getX() > mario.getX() + 380))
		{
			fball.setUsed(false);
		}	
	}
	
    public void jumpCooldown()
    {
    	if(mario.getVY() == 0 || mario.getVY() == 1)
    	{
    		jumpWait = true;
    		jCooldownCount += 1;
    		if(jCooldownCount == 3)
    		{
	    		jumpWait = false;
	    		jCooldownCount = 0;
    		}
    	}
    }
  
    public void moveGoombas()
    {
    	if(screen == "level1")
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
    	if(screen == "level2")
    	{
			for(goomba g : goombas2)
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
    }
    
    public void moveSpinys()
    {
    	if(screen == "level2")
    	{
			for(spiny s : spinys)
			{
				if(!s.getKilled())
				{
					if(s.getLeft() == true)
					{
						if(s.getX() >= s.getMin())
						{
							s.addX(-1);
						}
						else
						{
							s.setLeft(false);
							s.setRight(true);
						}
					}
					if(s.getRight() == true)
					{
						if(s.getX() <= s.getMax())
						{
							s.addX(+1);
						}
						else
						{
							s.setLeft(true);
							s.setRight(false);
						}
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
    	for(int i=0;i<40;i++)
    	{
    		plx = rand.nextInt(9000) + 500;
    		ply = rand.nextInt(250) + 150;
    		size = rand.nextInt(320) + 150;
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
    
    public void loadPlatforms2()
    {
		int plx;
		int ply;
		int size;
    	boolean sameSpot = false;
    	Random rand = new Random();
    	for(int i=0;i<40;i++)
    	{
    		plx = rand.nextInt(9000) + 500;
    		ply = rand.nextInt(250) + 150;
    		size = rand.nextInt(320) + 150;
    		for(platform p : platforms2)
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
    			platforms2.add(new platform(plx,ply,size,10,false));
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
    
    public void loadCoins2()
    {
    	int r;
    	int x;
    	int rground;
    	boolean sameSpot = false;
    	Random rand = new Random();
    	
    	//coins on platforms
		for(platform p : platforms2)
		{
			r = rand.nextInt(4);
			if(r == 1) // 1 in 4 chance
			{
				x = rand.nextInt(p.getSizeX() - 15);
				coins2.add(new coin(p.getX() + x,p.getY() - 30,15,25,1,false));
			}
		}
		
		//coins on ground
		rground = rand.nextInt(10) + 5;
		for(int i=0;i<rground;i++)
		{
			x = rand.nextInt(9000) + 500;
    		for(coin c : coins2)
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
    			coins2.add(new coin(x,555-30,15,25,1,false));
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
    
    public void loadGoombas2()
    {
    	int r;
    	int x;
    	int rground;
    	boolean sameSpot = false;
    	Random rand = new Random();
    	
		for(platform p : platforms2)
		{
			if(p.getSomethingOn() == false)
			{
				r = rand.nextInt(6);
				if(r == 1)
				{
					x = rand.nextInt(p.getSizeX() - 40);
					goombas2.add(new goomba(p.getX() + x,p.getY() - 40,40,40,p.getX(),p.getX()+p.getSizeX()-40,true,false,false));
					p.setSomethingOn(true);
				}	
			}
		}
		
		rground = rand.nextInt(5)+2;
		for(int i=0;i<rground;i++)
		{
			x = rand.nextInt(9000) + 500;
    		for(goomba g : goombas2)
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
				goombas2.add(new goomba(x,555-40,40,40,x,x+500,true,false,false));
    		}
    		else
    		{
    			sameSpot = false;
    		}
		}
    }
    
    public void loadSpinys()
    {
    	int r;
    	int x;
    	int rground;
    	boolean sameSpot = false;
    	Random rand = new Random();
    	
		for(platform p : platforms2)
		{
			if(p.getSomethingOn() == false)
			{
				r = rand.nextInt(6);
				if(r == 1)
				{
					x = rand.nextInt(p.getSizeX() - 40);
					spinys.add(new spiny(p.getX() + x,p.getY() - 40,40,40,p.getX(),p.getX()+p.getSizeX()-40,true,false,false));
					p.setSomethingOn(true);
				}	
			}
		}
		
		rground = rand.nextInt(5)+2;
		for(int i=0;i<rground;i++)
		{
			x = rand.nextInt(9000) + 500;
    		for(goomba g : goombas2)
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
				spinys.add(new spiny(x,555-40,40,40,x,x+500,true,false,false));
    		}
    		else
    		{
    			sameSpot = false;
    		}
		}
    }
    
    public void loadBricks()
    {
    	int x=0;
    	int y=0;
    	boolean sameSpot = false;
    	Random rand = new Random();
    	
    	for(int i=0; i<30; i++)
    	{
    		x = rand.nextInt(9000) + 500;
    		y = 430;
    		for(ArrayList<brick> b2: bricks)
    		{
    			for(brick b: b2)
    			{
    				int dx = b2.get(b2.size()-1).getX() + 40 - b2.get(0).getX();
    				Rectangle brickSegment = new Rectangle(b2.get(0).getX(), b2.get(0).getY(), dx, 40);
    				
    				Rectangle newRect0 = new Rectangle(x-40,y,40,40);
    				Rectangle newRect = new Rectangle(x,y,40,40);
    				Rectangle newRect1 = new Rectangle(x+40, y, 40, 40);
    				Rectangle newRect2 = new Rectangle(x+80, y, 40, 40);
    				Rectangle newRect3 = new Rectangle(x+120, y, 40, 40);
    				Rectangle newRect4 = new Rectangle(x+160, y, 40, 40);
    				Rectangle newRect5 = new Rectangle(x+200, y, 40, 40);
	    			
	    			if(newRect0.intersects(brickSegment) || newRect.intersects(brickSegment) || newRect1.intersects(brickSegment) || newRect2.intersects(brickSegment) || newRect3.intersects(brickSegment) || newRect4.intersects(brickSegment) || newRect5.intersects(brickSegment))
	    			{
	    				sameSpot = true;
    				}
    				
    			}    			
    		}
    		if(!sameSpot)
    		{	
    			int randInt = rand.nextInt(4) + 2;
    			ArrayList<brick> tmp = new ArrayList<brick>();
    			for(int z=0; z<randInt; z++)
    			{
    				tmp.add(new brick(x+z*40, y, 40, 40));
    			}
    			bricks.add(tmp);
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
    
    public void checkBrickCollide()
    {
    	for(ArrayList<brick> b2: bricks)
    	{
    		for(brick b: b2)
    		{
    			Rectangle bLeft = new Rectangle(b2.get(0).getX(), b2.get(0).getY()+1,1,38); //left side of the brick segment
    			Rectangle bRight = new Rectangle(b2.get(b2.size()-1).getX()+40, b2.get(b2.size()-1).getY()+1,1,38); //right side of the brick segment
    			
    			Rectangle brickBot = new Rectangle(b.getX()+2,b.getY()+40,36,1);
		    	//Rectangle brickLeft = new Rectangle(b.getX(),b.getY(),1,39);
		    	//Rectangle brickRight = new Rectangle(b.getX()+40,b.getY(),1,39);
		    	Rectangle brickTop = new Rectangle(b.getX(),b.getY(),40, 1);
		    	
		    	Rectangle marioBot = new Rectangle(mario.getX(),mario.getY()+mario.getHeight(),mario.getWidth(),10);
		    	Rectangle marioLeft = new Rectangle(mario.getX(),mario.getY(),10,mario.getHeight());
		    	Rectangle marioRight = new Rectangle(mario.getX()+mario.getWidth()-10,mario.getY(),10,mario.getHeight());
		    	Rectangle marioTop = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),10);
		    	
		    	Rectangle guy = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
		    	if(bLeft.intersects(guy)){
		        	collideL = true;
		        	mario.setVY(5);	
		        }
		        if(bRight.intersects(guy)){
		        	collideR = true;
		        	mario.setVY(5);
		        }
		    	if(brickBot.intersects(guy)){
		        	mario.setVY(5);
		        }
		        if(brickTop.intersects(guy)){

		        	mario.setY(b.getY()-mario.getHeight());
		        	mario.setVY(0);
		        	mario.setJump(false);
		        }
		        if(mario.getY()==ground || mario.getY()<= b.getY()-50)
		        {
		        	collideL = false;
		        	collideR = false;
		        }
    		}
    	}  	
    }
	
    public void checkPlatformCollide()
    {
    	if(screen == "level1")
    	{
	    	boolean onPlatform = false;
	    	for(platform p : platforms)
	    	{
				Rectangle m = new Rectangle(mario.getX()-5,mario.getY()+mario.getHeight(),mario.getWidth()+10,20); //player has 10 pixel clearance for x position
				Rectangle plat = new Rectangle(p.getX(),p.getY(),p.getSizeX(),p.getSizeY());
				if(mario.getVY() >= 0 && mario.getJump() == true)
				{
		    		if(m.intersects(plat))
		    		{
		    			onPlatform = true;
						mario.setY(p.getY()-mario.getHeight());
						mario.setVY(0);
						mario.setJump(false);
		    		}
				}	
	    	}
	    	if(onPlatform == false && mario.getY() != ground)
	    	{
	    		mario.setJump(true);
	    	}	
    	}
    	if(screen == "level2")
    	{
	    	boolean onPlatform = false;
	    	for(platform p : platforms2)
	    	{
				Rectangle m = new Rectangle(mario.getX()-5,mario.getY()+mario.getHeight(),mario.getWidth()+10,20); //player has 10 pixel clearance for x position
				Rectangle plat = new Rectangle(p.getX(),p.getY(),p.getSizeX(),p.getSizeY());
				if(mario.getVY() >= 0 && mario.getJump() == true)
				{
		    		if(m.intersects(plat))
		    		{
		    			onPlatform = true;
						mario.setY(p.getY()-mario.getHeight());
						mario.setVY(0);
						mario.setJump(false);
		    		}
				}	
	    	}
	    	if(onPlatform == false && mario.getY() != ground)
	    	{
	    		mario.setJump(true);
	    	}	
    	}
    }
    
    public void checkCoinCollide()
    {
    	if(screen == "level1")
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
    	if(screen == "level2")
    	{
	    	for(coin c : coins2)
	    	{
				Rectangle m = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
				Rectangle coinRect = new Rectangle(c.getX(),c.getY(),c.getSizeX(),c.getSizeY());
	    		if(m.intersects(coinRect))
	    		{
	    			c.setCollected(true);
	    		}
	    	}	
    	}
    }
    
    public void checkGoombaCollide()
    {
    	if(screen == "level1")
    	{
	    	for(goomba g : goombas)
	    	{
				Rectangle m = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
				Rectangle f = new Rectangle(fball.getX(),fball.getY(),fball.getSizeX(),fball.getSizeY());
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
	    		else if(f.intersects(goombaRect))
	    		{
	    			if(g.getKilled()==false)
	    			{
	    				if(fball.getUsed() == true)
	    				{
	        				g.setKilled(true);
	    				}
	    			}
	    		}
	    	}
    	}
    	if(screen == "level2")
    	{
	    	for(goomba g : goombas2)
	    	{
				Rectangle m = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
				Rectangle f = new Rectangle(fball.getX(),fball.getY(),fball.getSizeX(),fball.getSizeY());
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
	    		else if(f.intersects(goombaRect))
	    		{
	    			if(g.getKilled()==false)
	    			{
	    				if(fball.getUsed() == true)
	    				{
	        				g.setKilled(true);
	    				}
	    			}
	    		}
	    	}
    	}
    }
    
 
    public void checkSpinyCollide()
    {
    	if(screen == "level2")
    	{
	    	for(spiny s : spinys)
	    	{
				Rectangle m = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
				Rectangle f = new Rectangle(fball.getX(),fball.getY(),fball.getSizeX(),fball.getSizeY());
				Rectangle spinyRect = new Rectangle(s.getX(),s.getY(),s.getSizeX(),s.getSizeY());
	    		if(m.intersects(spinyRect))
	    		{
	    			if(s.getKilled()==false)
	    			{
    					if(invincible == false)
    					{
	    					lives -= 1;
							inv = true;
    					}
	    			}
	    		}
	    		else if(f.intersects(spinyRect))
	    		{
	    			if(s.getKilled()==false)
	    			{
	    				if(fball.getUsed() == true)
	    				{
	        				s.setKilled(true);
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
    
    public void checkShroomCollide()
    {
    	boolean collide = false;
    	for(marioShroom mushroom: marioMushrooms)
    	{
    		
			Rectangle m = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
    		Rectangle shroom = new Rectangle(mushroom.getX(),mushroom.getY(),40,40);
    		if(m.intersects(shroom))
    		{
    			if(mushroom.getVY() != 0 || mushroom.getGround())
    			{
    				mushroom.setCond(false);
    				if(!marioBig)
    				{
    					mario.setHeight(70);
		    			mario.setWidth(40);
		    			marioBig = true;
	    				
	    				if(shiftLeft)
	    				{
	    					currPic = marioBigLeftWalkPics.get(0);
	    				}
	    				if(shiftRight)
	    				{
	    					currPic = marioBigRightWalkPics.get(0);
	    				}
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
    
	public void	mouseClicked(MouseEvent e){}
	public void	mouseEntered(MouseEvent e){}
	public void	mouseExited(MouseEvent e){}
	
	public void	mousePressed(MouseEvent e)
	{
		if(screen == "menu")
		{
			if(menuPlay.contains(mouse))
			{
				screen = "level1";
			}
		}
		if(screen == "intermission")
		{
			if(intermissionNext.contains(mouse))
			{
				screen = "level2";
			}
			if(intermissionStore.contains(mouse))
			{
				screen = "store";
			}
		}
		if(screen == "intermission2")
		{
			if(intermissionNext.contains(mouse))
			{
				screen = "level3";
			}
			if(intermissionStore.contains(mouse))
			{
				screen = "store";
			}
		}
		if(screen == "store")
		{
			if(storePrev.contains(mouse))
			{
				screen = "intermission";
			}
		}	
	}
	
	public void	mouseReleased(MouseEvent e){}

    public void paintComponent(Graphics g)
    { 	
    	if(screen == "menu")
    	{
	    	g.drawImage(menuback,0,0,null);
    		g.setColor(Color.blue);
	    	g.fillRect(menuPlay.x,menuPlay.y,menuPlay.width,menuPlay.height);
    	}
    	if(screen == "intermission")
    	{
	    	g.drawImage(intermissionback,0,0,null);
    		g.setColor(Color.blue);
	    	g.fillRect(intermissionNext.x,intermissionNext.y,intermissionNext.width,intermissionNext.height);
	    	g.fillRect(intermissionStore.x,intermissionStore.y,intermissionStore.width,intermissionStore.height);
    	}
    	if(screen == "intermission2")
    	{
	    	g.drawImage(intermissionback,0,0,null);
    		g.setColor(Color.blue);
	    	g.fillRect(intermissionNext.x,intermissionNext.y,intermissionNext.width,intermissionNext.height);
	    	g.fillRect(intermissionStore.x,intermissionStore.y,intermissionStore.width,intermissionStore.height);
    	}
    	if(screen == "store")
    	{
	    	g.drawImage(storeback,0,0,null);
    		g.setColor(Color.blue);
	    	g.fillRect(storePrev.x,storePrev.y,storePrev.width,storePrev.height);
			g.setColor(Color.white);
			g.drawImage(lifeIconPic, 20, 20, null); 
			g.drawImage(coinIconPic, 25, 53, null);
			g.setFont(marioFont);
			g.drawString("x"+Integer.toString(lives), 52, 48);
			g.drawString(Integer.toString(totalCoins), 48, 82);
    	}
    	if(screen == "level1")
    	{
	    	Rectangle marioRect = new Rectangle(mario.getX(), mario.getY(), mario.getWidth(),mario.getHeight());
	    	g.drawImage(back,backX,0,null);
	    	//g.drawRect(marioRect.x, marioRect.y, marioRect.width, marioRect.height);
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
			for(ArrayList<brick> b2: bricks)
			{
				for(int i=0; i<b2.size(); i++)
				{
					Rectangle m = new Rectangle(mario.getX(), mario.getY(), mario.getWidth(),10);
					Rectangle brickBot = new Rectangle(b2.get(i).getX()+4,b2.get(i).getY()+40,32,1);

					if(m.intersects(brickBot))
					{
						g.drawImage(brickPic,b2.get(i).getX(),b2.get(i).getY()-15,null);
						if(i==1 && b2.size()==3)
						{
							g.drawImage(questionPic,b2.get(1).getX(),b2.get(1).getY()-15,null);
							if(!b2.get(i).getShroom())
							{
								marioMushrooms.add(new marioShroom(b2.get(1).getX(), b2.get(1).getY()-45, 0));
								currBrick = b2;
								b2.get(i).setShroom(true);
							}
						}
						if(i==2 && b2.size()==5)
						{
							g.drawImage(questionPic,b2.get(2).getX(),b2.get(2).getY()-15,null);
							if(!b2.get(i).getShroom())
							{
								marioMushrooms.add(new marioShroom(b2.get(2).getX(), b2.get(2).getY()-45, 0));
								currBrick = b2;
								b2.get(i).setShroom(true);
							}
						}
					}	
					else
					{
						g.drawImage(brickPic,b2.get(i).getX(),b2.get(i).getY(),null);
						if(i==1 && b2.size()==3)
						{
							g.drawImage(questionPic,b2.get(1).getX(),b2.get(1).getY(),null);
						}
						if(i==2 && b2.size()==5)
						{
							g.drawImage(questionPic,b2.get(2).getX(),b2.get(2).getY(),null);
						}
					}
				}
			}
			for(marioShroom mushroom : marioMushrooms)
	    	{
	    		if(mushroom.getCond())
	    		{
	    			g.drawImage(shroomPic, mushroom.getX(),mushroom.getY(),null);
    				mushroom.addX(3);
	    		}
    			//for(ArrayList<brick> b2: bricks)
    			//{
    				//for(brick b: b2)
    				//{
    					//System.out.println(mushroom.getGround());
    					int dx = currBrick.get(currBrick.size()-1).getX()+40 - currBrick.get(0).getX();
    					//Rectangle plat = new Rectangle(b2.get(0).getX(), b2.get(0).getY(), dx, 40);
    					Rectangle mushRect = new Rectangle(mushroom.getX(), mushroom.getY(), 40, 40);
    					g.setColor(Color.red);
    					//g.drawRect(plat.x, plat.y, plat.width, plat.height);
    					g.drawRect(mushRect.x, mushRect.y, mushRect.width, mushRect.height);
    					if(mushroom.getX() > currBrick.get(0).getX()+dx && mushroom.getGround() == false)
    					{		
		    				mushroom.setVY(10);
		    			}
		    			if(mushroom.getY()>=ground)
	    				{
	    					mushroom.setGround(true);
		    				mushroom.setVY(0);
		    				mushroom.setY(515);
	    					//mushroom.setGround(true);
	    				}
	    					mushroom.addY(mushroom.getVY());
	    				
		    			System.out.println(mushroom.getY());
		    			//System.out.println(mushroom.getVY());
    				//}
    			//}
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
			
			if(fball.getUsed() == true)
			{
				if(fball.getLeft() == true)
				{
					g.drawImage(fireballLPic, fball.getX(), fball.getY(), null);
				}
				else
				{
					g.drawImage(fireballRPic, fball.getX(), fball.getY(), null);
				}
			}
			
			if(!right && !left)
			{
				if(marioBig)
				{
	        		g.drawImage(currPic, mario.getX(), mario.getY(), null);
				}
				else
				{
	        		g.drawImage(currPic, mario.getX(), mario.getY()+20, null);
				}
	        }
			if(right)
			{
				if(marioBig)
				{
					currPic = marioBigRightWalkPics.get(0);
					g.drawImage(marioBigRightWalkPics.get(frames),mario.getX(),mario.getY(),null);
				}
				else
				{
					currPic = marioRightWalkPics.get(0);
	            	g.drawImage(marioRightWalkPics.get(frames),mario.getX(),mario.getY()+20,null);
				}
	        }
	        if(left)
	        {
	        	if(marioBig)
				{
					currPic = marioBigLeftWalkPics.get(0);
					g.drawImage(marioBigLeftWalkPics.get(frames),mario.getX(),mario.getY(),null);
				}
				else
				{
					currPic = marioLeftWalkPics.get(0);
	            	g.drawImage(marioLeftWalkPics.get(frames),mario.getX(),mario.getY()+20,null);	
				}
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
			
			Rectangle mRect = new Rectangle(mario.getX()-5,mario.getY()+mario.getHeight(),mario.getWidth()+10,10);
			g.drawRect(mRect.x,mRect.y,mRect.width,mRect.height);
    	}
    	if(screen == "level2")
    	{
	    	Rectangle marioRect = new Rectangle(mario.getX(), mario.getY(), mario.getWidth(),mario.getHeight());
	    	g.drawImage(back2,backX2,0,null);
			g.drawRect(marioRect.x, marioRect.y, marioRect.width, marioRect.height);
			for(platform p : platforms2)
			{
				Color platBottomColor = new Color(216,192,96);
				g.setColor(platBottomColor);  
				g.fillRect(p.getX(),p.getY()+10,p.getSizeX(),565 - p.getY());
			}
			for(platform p : platforms2)
			{
				Color platTopColor = new Color(150,127,54);
				g.setColor(platTopColor);  
				g.fillRect(p.getX(),p.getY(),p.getSizeX(),p.getSizeY());
			}
			for(coin c : coins2)
			{
				if(c.getCollected() == false)
				{
					g.drawImage(coinPic, c.getX(), c.getY(), null);
				}
			}
			for(goomba gb : goombas2)
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
			
			for(spiny s : spinys)
			{
				g.setColor(Color.white);
				g.drawRect(s.getX(), s.getY(), s.getSizeX(), s.getSizeY());
			}
			
			if(!right && !left)
			{
				if(marioBig)
				{
	        		g.drawImage(currPic, mario.getX(), mario.getY(), null);
				}
				else
				{
	        		g.drawImage(currPic, mario.getX(), mario.getY()+20, null);
				}
	        }
			if(right)
			{
				if(marioBig)
				{
					currPic = marioBigRightWalkPics.get(0);
					g.drawImage(marioBigRightWalkPics.get(frames),mario.getX(),mario.getY(),null);
				}
				else
				{
					currPic = marioRightWalkPics.get(0);
	            	g.drawImage(marioRightWalkPics.get(frames),mario.getX(),mario.getY()+20,null);
				}
	        }
	        if(left)
	        {
	        	if(marioBig)
				{
					currPic = marioBigLeftWalkPics.get(0);
					g.drawImage(marioBigLeftWalkPics.get(frames),mario.getX(),mario.getY(),null);
				}
				else
				{
					currPic = marioLeftWalkPics.get(0);
	            	g.drawImage(marioLeftWalkPics.get(frames),mario.getX(),mario.getY()+20,null);	
				}
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
	public void setHeight(int n)
	{
		height = n;
	}
	public void setWidth(int n)
	{
		width = n;
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

class fireball
{
	private int X;
	private int Y;
	private int sizeX;
	private int sizeY;
	private boolean used;
	private boolean left;
	private boolean right;
	
	public fireball(int cx, int cy, int sx, int sy, boolean u, boolean l, boolean r)
	{
		X = cx;
		Y = cy;
		sizeX = sx;
		sizeY = sy;
		used = u;
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
	
	public void setX(int num)
	{
		X = num;
	}
	
	public void setY(int num)
	{
		Y = num;
	}
	
	public boolean getUsed()
	{
	    return used;
	}
	
	public void setUsed(boolean b)
	{
	    used = b;
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

class brick
{
	private int x;
	private int y;
	private int sizeX;
	private int sizeY;
	private ArrayList<Image>marioShroomPics = new ArrayList<Image>();
	private boolean shroom = false; 

	public brick(int x1, int y1, int sizeX1, int sizeY1)
	{
		x = x1;
		y = y1;
		sizeX = sizeX1;
		sizeY = sizeY1;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public boolean getShroom()
	{
		return shroom;
	}
	
	public void addX(int n)
	{
		x += n;
	}
	
	public void setShroom(boolean bool)
	{
		shroom = bool;
	}
}

class marioShroom
{
	private int x;
	private int y;
	private int vy;
	private Image shroomPic;
	private boolean onGround = false;
	private boolean cond = true;
	
	public marioShroom(int x1, int y1, int vy1)
	{
		x = x1;
		y = y1;
		vy = vy1;
		shroomPic = new ImageIcon("Mariopics/redShroom.png").getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getVY()
	{
		return vy;
	}
	
	public boolean getGround()
	{
		return onGround;
	}
	
	public boolean getCond()
	{
		return cond;
	}
	
	public void addX(int x1)
	{
		x += x1;
	}
	
	public void addY(int y1)
	{
		y += y1;
	}
	
	public void setVY(int vy1)
	{
		vy = vy1;
	}
	
	public void setY(int y1)
	{
		y = y1;
	}
	
	public void setGround(boolean bool)
	{
		onGround = bool;
	}
	
	public void setCond(boolean bool)
	{
		cond = bool;
	}
}

class spiny
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
	
	public spiny(int gx, int gy, int sx, int sy, int mi, int ma, boolean l, boolean r, boolean k)
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