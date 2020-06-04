//MarioRun.java
//Nafiz Hasan and Ashad Ahmed
//

import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.MouseInfo;
import javax.swing.Timer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MarioRun extends JFrame implements ActionListener
{
	Timer myTimer;   
	GamePanel game;
		
    public MarioRun()
    {
		super("Mario Run");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900,650);

		myTimer = new Timer(15, this);

	
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
	private String screen = "level3";
	
	private boolean []keys;
	private MarioRun mainFrame;
	private Point mouse;
	private Rectangle menuPlay;
	private Rectangle intermissionStore;
	private Rectangle intermissionNext;
	private Rectangle store1;
	private Rectangle store2;
	private Rectangle store3;
	private Rectangle storePrev;
	private int totalCoins;
	private Image back, platBack, menuback, intermissionback, storeback, currPic, coinIconPic, lifeIconPic, coinPic, mushroomPic, fireFlowerIconPic, marioShroomIconPic, fireballLPic, fireballRPic, brickPic, shroomPic, questionPic;
	private ArrayList<Image>marioLeftWalkPics = new ArrayList<Image>();
	private ArrayList<Image>marioRightWalkPics = new ArrayList<Image>();
	private ArrayList<Image>marioBigLeftWalkPics = new ArrayList<Image>();
	private ArrayList<Image>marioBigRightWalkPics = new ArrayList<Image>();
	private ArrayList<Image>tmp = new ArrayList<Image>();
	private int backX = -20;
	private int ground = 555-50;
	private int collectedCoins = 0;
	private boolean shiftLeft = false;
	private boolean shiftRight = false;
	private boolean collide, invE;
	private ArrayList<brick> currBrick = new ArrayList<brick>();
	private ArrayList<ArrayList<Image>> evolvePicsRight = new ArrayList<ArrayList<Image>>();
	private ArrayList<ArrayList<Image>> evolvePicsLeft = new ArrayList<ArrayList<Image>>();
	private ArrayList<BufferedImage> platPics = new ArrayList<BufferedImage>();
	private ArrayList<BufferedImage> platTopPics = new ArrayList<BufferedImage>();
	private int frames;
	private int jCooldownCount = 0;
	private int evolveCD = 0;
	private int evolveVY = 0;
	private boolean jumpWait = false;
	private boolean inv = false;
	private int invincibleCount = 0;
	private boolean invincible = false;
	private int lives = 5;
	private boolean right, left, collideL, collideR, marioBig;
	private Font marioFont;
	private boolean fireflowerPower = true;
	private int breakCount = 0;
	
	private ArrayList<coin> currCoins = new ArrayList<coin>();
	private ArrayList<platform> currPlatforms = new ArrayList<platform>();
	private ArrayList<mushroom> currMushrooms = new ArrayList<mushroom>();
	private ArrayList<goomba> currGoombas = new ArrayList<goomba>();
	private ArrayList<spiny> currSpinys = new ArrayList<spiny>();
	private ArrayList<bulletBill> currBills = new ArrayList<bulletBill>();
	private ArrayList<ArrayList<brick>> currBricks = new ArrayList<ArrayList<brick>>();
	private ArrayList<marioShroom> currMarioMushrooms = new ArrayList<marioShroom>();
	private Image currBack;
	private int currBackX;
	
	//Each level consists of at least some platforms, coins, green mushrooms (gives extra lives), goombas, bricks and question blocks
	//and red mushrooms (transform mario into big mario). Level 2 introduces spinys and level 3 brings bullet bills.
	//Level1------------------------
	private ArrayList<platform>platforms = new ArrayList<platform>();
	private ArrayList<coin>coins = new ArrayList<coin>();
	private ArrayList<mushroom>mushrooms = new ArrayList<mushroom>(); //green mushrooms
	private ArrayList<goomba> goombas = new ArrayList<goomba>();
	private ArrayList<ArrayList<brick>>bricks = new ArrayList<ArrayList<brick>>();
	private ArrayList<marioShroom> marioMushrooms = new ArrayList<marioShroom>(); //red mushrooms
	//Level2------------------------
	private Image back2;
	private int backX2 = -20;
	private ArrayList<coin>coins2 = new ArrayList<coin>();
	private ArrayList<platform>platforms2 = new ArrayList<platform>();
	private ArrayList<mushroom>mushrooms2 = new ArrayList<mushroom>();
	private ArrayList<goomba> goombas2 = new ArrayList<goomba>();
	private ArrayList<spiny> spinys = new ArrayList<spiny>();
	private ArrayList<ArrayList<brick>>bricks2 = new ArrayList<ArrayList<brick>>();
	private ArrayList<marioShroom> marioMushrooms2 = new ArrayList<marioShroom>();
	//Level3------------------------
	private Image back3;
	private int backX3 = -20;
	private int intermissionNum;
	private ArrayList<coin>coins3 = new ArrayList<coin>();
	private ArrayList<platform>platforms3 = new ArrayList<platform>();
	private ArrayList<mushroom>mushrooms3 = new ArrayList<mushroom>();
	private ArrayList<goomba> goombas3 = new ArrayList<goomba>();
	private ArrayList<spiny> spinys2 = new ArrayList<spiny>();
	private ArrayList<bulletBill> bills = new ArrayList<bulletBill>();
	private ArrayList<ArrayList<brick>>bricks3 = new ArrayList<ArrayList<brick>>();
	private ArrayList<marioShroom> marioMushrooms3 = new ArrayList<marioShroom>();
	
	player mario = new player(430,ground,0,false,false,50,25);
	fireball fball = new fireball(mario.getX(),mario.getY(),40,30,false,false,false);
	
	BufferedImage platPic;
	Image[] brickSegments = new Image[4];
	
	public GamePanel(MarioRun m)
	{
		menuPlay = new Rectangle(590,200,200,50);
		addMouseListener(this);
		
		intermissionStore = new Rectangle(100,200,200,50);
		intermissionNext = new Rectangle(590,200,200,50);
		
		storePrev = new Rectangle(610,30,200,50);
		store1 = new Rectangle(73,417,200,200);
		store2 = new Rectangle(332,417,200,200);
		store3 = new Rectangle(626,417,200,200);
		
		back2 = new ImageIcon("MarioBackground2.png").getImage().getScaledInstance(10500,650,Image.SCALE_SMOOTH);
		
		back3 = new ImageIcon("MarioBackground3.png").getImage().getScaledInstance(10500,650,Image.SCALE_SMOOTH);
		
		keys = new boolean[KeyEvent.KEY_LAST+1];
		back = new ImageIcon("MarioBackground.png").getImage().getScaledInstance(10500,650,Image.SCALE_SMOOTH);
		storeback = new ImageIcon("storeBackground.jpg").getImage().getScaledInstance(900,620,Image.SCALE_SMOOTH);
		intermissionback = new ImageIcon("intermissionBackground.jpg").getImage();
		menuback = new ImageIcon("MenuBackground.png").getImage();
		coinPic = new ImageIcon("Mariopics/coin.gif").getImage().getScaledInstance(15,25,Image.SCALE_SMOOTH);
		mushroomPic = new ImageIcon("Mariopics/mushroom.png").getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH);
		fireFlowerIconPic = new ImageIcon("Mariopics/fireFlowerIconPic.png").getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH);
		marioShroomIconPic = new ImageIcon("Mariopics/marioShroomIcon.png").getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH);
		coinIconPic = new ImageIcon("Mariopics/coinIcon.png").getImage().getScaledInstance(20,30,Image.SCALE_SMOOTH);
		lifeIconPic = new ImageIcon("Mariopics/lifeMushroom.png").getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH);
		fireballLPic = new ImageIcon("Mariopics/fireball.png").getImage().getScaledInstance(40,30,Image.SCALE_SMOOTH);
		fireballRPic = new ImageIcon("Mariopics/fireballR.png").getImage().getScaledInstance(40,30,Image.SCALE_SMOOTH);
		brickPic = new ImageIcon("Mariopics/mariobrick.png").getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
		questionPic = new ImageIcon("Mariopics/questionblock.png").getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
		shroomPic = new ImageIcon("Mariopics/redShroom.png").getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH);
		for(int i=0; i<4; i++)
		{
			brickSegments[i] = new ImageIcon("Mariopics/brickSegment"+Integer.toString(i)+".jpg").getImage().getScaledInstance(10,10,Image.SCALE_SMOOTH);
		}
		
        for(int i=0; i<8; i++)
        {
        	Image tmp = new ImageIcon("MarioPics/mariowalk" +Integer.toString(i)+".png").getImage().getScaledInstance(mario.getWidth(),mario.getHeight(),Image.SCALE_SMOOTH);
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
        for(int i=0; i<8; i+=2)
        {
        	ArrayList<Image> tmp = new ArrayList<Image>();
        	tmp.add(new ImageIcon("Mariopics/marioE"+Integer.toString(i)+".png").getImage());
        	tmp.add(new ImageIcon("Mariopics/marioE"+Integer.toString(i+1)+".png").getImage());
        	for(int z=0; z<5; z++)
        	{
        		evolvePicsRight.add(tmp);
        	}
        }
        for(int i=8; i<16; i+=2)
        {
        	ArrayList<Image> tmp = new ArrayList<Image>();
        	tmp.add(new ImageIcon("Mariopics/marioE"+Integer.toString(i)+".png").getImage());
        	tmp.add(new ImageIcon("Mariopics/marioE"+Integer.toString(i+1)+".png").getImage());
        	for(int z=0; z<5; z++)
        	{
        		evolvePicsLeft.add(tmp);
        	}
        }
        try
		{
			marioFont = Font.createFont(Font.TRUETYPE_FONT, new File("SuperMario.ttf")).deriveFont(48f);
			platPic = ImageIO.read(new File("MarioPics/platback.jpg"));
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
    	
        //loadCoins2();
        //loadPlatforms2();
    	//loadGoombas2();
    	loadSpinys();
    	loadBills();
    	System.out.println(spinys2.size());

	}
	
    public void addNotify()
    {
        super.addNotify();
        requestFocus();
        mainFrame.start();
    }
    
    public void update()
    {
    	getCurr();
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
    	checkSpinyCollide();
    	checkBillCollide();
    	moveGoombas();
    	moveMushrooms();
    	moveSpinys();
    	moveBills();
    	
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
	    		intermissionNum = 1;
	    	}	
    	}
    	if(screen == "level2")
    	{
	    	if(backX2 < -9560)
	    	{
	    		totalCoins = collectedCoins;
	    		screen = "intermission2";
	    		intermissionNum = 2;
	    	}	
    	}
    }
    
    public void shrink()
    {
		mario.setHeight(50);
		mario.setWidth(25);
		marioBig = false;
		collide = true;
		ground += 20;
		mario.setY(mario.getY()+20);	
		evolveVY = mario.getVY();
		invE = true;
    }
    
    public void grow()
    {
		mario.setHeight(70);
		mario.setWidth(40);
		marioBig = true;
		collide = true;
		ground-=20;
		mario.setY(mario.getY()-20);
		evolveVY = mario.getVY();
		invE = true;
    }
    
    public void getCurr()
    {
    	if(screen == "level1")
    	{
    		currPlatforms = platforms;
    		currGoombas = goombas;
    		currCoins = coins;
    		currMushrooms = mushrooms;
    		currBricks = bricks;
    		currMarioMushrooms = marioMushrooms;
    		currBackX = backX;
    		currBack = back;
    	}
    	
    	if(screen == "level2")
    	{
    		currPlatforms = platforms2;
    		currGoombas = goombas2;
    		currCoins = coins2;
    		currMushrooms = mushrooms2;
    		currBricks = bricks2;
    		currMarioMushrooms = marioMushrooms2;
    		currSpinys = spinys;
    		currBackX = backX2;
    		currBack = back2;
    	}
    	
    	if(screen == "level3")
    	{
    		currPlatforms = platforms3;
    		currGoombas = goombas3;
    		currCoins = coins3;
    		currMushrooms = mushrooms3;
    		currBricks = bricks3;
    		currMarioMushrooms = marioMushrooms3;
    		currSpinys = spinys2;
    		currBills = bills;
    		currBackX = backX3;
    		currBack = back3;
    	}
    }
    
    public void getTmp()
    {
		if(shiftRight)
		{
			if(right)
			{
				tmp = evolvePicsRight.get(frames);
			}
			else if(mario.getVY()!=0)
			{
				tmp = evolvePicsRight.get(frames);
			}
			else
			{
				tmp = evolvePicsRight.get(0);
			}
		}
		if(shiftLeft)
		{
			if(left || mario.getVY()!=0)
			{
				tmp = evolvePicsLeft.get(frames);
			}
			else if(mario.getVY()!=0)
			{
				tmp = evolvePicsLeft.get(frames);
			}
			else
			{
				tmp = evolvePicsLeft.get(0);
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
    	else if(invE) //evolve animation invincibility
    	{
    		invincible = true;
    		invincibleCount += 1;
    		if(invincibleCount == 90) 
    		{
	    		invincible = false;
	    		invincibleCount = 0;
	    		invE = false;
    		}
    	}
    }
    
    public void moveBackLeft()
    {
		backX -= 4;
		for(platform p : currPlatforms)
		{
			p.addX(-4);
		}
		for(coin c : currCoins)
		{
			c.addX(-4);
		}
		for(goomba g : currGoombas)
		{
			g.addX(-4);
			g.addMin(-4);
			g.addMax(-4);
		}
		for(mushroom m : currMushrooms)
		{
			m.addX(-4);
			m.addMin(-4);
			m.addMax(-4);
		}
		for(ArrayList<brick> b2 : currBricks)
		{
			for(brick b: b2)
			{
				b.addX(-4);
			}
		}
		for(marioShroom mushroom: currMarioMushrooms)
		{
			mushroom.addX(-4);
		}	
		for(bulletBill bb : currBills)
		{
			bb.addX(-4);
			bb.addBX(-4);
		}
		for(spiny s : currSpinys)
		{
			s.addX(-4);
			s.addMin(-4);
			s.addMax(-4);
		}
    }
    public void moveBackRight()
    {
		backX += 4;
		for(platform p : currPlatforms)
		{
			p.addX(+4);
		}
		for(coin c : currCoins)
		{
			c.addX(+4);
		}
		for(goomba g : currGoombas)
		{
			g.addX(+4);
			g.addMin(+4);
			g.addMax(+4);
		}
		for(mushroom m : currMushrooms)
		{
			m.addX(+4);
			m.addMin(+4);
			m.addMax(+4);
		}
		for(ArrayList<brick> b2 : currBricks)
		{
			for(brick b: b2)
			{
				b.addX(4);
			}
		}
		for(marioShroom mushroom: currMarioMushrooms)
		{
			mushroom.addX(4);
		}
		for(bulletBill bb : currBills)
		{
			bb.addX(4);
			bb.addBX(4);
		}
		for(spiny s : currSpinys)
		{
			s.addX(+4);
			s.addMin(+4);
			s.addMax(+4);
		}
    }
	
	public void move()
	{
		if(screen == "level1")
		{
			if(!collideL && !collide)
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
			
			if(!collideR && !collide)
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
			else
			{
				right = false;
				left = false;
			}
			Point m = MouseInfo.getPointerInfo().getLocation();
			Point offset = getLocationOnScreen();
		}
		if(screen == "level2")
		{
			if(!collideL && !collide)
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
			
			if(!collideR && !collide)
			{
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
			}
			else
			{
				right = false;
				left = false;
			}
			Point m = MouseInfo.getPointerInfo().getLocation();
			Point offset = getLocationOnScreen();
		}
		if(screen == "level3")
		{
			if(!collideL && !collide)
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
			
			if(!collideR && !collide)
			{
				if(keys[KeyEvent.VK_LEFT] && backX3 <= 0)
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
			else
			{
				right = false;
				left = false;
			}
			Point m = MouseInfo.getPointerInfo().getLocation();
			Point offset = getLocationOnScreen();
		}
	}
	
	public void jump()
	{
		if(!collide)
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
		for(goomba g : currGoombas)
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
    
    public void moveSpinys()
    {
		for(spiny s : currSpinys)
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
    
    public void moveMushrooms()
    {
		for(mushroom m : currMushrooms)
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
    
    public void moveBills()
    {
    	for(bulletBill bb: bills)
    	{
    		
    		if(bb.getCD())
    		{
    			if(bb.getLeft())
    			{
    				bb.addX(-2);
    			}
	    		if(bb.getRight())
	    		{
	    			bb.addX(2);
	    		}
    		}
    		else
    		{
    			bb.setCD(true);
				if(bb.getBX()< mario.getX())
				{
					bb.setRight(true);
					bb.setLeft(false);
					
				}
				if(bb.getBX()>= mario.getX())
				{
					bb.setLeft(true);
					bb.setRight(false);
				}	
    		}
    	}
    }
    
    public void loadPlatforms()
    {
    	for(int z=1; z<4; z++)
    	{
    		int plx;
			int ply;
			int size;
	    	boolean sameSpot = false;
	    	ArrayList<platform> currList = new ArrayList<platform>();
	    	if(z==1)
	    	{
	    		currList = platforms;
	    	}
	    	if(z==2)
	    	{
	    		currList = platforms2;
	    	}
	    	if(z==3)
	    	{
	    		currList = platforms3;
	    	}
	    	Random rand = new Random();
	    	for(int i=0;i<40;i++)
	    	{
	    		plx = rand.nextInt(9000) + 500;
	    		ply = rand.nextInt(250) + 150;
	    		size = rand.nextInt(320) + 150;
	    		for(platform p : currList)
	    		{
	    			Rectangle newRect = new Rectangle(plx,ply,size,555-ply);
	    			Rectangle oldRect = new Rectangle(p.getX()-10,p.getY()-40,p.getSizeX()+20,p.getSizeY()+80);
	    			if(newRect.intersects(oldRect))
	    			{
	    				sameSpot = true;
	    			}	
	    		}
	    		if(sameSpot == false)
	    		{
	    			if(z==1)
	    			{
	    				platform plat = new platform(plx,ply,size,10,false);
		    			platforms.add(plat);
		    			platPics.add(platPic.getSubimage(0, 13, plat.getSizeX(), 545 - plat.getY()));
		    			platTopPics.add(platPic.getSubimage(0, 0, plat.getSizeX(), 10));
	    			}
	    			if(z==2)
	    			{
	    				platform plat = new platform(plx,ply,size,10,false);
		    			platforms2.add(plat);
		    			//platPics.add(platPic.getSubimage(0, 13, plat.getSizeX(), 545 - plat.getY()));
		    			//platTopPics.add(platPic.getSubimage(0, 0, plat.getSizeX(), 10));
	    			}
	    			if(z==3)
	    			{
	    				platform plat = new platform(plx,ply,size,10,false);
		    			platforms3.add(plat);
		    			//platPics.add(platPic.getSubimage(0, 13, plat.getSizeX(), 545 - plat.getY()));
		    			//platTopPics.add(platPic.getSubimage(0, 0, plat.getSizeX(), 10));
	    			}
	    		}
	    		else
	    		{
	    			sameSpot = false;
	    		}
	    	}
    	}
    }
    
    public void loadCoins()
    {
    	for(int z=1; z<4; z++)
    	{
    		int r;
	    	int x;
	    	int rground;
	    	boolean sameSpot = false;
	    	Random rand = new Random();
	    	ArrayList<platform> currList = new ArrayList<platform>();
	    	ArrayList<coin> currList2 = new ArrayList<coin>();
	    	if(z==1)
	    	{
	    		currList = platforms;
	    		currList2 = coins;
	    	}
	    	if(z==2)
	    	{
	    		currList = platforms2;
	    		currList2 = coins2;
	    	}
	    	if(z==3)
	    	{
	    		currList = platforms3;
	    		currList2 = coins3;
	    	}
	    	//coins on platforms
			for(platform p : currList)
			{
				r = rand.nextInt(4);
				if(r == 1) // 1 in 4 chance
				{
					x = rand.nextInt(p.getSizeX() - 15);
					if(z==1)
					{
						coins.add(new coin(p.getX() + x,p.getY() - 30,15,25,1,false));
					}
					if(z==2)
					{
						coins2.add(new coin(p.getX() + x,p.getY() - 30,15,25,1,false));
					}
					if(z==3)
					{
						coins3.add(new coin(p.getX() + x,p.getY() - 30,15,25,1,false));
					}
				}
			}
			
			//coins on ground
			rground = rand.nextInt(10) + 5;
			for(int i=0;i<rground;i++)
			{
				x = rand.nextInt(9000) + 500;
	    		for(coin c : currList2)
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
	    			if(z==1)
	    			{
	    				coins.add(new coin(x,555-30,15,25,1,false));
	    			}
	    			if(z==2)
	    			{
	    				coins2.add(new coin(x,555-30,15,25,1,false));
	    			}
	    			if(z==3)
	    			{
	    				coins3.add(new coin(x,555-30,15,25,1,false));
	    			}
	    		}
	    		else
	    		{
	    			sameSpot = false;
	    		}
			}
    	}
    }
    
    public void loadGoombas()
    {
    	for(int z=1; z<4; z++)
    	{
    		int r;
	    	int x;
	    	int rground;
	    	boolean sameSpot = false;
	    	Random rand = new Random();
	    	ArrayList<platform> currList = new ArrayList<platform>();
	    	ArrayList<goomba> currList2 = new ArrayList<goomba>();
	    	if(z==1)
	    	{
	    		currList = platforms;
	    		currList2 = goombas;
	    	}
	    	if(z==2)
	    	{
	    		currList = platforms2;
	    		currList2 = goombas2;
	    	}
	    	if(z==3)
	    	{
	    		currList = platforms3;
	    		currList2 = goombas3;
	    	}
			for(platform p : currList)
			{
				if(p.getSomethingOn() == false)
				{
					r = rand.nextInt(4);
					if(r == 1)
					{
						x = rand.nextInt(p.getSizeX() - 40);
						if(z==1)
						{
							goombas.add(new goomba(p.getX() + x,p.getY() - 40,40,40,p.getX(),p.getX()+p.getSizeX()-40,true,false,false));	
						}
						if(z==2)
						{
							goombas2.add(new goomba(p.getX() + x,p.getY() - 40,40,40,p.getX(),p.getX()+p.getSizeX()-40,true,false,false));	
						}
						if(z==3)
						{
							goombas3.add(new goomba(p.getX() + x,p.getY() - 40,40,40,p.getX(),p.getX()+p.getSizeX()-40,true,false,false));	
						}
						p.setSomethingOn(true);
					}	
				}
			}
			
			rground = rand.nextInt(8)+2;
			for(int i=0;i<rground;i++)
			{
				x = rand.nextInt(9000) + 500;
	    		for(goomba g : currList2)
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
	    			if(z==1)
	    			{
	    				goombas.add(new goomba(x,555-40,40,40,x,x+500,true,false,false));
	    			}
	    			if(z==1)
	    			{
	    				goombas2.add(new goomba(x,555-40,40,40,x,x+500,true,false,false));
	    			}
	    			if(z==3)
	    			{
	    				goombas3.add(new goomba(x,555-40,40,40,x,x+500,true,false,false));
	    			}
	    		}
	    		else
	    		{
	    			sameSpot = false;
	    		}
			}	
    	}
    }
    
    public void loadSpinys()
    {
    	for(int z=2; z<4; z++)
    	{
    		int r;
	    	int x;
	    	int rground;
	    	boolean sameSpot = false;
	    	Random rand = new Random();
	    	ArrayList<platform> currList = new ArrayList<platform>();
	    	ArrayList<spiny> currList2 = new ArrayList<spiny>();
	    	ArrayList<goomba> currList3 = new ArrayList<goomba>();
	    	if(z==2)
	    	{
	    		currList = platforms2;
	    		currList2 = spinys;
	    		currList3 = goombas2;
	    	}
	    	if(z==3)
	    	{
	    		currList = platforms3;
	    		currList2 = spinys2;
	    		currList3 = goombas3;
	    	}
			for(platform p : currList)
			{
				if(p.getSomethingOn() == false)
				{
					r = rand.nextInt(3);
					if(r == 1)
					{
						x = rand.nextInt(p.getSizeX() - 40);
						if(z==2)
						{
							spinys.add(new spiny(p.getX() + x,p.getY() - 36,40,40,p.getX(),p.getX()+p.getSizeX()-40,true,false,false));
						}
						if(z==3)
						{
							spinys2.add(new spiny(p.getX() + x,p.getY() - 36,40,40,p.getX(),p.getX()+p.getSizeX()-40,true,false,false));
						}
						p.setSomethingOn(true);
					}	
				}
			}
			
			rground = rand.nextInt(5)+2;
			for(int i=0;i<rground;i++)
			{
				x = rand.nextInt(9000) + 500;
	    		for(goomba g : currList3)
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
	    			if(z==2)
	    			{
	    				spinys.add(new spiny(x,555-40,40,40,x,x+500,true,false,false));
	    			}
					if(z==3)
	    			{
	    				spinys2.add(new spiny(x,555-40,40,40,x,x+500,true,false,false));
	    			}
	    		}
	    		else
	    		{
	    			sameSpot = false;
	    		}
			}
    	}
    }
    
    public void loadBills()
    {
    	int r, x, rground;
    	boolean sameSpot = false;
    	Random rand = new Random();
   		for(platform p : platforms3)
		{
			if(p.getSomethingOn() == false)
			{
				r = rand.nextInt(2);
				if(r == 1)
				{
					x = rand.nextInt(p.getSizeX() - 40);
					bills.add(new bulletBill(p.getX() + x,p.getY() - 55, p.getX()+x, p.getY() - 70, 70, 40, false));
					p.setSomethingOn(true);
				}	
			}
		}
		
		rground = rand.nextInt(5)+2;
		for(int i=0;i<rground;i++)
		{
			x = rand.nextInt(9000) + 500;
    		for(goomba g : goombas3)
    		{
    			Rectangle newRect = new Rectangle(x,555-25,50,20);
    			Rectangle oldRect = new Rectangle(g.getX(),g.getY(),g.getSizeX()+20,g.getSizeY());
    			if(newRect.intersects(oldRect))
    			{
					sameSpot = true;
    			}
    		}
      		if(sameSpot == false)
    		{
				bills.add(new bulletBill(x,555-55, x, 555-70, 70, 40, false));
    		}
    		else
    		{
    			sameSpot = false;
    		}
		}
    }
    
    public void loadBricks()
    {
    	for(int z=1; z<4; z++)
    	{
    		int x=0;
	    	int y=0;
	    	boolean sameSpot = false;
	    	Random rand = new Random();
	    	ArrayList<ArrayList<brick>> currList = new ArrayList<ArrayList<brick>>();
	    	if(z==1)
	    	{
	    		currList = bricks;
	    	}
	    	if(z==2)
	    	{
	    		currList = bricks2;
	    	}
	    	if(z==3)
	    	{
	    		currList = bricks3;
	    	}
	    	for(int i=0; i<30; i++)
	    	{
	    		x = rand.nextInt(9000) + 500;
	    		y = 430;
	    		for(ArrayList<brick> b2: currList)
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
	    			for(int n=0; n<randInt; n++)
	    			{
	    				tmp.add(new brick(x+n*40, y, 40, 40)); //this will generate a brick segment between 2-5 bricks long, each being 40 by 40
	    			}
	    			if(z==1)
	    			{
	    				bricks.add(tmp);
	    			}
	    			if(z==2)
	    			{
	    				bricks2.add(tmp);
	    			}
	    			if(z==3)
	    			{
	    				bricks3.add(tmp);
	    			} 
	    			if(tmp.size()==3) //if the the brick segment is 3 or 5 bricks long, it will include a question block directly in the middle
	    			{
	    				tmp.get(1).setQuestion(true);
	    			}
	    			if(tmp.size()==5)
	    			{
	    				tmp.get(2).setQuestion(true);
	    			}
	    		}
	    		else
	    		{	
	    			sameSpot = false;
	    		}
	    	}
    	}
    }
    
    public void loadMushrooms()
    {
    	for(int z=1; z<4; z++)
    	{
    		int r;
	    	int x;
	    	int rground;
	    	boolean sameSpot = false;
	    	Random rand = new Random();
	    	ArrayList<platform> currList = new ArrayList<platform>();
	    	if(z==1)
	    	{
	    		currList = platforms;
	    	}
	    	if(z==2)
	    	{
	    		currList = platforms2;
	    	}
	    	if(z==3)
	    	{
	    		currList = platforms3;
	    	}
	    		
			for(platform p : currList)
			{
				if(p.getSomethingOn() == false)
				{
					r = rand.nextInt(12);
					if(r == 1)
					{
						x = rand.nextInt(p.getSizeX() - 30);
						if(z==1)
						{
							mushrooms.add(new mushroom(p.getX() + x,p.getY() - 30,30,30,p.getX(),p.getX()+p.getSizeX()-30,false,false,true));
						}
						if(z==2)
						{
							mushrooms2.add(new mushroom(p.getX() + x,p.getY() - 30,30,30,p.getX(),p.getX()+p.getSizeX()-30,false,false,true));
						}
						if(z==3)
						{
							mushrooms3.add(new mushroom(p.getX() + x,p.getY() - 30,30,30,p.getX(),p.getX()+p.getSizeX()-30,false,false,true));
						}
						p.setSomethingOn(true);	
					}
				}
    		}
    	}
    }
    
    public void checkBrickCollide()
    {
    	for(ArrayList<brick> b2: currBricks)
    	{
    		for(brick b: b2)
    		{
    			Rectangle bLeft = new Rectangle(b2.get(0).getX(), b2.get(0).getY()+1,1,38); //left side of the brick segment
    			Rectangle bRight = new Rectangle(b2.get(b2.size()-1).getX()+40, b2.get(b2.size()-1).getY()+1,1,38); //right side of the brick segment
    			
    			Rectangle brickBot = new Rectangle(b.getX()+2,b.getY()+40,36,1);;
		    	Rectangle brickTop = new Rectangle(b.getX(),b.getY(),40, 1);
		    	
		    	Rectangle marioBot = new Rectangle(mario.getX(),mario.getY()+mario.getHeight(),mario.getWidth(),10);
		    	Rectangle marioLeft = new Rectangle(mario.getX(),mario.getY(),10,mario.getHeight());
		    	Rectangle marioRight = new Rectangle(mario.getX()+mario.getWidth()-10,mario.getY(),10,mario.getHeight());
		    	Rectangle marioTop = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),10);
		    	
		    	Rectangle guy = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
		    	if(!b.getBroken())
		    	{
		    		if(bLeft.intersects(guy)){
		        		collideL = true;
		        		mario.setVY(5);	
		        		mario.setJump(true);
		        	}
			        else if(bRight.intersects(guy)){
			        	collideR = true;
			        	mario.setVY(5);
			        	mario.setJump(true);
			        }
			        if(brickBot.intersects(guy))
			        {
			        	if(marioBig) //if mario is big and he collides with the underside of a normal brick, he breaks it
			        	{
			        		if(!b.getQuestion()) //if it is a normal brick
			        		{
		        				mario.setVY(5);
				        		mario.setJump(true);
				        		b.setBroken(true);
				        		b.setVY(-10);
			        		}
			        		else //if it is a question brick
			        		{
			        			mario.setJump(true);
				        		mario.setVY(5);
				        		return;
			        		}
			        	}
			        	else
			        	{
			        		mario.setJump(true);
			        		mario.setVY(5);
			        	}
			        }
			        else if(brickTop.intersects(guy))
			        {
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
    }
	
    public void checkPlatformCollide()
    {
    	boolean onPlatform = false;
    	for(platform p : currPlatforms)
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
    
    public void checkCoinCollide()
    {
		for(coin c : currCoins)
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

    	for(goomba g : currGoombas)
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
	    				if(marioBig && !invE) //if mario is big mario or he is not in the animation of growing into big mario
	    				{
	    					shrink();
    					}
    					else if(invincible == false)
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
 
    public void checkSpinyCollide()
    {
    	for(spiny s : currSpinys)
    	{
			Rectangle m = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
			Rectangle f = new Rectangle(fball.getX(),fball.getY(),fball.getSizeX(),fball.getSizeY());
			Rectangle spinyRect = new Rectangle(s.getX(),s.getY(),s.getSizeX(),s.getSizeY());
    		if(m.intersects(spinyRect))
    		{
    			if(s.getKilled()==false)
    			{
    				if(marioBig && !invE)
    				{
    					shrink();
    					getTmp();
					}
					else if(invincible == false)
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
    
    public void checkBillCollide()
    {
    	System.out.println(currBills.size());
    	for(bulletBill bb: currBills)
    	{
    		Rectangle m = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
			Rectangle f = new Rectangle(fball.getX(),fball.getY(),fball.getSizeX(),fball.getSizeY());
			Rectangle billRect = new Rectangle(bb.getX(),bb.getY(),bb.getSizeX(),bb.getSizeY());
			
    		if(m.intersects(billRect))
    		{
    			System.out.println(true);
    			if(bb.getKilled()==false)
    			{
    				if(mario.getVY() >= 2)
    				{
    					bb.setKilled(true);
    					mario.setVY(-10);
    				}
    				else if(marioBig && !invE)
    				{
    					shrink();
    					getTmp();
					}
					else if(invincible == false)
					{
    					lives -= 1;
						inv = true;
					}
    			}
    		}
    		else if(f.intersects(billRect))
    		{
    			if(bb.getKilled()==false)
    			{
    				if(fball.getUsed() == true)
    				{
        				bb.setKilled(true);
    				}
    			}
    		}
    	}
    }
    
    public void checkMushroomCollide()
    {
    	for(mushroom mu : currMushrooms)
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
    	for(marioShroom mushroom: currMarioMushrooms)
    	{
			Rectangle m = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
    		Rectangle shroom = new Rectangle(mushroom.getX(),mushroom.getY(),40,40);
    		if(m.intersects(shroom) && mushroom.getCond())
    		{
    			if(mushroom.getVY() != 0 || mushroom.getGround())
    			{
    				mushroom.setCond(false);
    				if(!marioBig  && !invE)
    				{
    					grow();
			    		getTmp();	
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
				frames = 0;
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
				if(intermissionNum == 1)
				{
					screen = "intermission";
				}
				if(intermissionNum == 2)
				{
					screen = "intermission2";
				}
			}
			if(store1.contains(mouse))
			{
				if(totalCoins >= 5)
				{
					if(marioBig == false)
					{
						grow();
					}
					totalCoins -= 5;
				}
			}
			if(store2.contains(mouse))
			{
				if(totalCoins >= 10)
				{
					lives += 5;
					totalCoins -= 10;
				}
			}
			if(store3.contains(mouse))
			{
				if(totalCoins >= 25)
				{
					fireflowerPower = true;
					totalCoins -= 25;
				}
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
			g.drawImage(lifeIconPic, 5, 7, null); 
			g.drawImage(coinIconPic, 10, 40, null);
			if(marioBig == true)
			{
				g.drawImage(marioShroomIconPic, 10, 73, null);
			}
			if(fireflowerPower == true)
			{
				g.drawImage(fireFlowerIconPic, 40, 73, null);
			}
			g.setFont(marioFont);
			g.drawString("x"+Integer.toString(lives), 37, 35);
			g.drawString(Integer.toString(totalCoins), 33, 69);
    	}
    	else
    	{
    		Rectangle marioRect = new Rectangle(mario.getX(), mario.getY(), mario.getWidth(),mario.getHeight());
    		g.drawImage(currBack,currBackX,0,null);
    		
    		for(platform p : currPlatforms)
			{
				Color platBottomColor = new Color(213,132,22);
				g.setColor(platBottomColor);  
				g.fillRect(p.getX(),p.getY()+10,p.getSizeX(),545 - p.getY());
			}
			for(platform p : currPlatforms)	
			{
				Color platTopColor = new Color(67,144,0);
				g.setColor(platTopColor);  
				g.fillRect(p.getX(),p.getY(),p.getSizeX(),p.getSizeY());
			}
			for(coin c : currCoins)
			{
				if(c.getCollected() == false)
				{
					g.drawImage(coinPic, c.getX(), c.getY(), null);
				}
			}
			for(ArrayList<brick> b2: currBricks)
			{
				for(int i=0; i<b2.size(); i++)
				{
					Rectangle m = new Rectangle(mario.getX(), mario.getY(), mario.getWidth(),10);
					Rectangle brickBot = new Rectangle(b2.get(i).getX()+4,b2.get(i).getY()+40,32,1);
					
					if(m.intersects(brickBot)) 
					{
						if(!marioBig)//if mario is small and hit hits the bottom of a brick segment, he pushes it up a bit
						{
							g.drawImage(brickPic,b2.get(i).getX(),b2.get(i).getY()-15,null);	
						}
						if(b2.get(i).getQuestion())
						{
							g.drawImage(questionPic,b2.get(i).getX(),b2.get(i).getY()-15,null);
							if(!b2.get(i).getShroom())
							{
								if(screen == "level1")
								{
									marioMushrooms.add(new marioShroom(b2.get(i).getX(), b2.get(i).getY()-45, 0));	
								}
								if(screen == "level2")
								{
									marioMushrooms2.add(new marioShroom(b2.get(i).getX(), b2.get(i).getY()-45, 0));	
								}
								if(screen == "level3")
								{
									marioMushrooms3.add(new marioShroom(b2.get(i).getX(), b2.get(i).getY()-45, 0));	
								}
								for(brick b: b2)
								{
									if(!b.getBroken())
									{
										currBrick.add(b);
									}
								}
								b2.get(i).setShroom(true);
							}	
						}
						
						if(i==1 && b2.size()==3) //3 brick segments
						{
							g.drawImage(questionPic,b2.get(1).getX(),b2.get(1).getY()-15,null);
							if(!b2.get(i).getShroom())
							{
								marioMushrooms.add(new marioShroom(b2.get(1).getX(), b2.get(1).getY()-45, 0));
								currBrick = b2;
								b2.get(i).setShroom(true);
							}
						}
						if(i==2 && b2.size()==5) //5 brick segments
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
					if(b2.get(i).getBroken()) //if mario is big and he collides with the bottom of a normal brick, he breaks it instead
					{
						if(b2.get(i).getY()<555)
						{  
							g.drawImage(brickSegments[0], b2.get(i).getX()-b2.get(i).getVX(), b2.get(i).getY(), null); //top left piece
							g.drawImage(brickSegments[1], b2.get(i).getX()+20+b2.get(i).getVX(), b2.get(i).getY(), null); //top right piece
							g.drawImage(brickSegments[2], b2.get(i).getX()-b2.get(i).getVX(), b2.get(i).getY()+20, null); //bottom left piece 
							g.drawImage(brickSegments[3], b2.get(i).getX()+20+b2.get(i).getVX(), b2.get(i).getY()+20, null); //bottom right piece
							b2.get(i).addY(b2.get(i).getVY()); //basically making the brick pieces "jump" 
							b2.get(i).addVY(1); 
							b2.get(i).addVX(1);
						}
					}	
					else if(!m.intersects(brickBot))
					{
						g.drawImage(brickPic,b2.get(i).getX(),b2.get(i).getY(),null);
						if(b2.get(i).getQuestion())
						{
							g.drawImage(questionPic,b2.get(i).getX(),b2.get(i).getY(),null);
						}
						
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
			for(marioShroom mushroom : currMarioMushrooms)
	    	{
	    		if(mushroom.getCond())
	    		{
	    			g.drawImage(shroomPic, mushroom.getX()+5,mushroom.getY()+10,null);
    				mushroom.addX(3);
	    		}
				int dx = currBrick.get(currBrick.size()-1).getX()+40 - currBrick.get(0).getX();
				Rectangle mushRect = new Rectangle(mushroom.getX(), mushroom.getY(), 40, 40);
				g.setColor(Color.red);
				//g.drawRect(mushRect.x, mushRect.y, mushRect.width, mushRect.height);
				if(mushroom.getX() > currBrick.get(0).getX()+dx && mushroom.getGround() == false)
				{		
    				mushroom.setVY(10);
    			}
    			if(mushroom.getY()>=ground)
				{
					mushroom.setGround(true);
    				mushroom.setVY(0);
    				mushroom.setY(515);
				}
					mushroom.addY(mushroom.getVY());
    		}
    		for(goomba gb : currGoombas)
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
					gb.addFrames(1);
					if(gb.getFrames()==30)
					{
						gb.addFrames(-gb.getFrames()); // reset frames
					}
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
	
			for(mushroom m : currMushrooms)
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
			for(spiny s : currSpinys)
			{	
				if(!s.getKilled())
				{
					s.draw(g);
				}
				else
				{
					if(s.getLeft())
					{ 
						if(s.getKillTimer()<=15) // dead goomba appears for a bit
						{
							g.drawImage(s.getDeadImage(0),s.getX(),s.getY(),null);
							s.setKillTimer(s.getKillTimer()+1);
						}
	
					}
					if(s.getRight())
					{ 
						if(s.getKillTimer()<=15)
						{
							g.drawImage(s.getDeadImage(1),s.getX(),s.getY(),null);
							s.setKillTimer(s.getKillTimer()+1);
						}
	
					}
				}
				Rectangle m = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
				g.drawRect(m.x,m.y,m.width,m.height);
				for(bulletBill bb : currBills)
				{
					if(bb.getKilled())
					{
						if(bb.getY()<555)
						{
							bb.addY(2);
							bb.draw(g);
						}
					}

					if(bb.getCD() && !bb.getKilled())
					{
						bb.draw(g);	//drawing the bullet bills	
					}
					g.drawImage(bb.getImage(), bb.getBX(), bb.getBY(), null); //drawing blaster
					if(Math.abs(bb.getX()-bb.getBX())>= 400)
					{
						bb.setCD(false);
						bb.setX(bb.getBX()); //reset the bullet bill to its blaster
						bb.setY(bb.getBY()+15);
						bb.setKilled(false);
					}
				}
			}
			
			if(collide) //when mario has hit a red mushroom or an enemy, he grows/shrinks
			{
				if(marioBig) //mario grows
				{
					if(evolveCD<=25)
					{
						g.drawImage(tmp.get(0), mario.getX(), mario.getY()+13, null);
						evolveCD++;
					}
					if(evolveCD>=25 && evolveCD<=50)
					{
						g.drawImage(tmp.get(1), mario.getX(), mario.getY()+6, null);
						evolveCD++;
					}
					if(evolveCD>=50 && evolveCD<=75)
					{
						if(shiftRight)
						{
							g.drawImage(marioBigRightWalkPics.get(frames),mario.getX(),mario.getY(),null);
						}
						if(shiftLeft)
						{
							g.drawImage(marioBigLeftWalkPics.get(frames),mario.getX(),mario.getY(),null);
						}
						evolveCD++;
					}
					if(evolveCD==75)
					{
						evolveCD = 0;
						collide = false;
					}
					if(shiftLeft)
					{
						if(left)
						{
							currPic = marioBigLeftWalkPics.get(frames);
						}
						else
						{
							currPic = marioBigLeftWalkPics.get(0);
						}
					}
					if(shiftRight)
					{
						if(right)
						{
							currPic = marioBigRightWalkPics.get(frames);
						}
						else
						{
							currPic = marioBigRightWalkPics.get(0);
						}
					}
				}
				else //mario shrinks
				{
					if(evolveCD<=25)
					{
						g.drawImage(tmp.get(1), mario.getX(), mario.getY()-13, null);
						evolveCD++;
					}
					if(evolveCD>=25 && evolveCD<=50)
					{
						g.drawImage(tmp.get(0), mario.getX(), mario.getY()-6, null);
						evolveCD++;
					}
					if(evolveCD>=50 && evolveCD<=75)
					{
						if(shiftRight)
						{
							if(right)
							{
								g.drawImage(marioRightWalkPics.get(frames),mario.getX(),mario.getY(),null);
							}
							else
							{
								g.drawImage(marioRightWalkPics.get(0),mario.getX(),mario.getY(),null);
							}
						}
						if(shiftLeft)
						{
							if(left)
							{
								g.drawImage(marioLeftWalkPics.get(frames),mario.getX(),mario.getY(),null);
							}
							else
							{
								g.drawImage(marioLeftWalkPics.get(0),mario.getX(),mario.getY(),null);
							}
						}
						evolveCD++;
					}
					if(evolveCD==75)
					{
						evolveCD = 0;
						collide = false;
						mario.setVY(evolveVY);
					}
					
					if(shiftLeft)
					{
						if(left)
						{
							currPic = marioLeftWalkPics.get(frames);
						}
						else
						{
							currPic = marioLeftWalkPics.get(0);
						}
					}
					if(shiftRight)
					{
						if(right)
						{
							currPic = marioRightWalkPics.get(frames);
						}
						else
						{
							currPic = marioRightWalkPics.get(0);
						}
					}
				}
			}
			
			if(!collide)
			{
				if(!right && !left)
				{
					if(marioBig)
					{
		        		g.drawImage(currPic, mario.getX(), mario.getY(), null);
					}
					else
					{
		        		g.drawImage(currPic, mario.getX(), mario.getY(), null);
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
		            	g.drawImage(marioRightWalkPics.get(frames),mario.getX(),mario.getY(),null);
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
		            	g.drawImage(marioLeftWalkPics.get(frames),mario.getX(),mario.getY(),null);	
					}
		        }
		        frames++;
				if(frames==20)
				{
					frames=0;	
				}
			}
			
			g.setColor(Color.white);
			g.drawImage(lifeIconPic, 5, 7, null); 
			g.drawImage(coinIconPic, 10, 40, null);
			if(marioBig == true)
			{
				g.drawImage(marioShroomIconPic, 10, 73, null);
			}
			if(fireflowerPower == true)
			{
				g.drawImage(fireFlowerIconPic, 40, 73, null);
			}
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
	private int X;
	private int Y;
	private int sizeX;
	private int sizeY;
	private boolean shroom = false;
	private boolean broken = false; 
	private boolean question = false;
	private int VY,VX;

	public brick(int X1, int Y1, int sizeX1, int sizeY1)
	{
		X = X1;
		Y = Y1;
		sizeX = sizeX1;
		sizeY = sizeY1;
	}
	
	public int getX()
	{
		return X;
	}
	
	public int getY()
	{
		return Y;
	}
	
	public int getVY()
	{
		return VY;
	}
	
	public int getVX()
	{
		return VX;
	}
	
	public boolean getShroom()
	{
		return shroom;
	}
	
	public boolean getBroken()
	{
		return broken;
	}
	
	public boolean getQuestion()
	{
		return question;
	}
	
	public void addX(int n)
	{
		X += n;
	}
	
	public void addY(int n)
	{
		Y += n;
	}
	
	public void addVY(int n)
	{
		VY += n;
	}
	
	public void addVX(int n)
	{
		VX += n;
	}
	
	public void setVY(int n)
	{
		VY = n;
	}
	
	public void setVX(int n)
	{
		VX = n;
	}
	
	public void setShroom(boolean bool)
	{
		shroom = bool;
	}
	
	public void setBroken(boolean bool)
	{
		broken = bool;
	}
	
	public void setQuestion(boolean bool)
	{
		question = bool;
	}
}

class marioShroom
{
	private int X;
	private int Y;
	private int VY;
	private Image shroomPic;
	private boolean onGround = false;
	private boolean cond = true;
	
	public marioShroom(int X1, int Y1, int VY1)
	{
		X = X1;
		Y = Y1;
		VY = VY1;
		shroomPic = new ImageIcon("Mariopics/redShroom.png").getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
	}
	
	public int getX()
	{
		return X;
	}
	
	public int getY()
	{
		return Y;
	}
	
	public int getVY()
	{
		return VY;
	}
	
	public boolean getGround()
	{
		return onGround;
	}
	
	public boolean getCond()
	{
		return cond;
	}
	
	public void addX(int X1)
	{
		X += X1;
	}
	
	public void addY(int Y1)
	{
		Y += Y1;
	}
	
	public void setVY(int VY1)
	{
		VY = VY1;
	}
	
	public void setY(int Y1)
	{
		Y = Y1;
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
	private ArrayList<Image> spinyLeftPics = new ArrayList<Image>();
	private ArrayList<Image> spinyRightPics = new ArrayList<Image>();
	private ArrayList<Image> spinyDeadPics = new ArrayList<Image>();
	
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
	 	for(int i=0; i<10; i++)
        {
        	Image tmp = new ImageIcon("MarioPics/spinyWalk" +Integer.toString(i)+".png").getImage().getScaledInstance(sizeX, sizeY,Image.SCALE_SMOOTH);
        	for(int x=0;x<6;x++)
        	{
        		if(i<5)
        		{
        			spinyLeftPics.add(tmp);
        		}
        		else
        		{
        			spinyRightPics.add(tmp);
        		}
        	}
    		
        }
        for(int i=0; i<2; i++)
        {
        	spinyDeadPics.add(new ImageIcon("MarioPics/deadspiny" +Integer.toString(i)+".png").getImage().getScaledInstance(sizeX, sizeY,Image.SCALE_SMOOTH));
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
		return spinyRightPics.get(frames);
	}
	
	public Image getLeftImage()
	{
		return spinyLeftPics.get(frames);
	}
	
	public Image getDeadImage(int n)
	{
		return spinyDeadPics.get(n);
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
	
	public void draw(Graphics g)
	{
		if(right)
		{
			g.drawImage(spinyRightPics.get(frames),X,Y,null);
		}
		else
		{
			g.drawImage(spinyLeftPics.get(frames),X,Y,null);
		}
		frames++;
		if(frames==30)
		{
			frames=0;
		}
	}
}
class bulletBill
{
	private int X,Y, bX, bY, sizeX, sizeY;
	private boolean left = false;
	private boolean right = false;
	private Image []rightPics = new Image[4];
	private Image []leftPics = new Image[4];
	private Image leftPic, rightPic, blaster;
	private boolean killed = false;
	private boolean cooldown;

	public bulletBill(int x1, int y1, int bX1, int bY1, int sx1, int sy1, boolean c)
	{
		X = x1;
		Y = y1;
		sizeX = sx1;
		sizeY = sy1;
		bX = bX1;
		bY = bY1;
		blaster = new ImageIcon("MarioPics/blaster2.png").getImage().getScaledInstance(60, 70,Image.SCALE_SMOOTH);
		leftPic = new ImageIcon("MarioPics/leftBill.png").getImage().getScaledInstance(sizeX, sizeY,Image.SCALE_SMOOTH);
		rightPic = new ImageIcon("MarioPics/rightBill.png").getImage().getScaledInstance(sizeX, sizeY,Image.SCALE_SMOOTH);
		cooldown = c;
	}
	
	public int getX()
	{
		return X;
	}
	
	public int getBX()
	{
		return bX;
	}
	
	public int getY()
	{
		return Y;
	}
	
	public int getBY()
	{
		return bY;
	}
	
	public int getSizeX()
	{
		return sizeX;
	}
	
	public int getSizeY()
	{
		return sizeY;
	}
	
	public boolean getLeft()
	{
	    return left;
	}
	
	public boolean getRight()
	{
	    return right;
	}
	
	public boolean getKilled()
	{
	    return killed;
	}
	
	public boolean getCD()
	{
	    return cooldown;
	}
	
	public Image getImage()
	{
		return blaster;
	}
	
	public Image getImageRight(int n)
	{
		return rightPics[n];
	}
	
	public Image getImageLeft(int n)
	{
		return leftPics[n];
	}
	
	public void addX(int num)
	{
		X += num;
	}
	
	public void addY(int num)
	{
		Y += num;
	}
	
	public void addBX(int num)
	{
		bX += num;
	}
	
	public void setX(int n)
	{
		X = n;
	}
	
	public void setY(int n)
	{
		Y = n;
	}  
	
	public void setLeft(boolean b)
	{
	    left = b;
	}
	
	public void setRight(boolean b)
	{
	    right = b;
	}
	
	public void setKilled(boolean b)
	{
	    killed = b;
	}
	
	public void setCD(boolean b)
	{
	    cooldown = b;
	}
	
	public void draw(Graphics g)
	{
		if(right)
		{
			g.drawImage(rightPic, X+5, Y, null);
		}
		if(left)
		{
			g.drawImage(leftPic, X-15, Y, null);
		}
	}
}