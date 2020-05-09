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

class GamePanel extends JPanel implements KeyListener{
	private boolean []keys;
	private MarioRun mainFrame;
	private String screen = "menu";
	
	private Image back, tmp, currPic, coinPic, lifePic, brickPic, questionPic, shroomPic;
	
	private ArrayList<Image>marioLeftWalkPics = new ArrayList<Image>();
	private ArrayList<Image>marioRightWalkPics = new ArrayList<Image>();
	
	private int backX = 0;
	
	private int ground = 555-50;
	
	private int collectedCoins = 0;
	
	private boolean shiftLeft = false;
	private boolean shiftRight = false;
	
	private ArrayList<platform>platforms = new ArrayList<platform>();
	private ArrayList<coin>coins = new ArrayList<coin>();
	private ArrayList<ArrayList<brick>>bricks = new ArrayList<ArrayList<brick>>();
	private ArrayList<brick> qBricks = new ArrayList<brick>();
	private ArrayList<brick> currBrick = new ArrayList<brick>();
	private ArrayList<marioShroom> marioMushrooms = new ArrayList<marioShroom>();
	
	private ArrayList<goomba> goombas = new ArrayList<goomba>();
	
	private int frames;
	
	private boolean jCooldown = false;
	private int jCooldownCount = 0;
	private boolean jumpWait = false;
	
	private boolean inv = false;
	private int invincibleCount = 0;
	private boolean invincible = false;
	
	private int lives = 5;

	private boolean right, left, mShroom;
	
	private Font marioFont;
	
	private boolean collide, collideL, collideR, collideT;
		
	player mario = new player(430,ground,0,false,false,50,25);
	
	public GamePanel(MarioRun m)
	{
		keys = new boolean[KeyEvent.KEY_LAST+1];
		back = new ImageIcon("MarioBackground.png").getImage().getScaledInstance(10000,650,Image.SCALE_SMOOTH);
		coinPic = new ImageIcon("Mariopics/coin.png").getImage().getScaledInstance(20,30,Image.SCALE_SMOOTH);
		lifePic = new ImageIcon("Mariopics/lifeMushroom.png").getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH);
		brickPic = new ImageIcon("Mariopics/mariobrick.png").getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
		questionPic = new ImageIcon("Mariopics/questionblock.png").getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
		shroomPic = new ImageIcon("Mariopics/redShroom.png").getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
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
    	loadBricks();
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
    	if(screen == "game")
    	{
	    	move();
	    	jump();
	    	jumpCooldown();
	    	invincibilityCooldown();
	    	checkCollectedCoins();
	    	checkPlatformCollide();
	    	checkCoinCollide();
	    	checkGoombaCollide();
	    	moveGoombas();
	    	checkBrickCollide();
	    	checkShroomCollide();
	    	//System.out.println(mario.getJump());
    	}
    }
    
	public void menuStart()
	{
		if(keys[KeyEvent.VK_SPACE] )
		{
			screen = "game";
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
	
	public void move()
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
				x = rand.nextInt(p.getSizeX() - 40);
				goombas.add(new goomba(p.getX() + x,p.getY() - 40,40,40,p.getX(),p.getX()+p.getSizeX()-40,true,false,false));
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
    
    public void loadBricks()
    {
    	int x=0;
    	int y=0;
    	boolean sameSpot = false;
    	Random rand = new Random();
    	
    	for(int i=0; i<40; i++)
    	{
    		x = rand.nextInt(9000) + 500;
    		y = 430;
    		for(ArrayList<brick> b2: bricks)
    		{
    			for(brick b: b2)
    			{
    				Rectangle newRect = new Rectangle(x,y,40,40);
    				Rectangle newRect1 = new Rectangle(x+40, y, 40, 40);
    				Rectangle newRect2 = new Rectangle(x+80, y, 40, 40);
    				Rectangle newRect3 = new Rectangle(x+120, y, 40, 40);
    				Rectangle newRect4 = new Rectangle(x+160, y, 40, 40);
    				Rectangle newRect5 = new Rectangle(x+200, y, 40, 40);
    				
	    			Rectangle oldRect = new Rectangle(b.getX(), b.getY(), 40, 40);
	    			/*Rectangle oldRectLeft = new Rectangle(b.getX()-40, b.getY(), 40, 40);
	    			Rectangle oldRectRight = new Rectangle(b.getX()+40, b.getY(), 40, 40);
	    			Rectangle oldRectLeft2 = new Rectangle(b.getX()-80, b.getY(), 40, 40);
	    			Rectangle oldRectRight2 = new Rectangle(b.getX()+80, b.getY(), 40, 40);
	    			Rectangle oldRectLeft3 = new Rectangle(b.getX()-120, b.getY(), 40, 40); 
	    			Rectangle oldRectRight3 = new Rectangle(b.getX()+120, b.getY(), 40, 40);*/
	    			
	    			if(newRect.intersects(oldRect) || newRect1.intersects(oldRect) || newRect2.intersects(oldRect) || newRect3.intersects(oldRect) || newRect4.intersects(oldRect) || newRect5.intersects(oldRect))
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
    			System.out.println(tmp);
    			
    			if(tmp.size()==3)
    			{
    				qBricks.add(tmp.get(1)); 
    			}
    			if(tmp.size()==5)
    			{
    				qBricks.add(tmp.get(2));
    			}
    			//System.out.println(qBricks);
    		}
    		else
    		{	
    			sameSpot = false;
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
		        	//collide = true;
		        }
		        if(brickTop.intersects(guy)){
		        	//collideT = true;
		        	mario.setY(b.getY()-50);
		        	mario.setVY(0);
		        	mario.setJump(false);
		        }
		        if(collideR)
		        {
		        	 System.out.println(collideR);
		        }
		       
		        if(mario.getY()==ground || mario.getY()<= b.getY()-50)
		        {
		        	collide = false;
		        	collideL = false;
		        	collideR = false;
		        }
    		}
    	}  	
    }
    
    public void checkPlatformCollide()
    {
    	boolean onPlatform = false;
    	for(platform p : platforms)
    	{
			Rectangle m = new Rectangle(mario.getX()-5,mario.getY()+25,mario.getWidth()+10,mario.getHeight()-25); //player has 10 pixel clearance for x position
			Rectangle plat = new Rectangle(p.getX(),p.getY(),p.getSizeX(),p.getSizeY());
			if(mario.getVY() >= 0 && mario.getJump() == true)
			{
	    		if(m.intersects(plat))
	    		{
	    			onPlatform = true;
					mario.setY(p.getY()-50);
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
    
    public void checkShroomCollide() // IN PROGRESS
    {
    	for(marioShroom mushroom: marioMushrooms)
    	{
    		Rectangle m = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
    		Rectangle shroom = new Rectangle(mushroom.getX(),mario.getY(),40,40);
    		if(m.intersects(shroom))
    		{
    			if(mushroom.getVY() != 0 || mushroom.getGround())
    			{
    				mushroom.setCond(false);
	    			mario.setHeight(70);
	    			mario.setWidth(40);
	    			for(Image pic : marioRightWalkPics)
	    			{
	    			//	marioRightWalkPics.add(pic.getScaledInstance(mario.getHeight(),mario.getWidth(),Image.SCALE_SMOOTH));
	    			//	marioRightWalkPics.remove(pic);
	    			}
	    			for(Image pic : marioLeftWalkPics)
	    			{
	    				//marioRightWalkPics.add(pic.getScaledInstance(mario.getHeight(),mario.getWidth(),Image.SCALE_SMOOTH));
	    				//marioRightWalkPics.remove(pic);
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
    	if(screen == "menu")
    	{
    		g.setColor(Color.red);
	    	g.fillRect(0,0,getWidth(),getHeight()); //background
    	}
    	if(screen == "game")
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
					g.setColor(Color.yellow);  
					g.fillRect(c.getX(),c.getY(),c.getSizeX(),c.getSizeY());
				}
			}
			for(ArrayList<brick> b2: bricks)
			{
				for(int i=0; i<b2.size(); i++)
				{
					g.setColor(Color.red);
					Rectangle m = new Rectangle(mario.getX(), mario.getY(), mario.getWidth(),10);
					g.drawRect(m.x,m.y,m.width,m.height);
					Rectangle brickBot = new Rectangle(b2.get(i).getX()+4,b2.get(i).getY()+40,32,1);
					g.drawRect(brickBot.x,brickBot.y,brickBot.width,brickBot.height);
					if(m.intersects(brickBot))
					{
						g.drawImage(brickPic,b2.get(i).getX(),b2.get(i).getY()-15,null);
						if(i==1 && b2.size()==3)
						{
							g.drawImage(questionPic,b2.get(1).getX(),b2.get(1).getY()-15,null);
							marioMushrooms.add(new marioShroom(b2.get(1).getX(), b2.get(1).getY()-45, 0));
							currBrick = b2;
							b2.get(i).setShroom(true);
						}
						if(i==2 && b2.size()==5)
						{
							g.drawImage(questionPic,b2.get(2).getX(),b2.get(2).getY()-15,null);
							marioMushrooms.add(new marioShroom(b2.get(2).getX(), b2.get(2).getY()-45, 0));
							currBrick = b2;
							b2.get(i).setShroom(true);
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
					
					/*Rectangle brick = new Rectangle(b.getX(),b.getY()+40,40,1);
			    	Rectangle brickLeft = new Rectangle(b.getX(),b.getY(),1,39);
			    	Rectangle brickRight = new Rectangle(b.getX()+40,b.getY(),1,39);
			    	Rectangle brickTop = new Rectangle(b.getX(),b.getY(),40, 1);
					g.drawRect(brick.x, brick.y, brick.width, brick.height);
					g.drawRect(brickLeft.x, brickLeft.y, brickLeft.width, brickLeft.height);
					g.drawRect(brickRight.x, brickRight.y, brickRight.width, brickRight.height);
					g.drawRect(brickTop.x, brickTop.y, brickTop.width, brickTop.height);*/
					
    			Rectangle bLeft = new Rectangle(b2.get(0).getX(), b2.get(0).getY(),1,40); //left side of the brick segment
    			Rectangle bRight = new Rectangle(b2.get(b2.size()-1).getX()+40, b2.get(b2.size()-1).getY(),1,40); 
		    			
		    		//g.drawRect(bLeft.x,bLeft.y,bLeft.width,bLeft.height);
		    		//g.drawRect(bRight.x,bRight.y,bRight.width,bRight.height);
				}
			}
			
			/*for(brick b: qBricks)
    		{
	    		Rectangle questionB = new Rectangle(b.getX(), b.getY()+40, 40, 1);
	    		Rectangle m = new Rectangle(mario.getX(), mario.getY(), mario.getWidth(), mario.getHeight());
	    		if(m.intersects(questionB) && !b.getShroom())
	    		{
	    			marioMushrooms.add(new marioShroom(b.getX(), b.getY()-40, 0));	
	    			b.setShroom(true);
	    		}
    		}*/
    		
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
				if(gb.getFrames()==30){
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
			g.drawImage(lifePic, 5, 7, null); 
			g.drawImage(coinPic, 10, 40, null);
			g.setFont(marioFont);
			g.drawString("x"+Integer.toString(lives), 37, 35);
			g.drawString(Integer.toString(collectedCoins), 33, 69);
			
			Rectangle marioBot = new Rectangle(mario.getX(),mario.getY()+mario.getHeight()-5,mario.getWidth(),10);
		    	Rectangle marioLeft = new Rectangle(mario.getX(),mario.getY(),10,mario.getHeight());
		    	Rectangle marioRight = new Rectangle(mario.getX()+mario.getWidth()-10,mario.getY(),10,mario.getHeight());
		    	Rectangle marioTop = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),10);
		    	
		    //g.drawRect(marioBot.x,marioBot.y,marioBot.width,marioBot.height);
		    //drawRect(marioLeft.x,marioLeft.y,marioLeft.width,marioLeft.height);
		    //drawRect(marioRight.x,marioRight.y,marioRight.width,marioRight.height);
		    //g.drawRect(marioTop.x,marioTop.y,marioTop.width,marioTop.height);
		    
		    
		    Rectangle guy = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
		    g.drawRect(guy.x,guy.y,guy.width,guy.height);
		    
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
	private Image[] marioEvolvePics = new Image[5];
	
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