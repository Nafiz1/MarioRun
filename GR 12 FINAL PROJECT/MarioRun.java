//MarioRun.java
//Nafiz Hasan and Ashad Ahmed
//This program is a Mario-inspired game in which the user has to get Mario through 3 randomly generated levels and onto the 4th level in which Bowser has held Princess
//Peach captive. In each level, the user will find platforms and the classic Mario bricks in which the user can obtain a red mushroom to transform into big Mario. For enemies, 
//level 1 has goombas, level two has goombas and spinys, level 3 has goombas, spinys, and bullet bills. Lastly, level 4 obviously has Bowser. The user starts with 5 lives,
//and if the user loses all lives they have to to restart the game. The user can also aqcuire green mushrooms that are randomly generated in the first 3 levels to gain lives. Coins are also randomly generated 
//within the first three levels, and can be used to purchase items in the shop. A chance to enter the shop is present at the end of every level. 
//The user can buy red mushrooms for 5 coins, green mushrooms for 10 coins, and fire flowers which allow Mario to shoot fireballs for 25 coins. 

//importing
import java.util.*;
import java.io.*;
import java.applet.*;
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
		setLocationRelativeTo(null); //Centralizes the JFrame
    }
	
	public void start()
	{
		myTimer.start();
	}

	public void actionPerformed(ActionEvent evt)
	{
		game.update(); //updates all methods
		game.repaint();
	}

    public static void main(String[] arguments)
    {
    	GameMusic music = new GameMusic(); //loads the mario music
		MarioRun frame = new MarioRun();
    }
}

class GamePanel extends JPanel implements KeyListener, MouseListener
{
	private String screen = "level4";
	
	//Game Data------------------------
	private boolean []keys;
	private MarioRun mainFrame;
	private Point mouse;
	private Rectangle menuPlay;
	private Rectangle menuInstructions;
	private Rectangle menuCredits;
	private Rectangle intermissionStore;
	private Rectangle intermissionNext;
	private Rectangle store1;
	private Rectangle store2;
	private Rectangle store3;
	private Rectangle storePrev;
	private Rectangle exit;
	private int totalCoins;
	private Image backbtn, storebtn, instructionsbtn, nextbtn, creditsbtn, startbtn, exitbtn;
	private Image backbtnDown, storebtnDown, instructionsbtnDown, nextbtnDown, creditsbtnDown, startbtnDown, exitbtnDown;
	private Image menuback, menuinstructions, menucredits, intermissionback, intermissionback2, storeback, exitBack, exitText, currPic, coinIconPic, lifeIconPic, coinPic, mushroomPic, fireFlowerIconPic, marioShroomIconPic, fireballLPic, fireballRPic, brickPic, shroomPic, questionPic, emptyQuestionPic;
	private ArrayList<Image>marioLeftWalkPics = new ArrayList<Image>();
	private ArrayList<Image>marioRightWalkPics = new ArrayList<Image>();
	private ArrayList<Image>marioBigLeftWalkPics = new ArrayList<Image>();
	private ArrayList<Image>marioBigRightWalkPics = new ArrayList<Image>();
	private ArrayList<Image>tmp = new ArrayList<Image>();
	private Image[] brickSegments = new Image[4];
	private int ground = 555-50;
	private int collectedCoins = 0;
	private boolean shiftLeft = false;
	private boolean shiftRight = false;
	private boolean collide, invE;
	private boolean coinCollide = false;
	private int coinCount = 0; 
	private brick tmpBrick;
	private ArrayList<brick> currBrick = new ArrayList<brick>();
	private ArrayList<ArrayList<Image>> evolvePicsRight = new ArrayList<ArrayList<Image>>();
	private ArrayList<ArrayList<Image>> evolvePicsLeft = new ArrayList<ArrayList<Image>>();
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
	
	//Sound Effects------------------------
	AudioClip sound;
	File clearwavFile = new File("sounds/clear.wav");
	File oneupwavFile = new File("sounds/1up.wav");
	File breakwavFile = new File("sounds/break.wav");
	File damagewavFile = new File("sounds/damage.wav");
	File firewavFile = new File("sounds/fire.wav");
	File startwavFile = new File("sounds/start.wav");
	
	//Current Data------------------------ This is for deciding which enemies, pictures, etc. are to be used in each level
	player mario = new player(430,ground,0,false,false,50,25);
	fireball fball = new fireball(mario.getX(),mario.getY(),40,30,false,false,false);
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
	private ArrayList<BufferedImage> currPlatPics = new ArrayList<BufferedImage>();
	private ArrayList<BufferedImage> currPlatTopPics = new ArrayList<BufferedImage>();
	
	//Each level consists of at least some platforms, coins, 1up mushrooms, goombas, bricks, question blocks,
	//and red mushrooms. Level 2 introduces spinys and level 3 introduces bullet bills.
	
	//Level1------------------------
	private Image back;
	private int backX = -20;
	private ArrayList<platform>platforms = new ArrayList<platform>();
	private ArrayList<coin>coins = new ArrayList<coin>();
	private ArrayList<mushroom>mushrooms = new ArrayList<mushroom>(); //green mushrooms
	private ArrayList<goomba> goombas = new ArrayList<goomba>();
	private ArrayList<ArrayList<brick>>bricks = new ArrayList<ArrayList<brick>>();
	private ArrayList<marioShroom> marioMushrooms = new ArrayList<marioShroom>(); //red mushrooms
	private ArrayList<BufferedImage> platPics = new ArrayList<BufferedImage>();
	private ArrayList<BufferedImage> platTopPics = new ArrayList<BufferedImage>();
	private BufferedImage platPic;
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
	private ArrayList<BufferedImage> platPics2 = new ArrayList<BufferedImage>();
	private ArrayList<BufferedImage> platTopPics2 = new ArrayList<BufferedImage>();
	private BufferedImage platPic2;
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
	private ArrayList<BufferedImage> platPics3 = new ArrayList<BufferedImage>();
	private ArrayList<BufferedImage> platTopPics3 = new ArrayList<BufferedImage>();
	private BufferedImage platPic3;
	private BufferedImage platTopPic;
	//Level4------------------------
	private Image back4;
	private int backX4 = -20;
	boss bowser = new boss(650, 555-70, 70, 70, 100);
	
	public GamePanel(MarioRun m)
	{
		addMouseListener(this);
		
		//button rectangles
		menuPlay = new Rectangle(590,200,200,50);
		menuInstructions = new Rectangle(590,300,200,50);
		menuCredits = new Rectangle(590,400,200,50);
		intermissionStore = new Rectangle(100,200,200,50);
		intermissionNext = new Rectangle(590,200,200,50);
		storePrev = new Rectangle(610,30,200,50);
		store1 = new Rectangle(73,417,200,200);
		store2 = new Rectangle(332,417,200,200);
		store3 = new Rectangle(626,417,200,200);
		exit = new Rectangle(350, 450, 200, 100);
		
		//loading images
		back2 = new ImageIcon("MarioBackground2.png").getImage().getScaledInstance(10500,650,Image.SCALE_SMOOTH);
		back3 = new ImageIcon("MarioBackground3.png").getImage().getScaledInstance(10500,650,Image.SCALE_SMOOTH);
		back4 = new ImageIcon("MarioBackground4.png").getImage().getScaledInstance(10500,650,Image.SCALE_SMOOTH);
		keys = new boolean[KeyEvent.KEY_LAST+1];
		backbtn = new ImageIcon("buttons/backbtn.png").getImage().getScaledInstance(200,50,Image.SCALE_SMOOTH);
		instructionsbtn = new ImageIcon("buttons/instructionsbtn.png").getImage().getScaledInstance(200,50,Image.SCALE_SMOOTH);
		creditsbtn = new ImageIcon("buttons/creditsbtn.png").getImage().getScaledInstance(200,50,Image.SCALE_SMOOTH);
		nextbtn = new ImageIcon("buttons/nextbtn.png").getImage().getScaledInstance(200,50,Image.SCALE_SMOOTH);
		storebtn = new ImageIcon("buttons/storebtn.png").getImage().getScaledInstance(200,50,Image.SCALE_SMOOTH);
		startbtn = new ImageIcon("buttons/startbtn.png").getImage().getScaledInstance(200,50,Image.SCALE_SMOOTH);
		exitbtn = new ImageIcon("buttons/exitbtn.png").getImage().getScaledInstance(200,50,Image.SCALE_SMOOTH);
		
		backbtnDown = new ImageIcon("buttons/backbtnDown.png").getImage().getScaledInstance(200,50,Image.SCALE_SMOOTH);
		instructionsbtnDown = new ImageIcon("buttons/instructionsbtnDown.png").getImage().getScaledInstance(200,50,Image.SCALE_SMOOTH);
		creditsbtnDown = new ImageIcon("buttons/creditsbtnDown.png").getImage().getScaledInstance(200,50,Image.SCALE_SMOOTH);
		nextbtnDown = new ImageIcon("buttons/nextbtnDown.png").getImage().getScaledInstance(200,50,Image.SCALE_SMOOTH);
		storebtnDown = new ImageIcon("buttons/storebtnDown.png").getImage().getScaledInstance(200,50,Image.SCALE_SMOOTH);
		startbtnDown = new ImageIcon("buttons/startbtnDown.png").getImage().getScaledInstance(200,50,Image.SCALE_SMOOTH);
		exitbtnDown = new ImageIcon("buttons/exitbtnDown.png").getImage().getScaledInstance(200,50,Image.SCALE_SMOOTH);
		
		back = new ImageIcon("MarioBackground.png").getImage().getScaledInstance(10500,650,Image.SCALE_SMOOTH);
		storeback = new ImageIcon("storeBackground.jpg").getImage().getScaledInstance(900,620,Image.SCALE_SMOOTH);
		intermissionback = new ImageIcon("intermissionBackground.jpg").getImage();
		intermissionback2 = new ImageIcon("intermissionBackground2.jpg").getImage();
		exitBack = new ImageIcon("exitBackground.jpg").getImage();
		menuback = new ImageIcon("MenuBackground.png").getImage();
		menuinstructions = new ImageIcon("MenuInstructions.png").getImage();
		menucredits = new ImageIcon("MenuCredits.png").getImage();
		exitText = new ImageIcon("Mariopics/exitText.png").getImage();
		coinPic = new ImageIcon("Mariopics/coin.gif").getImage().getScaledInstance(15,25,Image.SCALE_SMOOTH);
		mushroomPic = new ImageIcon("Mariopics/mushroom.png").getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH);
		fireFlowerIconPic = new ImageIcon("Mariopics/fireFlowerIconPic.png").getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH);
		marioShroomIconPic = new ImageIcon("Mariopics/marioShroomIcon.png").getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH);
		coinIconPic = new ImageIcon("Mariopics/coinIcon.png").getImage().getScaledInstance(20,30,Image.SCALE_SMOOTH);
		lifeIconPic = new ImageIcon("Mariopics/lifeMushroom.png").getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH);
		fireballLPic = new ImageIcon("Mariopics/fireball.png").getImage().getScaledInstance(40,30,Image.SCALE_SMOOTH);
		fireballRPic = new ImageIcon("Mariopics/fireballR.png").getImage().getScaledInstance(40,30,Image.SCALE_SMOOTH);
		brickPic = new ImageIcon("Mariopics/mariobrick.png").getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
		questionPic = new ImageIcon("Mariopics/questionblock2.png").getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
		emptyQuestionPic = new ImageIcon("Mariopics/emptyQuestionblock.png").getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
		shroomPic = new ImageIcon("Mariopics/redShroom.png").getImage().getScaledInstance(30,30,Image.SCALE_SMOOTH);
		
		//loading images for arrays, used for animations
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
    			for(int z=0; z<5; z++) //adding 5 copies of each frame to slow down the animation
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
        for(int i=0; i<8; i+=2) //two evolve pictures for every picture in the marioWalk pictures 
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
			platPic2 = ImageIO.read(new File("MarioPics/platback2.png"));
			platPic3 = ImageIO.read(new File("MarioPics/platback3.png"));
			platTopPic = ImageIO.read(new File("MarioPics/platTopPic.jpg"));	
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
		
		//loading everything in the game
        currPic = marioRightWalkPics.get(0);
		mainFrame = m;
		setSize(800,600);
        addKeyListener(this);
        loadPlatforms();
        loadCoins();
    	loadGoombas();
    	loadMushrooms();
    	loadBricks();
    	loadSpinys();
    	loadBills();
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
    	
    	if(screen == "level1" || screen == "level2" || screen == "level3" || screen == "level4")
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
	    	checkSpinyCollide();
	    	checkBillCollide();
	    	moveGoombas();
	    	moveMushrooms();
	    	moveSpinys();
	    	moveBills();
    	}
    	if(screen == "level4")
    	{
    		checkBowserCollide();
    		moveBowser();
    	}
    	
		mouse = MouseInfo.getPointerInfo().getLocation();
		Point offset = getLocationOnScreen();
		mouse.translate(-offset.x, -offset.y);
    }
	
    public void checkDeath()
    {
    	//Everything is re-generated and reset if Mario dies and the player restarts at the beginning of the level they died on 
    	if(lives==0)
    	{
			backX = -20;
			platforms = new ArrayList<platform>();
			coins = new ArrayList<coin>();
			mushrooms = new ArrayList<mushroom>(); //green mushrooms
			goombas = new ArrayList<goomba>();
			bricks = new ArrayList<ArrayList<brick>>();
			marioMushrooms = new ArrayList<marioShroom>(); //red mushrooms
			platPics = new ArrayList<BufferedImage>();
			platTopPics = new ArrayList<BufferedImage>();
			
			backX2 = -20;
			coins2 = new ArrayList<coin>();
			platforms2 = new ArrayList<platform>();
			mushrooms2 = new ArrayList<mushroom>();
			goombas2 = new ArrayList<goomba>();
			spinys = new ArrayList<spiny>();
			bricks2 = new ArrayList<ArrayList<brick>>();
			marioMushrooms2 = new ArrayList<marioShroom>();
			platPics2 = new ArrayList<BufferedImage>();
			platTopPics2 = new ArrayList<BufferedImage>();
			
			backX3 = -20;
			coins3 = new ArrayList<coin>();
			platforms3 = new ArrayList<platform>();
			mushrooms3 = new ArrayList<mushroom>();
			goombas3 = new ArrayList<goomba>();
			spinys2 = new ArrayList<spiny>();
			bills = new ArrayList<bulletBill>();
			bricks3 = new ArrayList<ArrayList<brick>>();
			marioMushrooms3 = new ArrayList<marioShroom>();
			platPics3 = new ArrayList<BufferedImage>();
			platTopPics3 = new ArrayList<BufferedImage>();
			
			Image back4;
			backX4 = -20;
			bowser = new boss(650, 555-70, 70, 70, 100);
			
			currPic = marioRightWalkPics.get(0);
	    	loadPlatforms();
	        loadCoins();
	    	loadGoombas();
	    	loadMushrooms();
	    	loadBricks();
	    	loadSpinys();
	    	loadBills();
	    	lives = 5;
	    	collectedCoins = 0;
	    	totalCoins = 0;
	    	mario.setVY(0);
    	}
    }
    
    public void checkFinish()
    {
    	//once the player reaches a certain point in the level they are brought to the intermission screen for the next level
    	if(screen == "level1")
    	{
	    	if(backX < -9560)
	    	{
			    try{sound = Applet.newAudioClip(clearwavFile.toURL());} //level clear sound
			    catch(Exception e){e.printStackTrace();}
			    sound.play();
	    		totalCoins = collectedCoins; //the total coins variable becomes equal to the collected coins of the level
	    		screen = "intermission"; //goes to the intermission screen
	    		intermissionNum = 1; //controls what intermission to go back to when in the store
	    	}	
    	}
    	if(screen == "level2")
    	{
	    	if(backX2 < -9560)
	    	{
			    try{sound = Applet.newAudioClip(clearwavFile.toURL());}
			    catch(Exception e){e.printStackTrace();}
			    sound.play();
	    		totalCoins = collectedCoins;
	    		screen = "intermission2";
	    		intermissionNum = 2;
	    	}	
    	}
    	
    	if(screen == "level3")
    	{
	    	if(backX3 < -9760)
	    	{
			    try{sound = Applet.newAudioClip(clearwavFile.toURL());}
			    catch(Exception e){e.printStackTrace();}
			    sound.play();
	    		totalCoins = collectedCoins;
	    		screen = "intermission3";
	    		intermissionNum = 3;
	    	}	
    	}
    }
    
    public void shrink()
    {
    	//makes mario smaller
		mario.setHeight(50);
		mario.setWidth(25);
		marioBig = false;
		collide = true;
		ground += 20; //this is for decreasing Mario's Y value while on the ground
		mario.setY(mario.getY()+20);	
		evolveVY = mario.getVY();
		invE = true; //Mario is given a short period of invincibility so he does not lose a life immediately after shrinking
    }
    
    public void grow()
    {
    	//makes mario bigger
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
    	//used to get the data for the current level the user is on
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
			currPlatPics = platPics;    		
    		currPlatTopPics = platTopPics;
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
    		currPlatPics = platPics2;    		
    		currPlatTopPics = platTopPics2;
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
    		currPlatPics = platPics3;    		
    		currPlatTopPics = platTopPics3;
    	}
    	
    	if(screen == "level4")
    	{
    		currBackX = backX4;
    		currBack = back4;
		    currCoins = new ArrayList<coin>();
			currPlatforms = new ArrayList<platform>();
			currMushrooms = new ArrayList<mushroom>();
			currGoombas = new ArrayList<goomba>();
			currSpinys = new ArrayList<spiny>();
			currBills = new ArrayList<bulletBill>();
			currBricks = new ArrayList<ArrayList<brick>>();
			currMarioMushrooms = new ArrayList<marioShroom>();
    	}
    }
    
    public void getTmp() //getting the correct frame for the animation of Mario transforming into big/small Mario 
    {
		if(shiftRight) //if mario is facing right
		{
			if(right) //if mario is moving right or jumping, then it looks for the corresponding picture 
			{
				tmp = evolvePicsRight.get(frames);
			}
			else if(mario.getVY()!=0) 
			{
				tmp = evolvePicsRight.get(frames);
			}
			else //otherwise it obtains the idle picture 
			{
				tmp = evolvePicsRight.get(0);
			}
		}
		if(shiftLeft) //same concept for left pictures
		{
			if(left)
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
    	//used to total the coins on the ground and platforms
    	int count = 0;
    	for(coin c : currCoins)
    	{
    		if(c.getCollected()==true)
    		{
    			//if a coin is collected it is added to count, the total amount of coins collected in the level
    			count += c.getPoints();
    		}
    	}
    	collectedCoins = count + totalCoins; //the final total is the collectedCoins(collected in the level) + the totalCoins(which were gained previously)
    }
    
    public void invincibilityCooldown()
    {
    	if(inv) //damage invincibility
    	{
    		invincible = true;
    		invincibleCount += 1;
    		if(invincibleCount == 60) //for a certain time the player will not take any damage
    		{
    			//when that time is over the players invincibilty disappears
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
    	//the player itself does not move, but the background moves with the arrow keys
    	
    	//used to move everything in the level left
    	if(screen == "level1")
    	{
			backX -= 4;
    	}
    	else if(screen == "level2")
    	{
			backX2 -= 4;
    	}
    	else if(screen == "level3")
    	{
			backX3 -= 4;
    	}
    	else if(screen == "level4")
    	{
			backX4 -= 4;
			bowser.addX(-4);
    	}
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
       	//used to move everything in the level right
    	if(screen == "level1")
    	{
			backX += 4;
    	}
    	else if(screen == "level2")
    	{
			backX2 += 4;
    	}
    	else if(screen == "level3")
    	{
			backX3 += 4;
    	}
    	else if(screen == "level4")
    	{
			backX4 += 4;
			bowser.addX(4);
    	}
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
					//controls direction
					right = true;
					left = false;
					if(shiftRight == false)
					{
						mario.addX(-10); //shifts the player to the left or right to better indicate the direction
					}
					shiftRight = true;
					shiftLeft = false;
					moveBackLeft(); //moves the background left or right
				}	
			}
			
			if(!collideR && !collide)
			{
				if(keys[KeyEvent.VK_LEFT] && backX <= 0) //the player can not go left beyond a certain point
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
			Point m = MouseInfo.getPointerInfo().getLocation();
			Point offset = getLocationOnScreen();
		}
		if(screen == "level4")
		{
			if(!collideL && !collide)
			{
				if(keys[KeyEvent.VK_RIGHT] && backX4 >= -600)
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
				if(keys[KeyEvent.VK_LEFT] && backX4 <= 0)
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
		}
	}
	
	public void jump()
	{
		if(!collide)
		{
			if(keys[KeyEvent.VK_UP] && mario.getJump()==false && jumpWait == false) //checks if a jump is possible
			{
				mario.setJump(true);
				mario.setVY(-20); //the velocity of the jump
			}
			if(mario.getJump() == true)
			{
				mario.addY(mario.getVY()); //adds to marios y position to make him jump
						
				if(mario.getY() >= ground) //if mario is on the ground everything is reseted
				{
					mario.setY(ground); //his y position is on the ground
					mario.setVY(0);
					mario.setJump(false);
				}
				mario.addVY(1); //gravity so mario falls back down
			}
		}
	}
	
	public void shoot()
	{
		if(keys[KeyEvent.VK_SPACE] && fireflowerPower == true && fball.getUsed() == false) //checks if a fireball is possible
		{
			fball.setLeft(shiftLeft); //decides the direction of the fireball
			fball.setRight(shiftRight);
			fball.setX(mario.getX()); //the fireball is created at marios position
			fball.setY(mario.getY()+20);
			fball.setUsed(true);
			//plays fireball sound
		    try{sound = Applet.newAudioClip(firewavFile.toURL());}
		    catch(Exception e){e.printStackTrace();}
		    sound.play();
		}
		if(fball.getUsed() == true)
		{
			//moves based on direction mario is facing
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
			fball.setUsed(false); //when it reaches a certain point the fireball disappears
		}	
	}
	
    public void jumpCooldown()
    {
    	if(mario.getVY() == 0 || mario.getVY() == 1) //if mario is on the ground or on a platform the cooldown starts
    	{
    		jumpWait = true;
    		jCooldownCount += 1;
    		if(jCooldownCount == 3) //for a certain time mario can not jump
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
			//moves goombas based on how they were loaded, same for spinys, and 1up mushrooms
			if(!g.getKilled())
			{
				if(g.getLeft() == true) //moves based on current direction
				{
					if(g.getX() >= g.getMin()) //while it does not reach the min or max it keeps on moving
					{
						g.addX(-1);
					}
					else //if it does reach the min or max it changes direction
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
    		if(bb.getCD()) //the bullet bill keeps moving until it has reached its max distance and while the cooldown is active
    		{			   
    			//bulletbill moves based on direction
    			if(bb.getLeft())
    			{
    				bb.addX(-2);
    			}
	    		if(bb.getRight())
	    		{
	    			bb.addX(2);
	    		}
    		}
    		else //once the bullet has reached its max distance, it relaunches after checking which way to launch
    		{
    			bb.setCD(true);
				if(bb.getBX()< mario.getX()) //the bills get launched out of the left/right side of the blaster depending on Mario's location
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
    
    public void moveBowser()
    {
    	if(!bowser.getFireball()) //if Bowser is not shooting fireballs or is not punching, it checks where Mario is and Bowser tracks him down until 
		{						  //Mario is in punch range
			if(!bowser.getPunch())
			{
				if(bowser.getX()-mario.getX()>15)
				{
					bowser.addX(-1);
					bowser.setLeft(true);
					bowser.setRight(false);
				}
				if(bowser.getX()-mario.getX()<-50)
				{
					bowser.addX(1);
					bowser.setRight(true);
					bowser.setLeft(false);
				}
			}
			if(bowser.getX()-mario.getX()>=-50 && bowser.getX()-mario.getX()<=15) //Bowser punches once he is in close range of Mario
			{
				if(!bowser.getPunch())
				{
					bowser.setPunch(true);
				}
			}
		}
		if(bowser.getHealth()>0) //This determines which way Bowser has to face according to Mario's location
		{
			if(bowser.getX()>=mario.getX())
			{
				bowser.setRight(false);
				bowser.setLeft(true);
				
			}
			else if(bowser.getX()<mario.getX())
			{
				bowser.setRight(true);
				bowser.setLeft(false);
			}
		}
    }    
    
    public void loadPlatforms()
    {
    	for(int z=1; z<4; z++) //loads 3 times for each level
    	{
    		int plx;
			int ply;
			int size;
	    	boolean sameSpot = false;
	    	ArrayList<platform> currList = new ArrayList<platform>();  //used to point to the respective list(s) of the level 
	    	if(z==1)												   //same method is used for loading other things
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
	    	for(int i=0;i<40;i++) //the amount of platforms in the level
	    	{
	    		plx = rand.nextInt(9000) + 500; //makes the platforms have a random x position, y position, and size
	    		ply = rand.nextInt(250) + 150;
	    		size = rand.nextInt(320) + 150;
	    		for(platform p : currList)
	    		{
	    			Rectangle newRect = new Rectangle(plx,ply,size,555-ply);
	    			Rectangle oldRect = new Rectangle(p.getX()-10,p.getY()-40,p.getSizeX()+20,p.getSizeY()+80);
	    			if(newRect.intersects(oldRect))
	    			{
	    				sameSpot = true; //if a platform is loaded on top of an existing platform it is not made
	    				//the same method is used for loading the rest of the data as well
	    			}	
	    		}
	    		if(sameSpot == false)
	    		{
	    			if(z==1) //adds the platform and its texture according to the level
	    			{
	    				platform plat = new platform(plx,ply,size,10,false);
		    			platforms.add(plat);
		    			platPics.add(platPic.getSubimage(0, 13, plat.getSizeX(), 555 - plat.getY())); //getting texture for the majority of the platform
		    			platTopPics.add(platPic.getSubimage(0, 0, plat.getSizeX(), 10)); //getting texture for the very top of the platform where entities can stand
	    			}
	    			if(z==2)
	    			{
	    				platform plat = new platform(plx,ply,size,10,false);
		    			platforms2.add(plat);
		    			platPics2.add(platPic2.getSubimage(0, 13, plat.getSizeX(), 575 - plat.getY()));
		    			platTopPics2.add(platPic2.getSubimage(0, 0, plat.getSizeX(), 10));
	    			}
	    			if(z==3)
	    			{
	    				platform plat = new platform(plx,ply,size,10,false);
		    			platforms3.add(plat);
		    			platPics3.add(platPic3.getSubimage(0, 13, plat.getSizeX(), 555 - plat.getY()));
		    			platTopPics3.add(platTopPic.getSubimage(0, 0, plat.getSizeX(), 10));
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
					x = rand.nextInt(p.getSizeX() - 15); //makes the x position somewhere on the platform
					if(z==1)
					{
						coins.add(new coin(p.getX() + x,p.getY() - 30,15,25,1,false)); //adds a coin on the platform anywhere from the start of the platform and X pixels right 
					}																   //and 30 pixels up since that is the height of the coin
					if(z==2)														   //same method is used for generating anything else on the platforms and the ground
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
				x = rand.nextInt(9000) + 500; //makes the x position somewhere on the ground
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
	    	//Goombas on platforms
			for(platform p : currList)
			{
				if(p.getSomethingOn() == false) //If an enemy does not already occupy this platform 
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
						p.setSomethingOn(true); //Now an enemy occupies this platform
					}	
				}
			}
			//Goombas on the ground 
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
					bills.add(new bulletBill(p.getX() + x,p.getY() - 55, p.getX()+x, p.getY() - 70, 70, 40, false)); //first two coordinates are for the bullet bill, and the next two are for
					p.setSomethingOn(true);																			 //for its blaster 
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
    			Rectangle oldRect = new Rectangle(g.getX()-10,g.getY(),g.getSizeX()+20,g.getSizeY());
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
	    		y = rand.nextInt(15) + 420;
	    		for(ArrayList<brick> b2: currList) //2D ArrayLists are used for the bricks to keep the brick segments separate and so we can access the individual bricks
	    		{								   //in each brick segment
	    			for(brick b: b2)
	    			{
	    				int dx = b2.get(b2.size()-1).getX() + 40 - b2.get(0).getX(); //length of the brick segment
	    				Rectangle brickSegment = new Rectangle(b2.get(0).getX(), b2.get(0).getY(), dx, 40);
	    				Rectangle newRect = new Rectangle(x-40, y, 280, 40); //this makes sure that there is at least a one brick-wide gap in between the brick segments

	    				if(newRect.intersects(brickSegment))
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
    			Rectangle bBot = new Rectangle(b.getX()+2,b.getY()+40,36,1);;
		    	Rectangle bTop = new Rectangle(b.getX(),b.getY(),40, 1);
		    	
		    	Rectangle guy = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
		    	
		    	if(!b.getBroken()) //If the brick is not already broken, then it will check for interactions
		    	{
		    		if(bLeft.intersects(guy)){ //If mario collides with the left or right side of a brick, he will not be able to continue in that same direction because this
		        		collideL = true;	   //prevents him from sinking into the bricks
		        	}
			        else if(bRight.intersects(guy)){
			        	collideR = true;
			        }
			        if(bBot.intersects(guy)) 
			        {
			        	if(marioBig) //if mario is big and he collides with the underside of a normal brick, he breaks it
			        	{
			        		if(!b.getQuestion()) //if it is a normal brick
			        		{
							    try{sound = Applet.newAudioClip(breakwavFile.toURL());}
							    catch(Exception e){e.printStackTrace();}
							    sound.play();
		        				mario.setVY(5); //if mario collides with the underside of a brick, he gets pushed backed down to the ground due to gravity
				        		mario.setJump(true);
				        		b.setBroken(true);
				        		b.setVY(-10); //sets the velocity for the pieces of the broken brick
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
			        else if(bTop.intersects(guy))
			        {
			        	mario.setY(b.getY()-mario.getHeight()); //if mario lands on top of a brick, his Y coordinate is adjusted for him to stay there
			        	mario.setVY(0);
			        	mario.setJump(false);
			        }
			        if(mario.getY()==ground || mario.getY()<= b.getY()-mario.getHeight())  //if mario is not colliding either side of a brick
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
			
			//Similar methods used for collisions of all other enemies
    		if(m.intersects(goombaRect))
    		{
    			if(g.getKilled()==false)
    			{
    				if(mario.getVY() != 1 && mario.getVY() != 0 && mario.getVY() > 1) //if Mario is falling on top of the goomba, the goomba dies
    				{
    					g.setKilled(true);
    					mario.setVY(-10);
    				}
    				else //otherwise Mario gets shrinked if he is big mario or loses a life otherwise
    				{
	    				if(marioBig && !invE) //if Mario is big mario and he is not in the animation of growing into big mario
	    				{
						    try{sound = Applet.newAudioClip(damagewavFile.toURL());}
						    catch(Exception e){e.printStackTrace();}
						    sound.play();
	    					shrink();
	    					getTmp(); //getting pictures needed for animation of shrinking 
    					}
    					else if(invincible == false)
    					{
						    try{sound = Applet.newAudioClip(damagewavFile.toURL());}
						    catch(Exception e){e.printStackTrace();}
						    sound.play();
	    					lives -= 1;
							inv = true; //Gives him a short period of invincibility so Mario does not lose another life immediately 
    					}
    				}
    			}
    		}
    		else if(f.intersects(goombaRect)) //if Mario's fireballs hit a goomba, it dies
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
					    try{sound = Applet.newAudioClip(damagewavFile.toURL());}
					    catch(Exception e){e.printStackTrace();}
					    sound.play();
					}
					else if(invincible == false)
					{
					    try{sound = Applet.newAudioClip(damagewavFile.toURL());}
					    catch(Exception e){e.printStackTrace();}
					    sound.play();
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
    	for(bulletBill bb: currBills)
    	{
    		Rectangle m = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
			Rectangle f = new Rectangle(fball.getX(),fball.getY(),fball.getSizeX(),fball.getSizeY());
			Rectangle billRect = new Rectangle(bb.getX(),bb.getY(),bb.getSizeX(),bb.getSizeY());
			Rectangle mBot = new Rectangle(mario.getX(), mario.getY()+mario.getHeight()-5, mario.getWidth(), 5);
			Rectangle bbTop = new Rectangle(bb.getX(), bb.getY(), bb.getSizeX(), 5);
			
    		if(m.intersects(billRect))
    		{
    			if(bb.getKilled()==false)
    			{
    				if(mario.getVY() >= 2 && Math.abs(bb.getX()-bb.getBX())<= 390)
    				{
    					bb.setKilled(true);
    					mario.setVY(-10);
    				}
    				else if(marioBig && !invE)
    				{
    					shrink();
    					getTmp();
					    try{sound = Applet.newAudioClip(damagewavFile.toURL());}
					    catch(Exception e){e.printStackTrace();}
					    sound.play();
					}
					else if(invincible == false)
					{
					    try{sound = Applet.newAudioClip(damagewavFile.toURL());}
					    catch(Exception e){e.printStackTrace();}
					    sound.play();
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
    
    public void checkBowserCollide()
	{
		Rectangle fireRect = new Rectangle(bowser.getX()+bowser.getFireDist(), bowser.getY()+10, 50, 20);
		Rectangle marioRect = new Rectangle(mario.getX(), mario.getY(), mario.getWidth(), mario.getHeight());
		Rectangle mBot = new Rectangle(mario.getX(), mario.getY()+mario.getHeight()-5, mario.getWidth(), 10);
		Rectangle bowserTop = new Rectangle(bowser.getX(), bowser.getY(), bowser.getSizeX(), 10);
		Rectangle bowserRect = new Rectangle();
		Rectangle f = new Rectangle(fball.getX(),fball.getY(),fball.getSizeX(),fball.getSizeY());
		Rectangle bRect = new Rectangle(bowser.getX(), bowser.getY()+10, bowser.getSizeX(), bowser.getSizeY());
		//These are to determine which side to put the Bowser's hitbox of getting hit by Mario's fireballs,
		//since making a large hitbox would make Mario's fireball do too much damage
		if(bowser.getRight()) 
		{
			bowserRect = new Rectangle(bowser.getX(), bowser.getY(), 1, bowser.getSizeY());			
		}
		if(bowser.getLeft())
		{
			bowserRect = new Rectangle(bowser.getX()+bowser.getSizeX()-5, bowser.getY(), 1, bowser.getSizeY());			
		}
		
		if(bRect.intersects(marioRect) || fireRect.intersects(marioRect)) //if Mario gets hit by Bowser's punch or fireball
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
		else if(f.intersects(bowserRect)) //Mario's fireballs
		{
			if(fball.getUsed() == true)
			{
				bowser.damage(1);
			}
		}
		else if(mBot.intersects(bowserTop))
		{
			if(!bowser.getAttackCD()) //Mario can jump on Bowser's head and damage him if he hasn't done so recently
			{
				bowser.damage(1);
				mario.setVY(-10); //As usual, Mario jumps up after hitting an enemy from above
				bowser.setAttackCD(true);
			}
			else
			{
				mario.setVY(5); //if Mario is still on top of Bowser after jumping once, then the 
			}
		}
		if(bowser.getAttackCD()) //if Mario has jumped on Bowser and damaged him, he cannot do that again for a few moments
		{						 //this is to prevent the user to just keep jumping on Bowser's head to kill him
			bowser.attackCD();
		}
	}
    
    public void checkMushroomCollide() 
    {
    	for(mushroom mu : currMushrooms) //1 up mushrooms (green)
    	{
			Rectangle m = new Rectangle(mario.getX(),mario.getY(),mario.getWidth(),mario.getHeight());
			Rectangle mushRect = new Rectangle(mu.getX(),mu.getY(),mu.getSizeX(),mu.getSizeY());
    		if(m.intersects(mushRect))
    		{
    			if(mu.getCollected()==false) //if mario collides with the green mushroom, he gains a life
    			{
				    try{sound = Applet.newAudioClip(oneupwavFile.toURL());}
				    catch(Exception e){e.printStackTrace();}
				    sound.play();
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
    			if(mushroom.getVY() != 0 || mushroom.getGround()) //if the mushroom is falling off of its brick segment or is on the ground, then it will be obtainable 
    			{
    				mushroom.setCond(false);
    				if(!marioBig  && !invE) //if Mario is not already big Mario or is not in the animation of becoming big Mario,
    				{						//he collects the mushroom and transform into big Mario
    					grow();
			    		getTmp();	
    				}
    				else //otherwise he gets a coin
    				{
    					totalCoins++;
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
			    try{sound = Applet.newAudioClip(startwavFile.toURL());}
			    catch(Exception ex){ex.printStackTrace();}
			    sound.play();
			}
			if(menuInstructions.contains(mouse))
			{
				frames = 0;
				screen = "instructions";
			}
			if(menuCredits.contains(mouse))
			{
				frames = 0;
				screen = "credits";
			}
		}
		if(screen == "instructions")
		{
			if(storePrev.contains(mouse))
			{
				screen = "menu";
			}
		}
		if(screen == "credits")
		{
			if(storePrev.contains(mouse))
			{
				screen = "menu";
			}
		}
		if(screen == "intermission")
		{
			if(intermissionNext.contains(mouse))
			{
				mario.setVY(0);
				screen = "level2";
			    try{sound = Applet.newAudioClip(startwavFile.toURL());}
			    catch(Exception ex){ex.printStackTrace();}
			    sound.play();
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
				mario.setVY(0);
				frames=0;
				screen = "level3";
			    try{sound = Applet.newAudioClip(startwavFile.toURL());}
			    catch(Exception ex){ex.printStackTrace();}
			    sound.play();
			}
			if(intermissionStore.contains(mouse))
			{
				screen = "store";
			}
		}
		if(screen == "intermission3")
		{
			if(intermissionNext.contains(mouse))
			{
				mario.setVY(0);
				frames=0;
				screen = "level4";
			    try{sound = Applet.newAudioClip(startwavFile.toURL());}
			    catch(Exception ex){ex.printStackTrace();}
			    sound.play();
			}
			if(intermissionStore.contains(mouse))
			{
				screen = "store";
			}
		}
		if(screen == "exit")
		{
			if(exit.contains(mouse))
			{
				System.exit(0);
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
				if(intermissionNum == 3)
				{
					screen = "intermission3";
				}
			}
			if(store1.contains(mouse))
			{
				if(totalCoins >= 5)
				{
					if(marioBig == false)
					{
						getTmp();
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
    	if(mouse==null)
    	{
    		return;
    	}
    	if(screen == "menu")
    	{
	    	g.drawImage(menuback,0,0,null);
    		g.setColor(Color.blue);
	    	if(menuPlay.contains(mouse))
	    	{
	    		g.drawImage(startbtnDown,menuPlay.x,menuPlay.y,null);
	    	}
	    	else
	    	{
	    		g.drawImage(startbtn,menuPlay.x,menuPlay.y,null);
	    	}
	    	if(menuInstructions.contains(mouse))
	    	{
	    		g.drawImage(instructionsbtnDown,menuInstructions.x,menuInstructions.y,null);
	    	}
	    	else
	    	{
	    		g.drawImage(instructionsbtn,menuInstructions.x,menuInstructions.y,null);
	    	}
	    	if(menuCredits.contains(mouse))
	    	{
	    		g.drawImage(creditsbtnDown,menuCredits.x,menuCredits.y,null);
	    	}
	    	else
	    	{
	    		g.drawImage(creditsbtn,menuCredits.x,menuCredits.y,null);
	    	}
    	}
    	if(screen == "instructions")
    	{
	    	g.drawImage(menuinstructions,0,0,null);
    		g.setColor(Color.blue);
	    	if(storePrev.contains(mouse))
	    	{
	    		g.drawImage(backbtnDown,storePrev.x,storePrev.y,null);
	    	}
	    	else
	    	{
	    		g.drawImage(backbtn,storePrev.x,storePrev.y,null);
	    	}
    	}
    	if(screen == "credits")
    	{
	    	g.drawImage(menucredits,0,0,null);
    		g.setColor(Color.blue);
	    	if(storePrev.contains(mouse))
	    	{
	    		g.drawImage(backbtnDown,storePrev.x,storePrev.y,null);
	    	}
	    	else
	    	{
	    		g.drawImage(backbtn,storePrev.x,storePrev.y,null);
	    	}
    	}
    	if(screen == "intermission" || screen == "intermission2")
    	{
	    	g.drawImage(intermissionback,0,0,null);
	    	if(intermissionNext.contains(mouse))
	    	{
	    		g.drawImage(nextbtnDown,intermissionNext.x,intermissionNext.y,null);
	    	}
	    	else
	    	{
	    		g.drawImage(nextbtn,intermissionNext.x,intermissionNext.y,null);
	    	}
	    	if(intermissionStore.contains(mouse))
	    	{
	    		g.drawImage(storebtnDown,intermissionStore.x,intermissionStore.y,null);
	    	}
	    	else
	    	{
	    		g.drawImage(storebtn,intermissionStore.x,intermissionStore.y,null);
	    	}
    	}
    	if(screen == "intermission3")
    	{
	    	g.drawImage(intermissionback2,0,0,null);
	    	if(intermissionNext.contains(mouse))
	    	{
	    		g.drawImage(nextbtnDown,intermissionNext.x,intermissionNext.y,null);
	    	}
	    	else
	    	{
	    		g.drawImage(nextbtn,intermissionNext.x,intermissionNext.y,null);
	    	}
	    	if(intermissionStore.contains(mouse))
	    	{
	    		g.drawImage(storebtnDown,intermissionStore.x,intermissionStore.y,null);
	    	}
	    	else 
	    	{
	    		g.drawImage(storebtn,intermissionStore.x,intermissionStore.y,null);
	    	}
    	}
    	if(screen == "exit") //Game completed screen 
    	{
    		g.setColor(Color.white);
    		g.fillRect(0,0,900,650);
    		g.drawImage(exitBack, 400, 230, null);
    		g.drawImage(exitText, 200, 50, null);
    		if(exit.contains(mouse))
    		{
    			g.drawImage(exitbtnDown,exit.x,exit.y,null);
    		}
    		else
    		{
    			g.drawImage(exitbtn,exit.x,exit.y,null);
    		}
    	}
    	if(screen == "store")
    	{
	    	g.drawImage(storeback,0,0,null);
    		g.setColor(Color.blue);
	    	if(storePrev.contains(mouse))
	    	{
	    		g.drawImage(backbtnDown,storePrev.x,storePrev.y,null);
	    	}
	    	else
	    	{
	    		g.drawImage(backbtn,storePrev.x,storePrev.y,null);
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
			g.drawString(Integer.toString(totalCoins), 33, 69);
    	}
    	if(screen == "level1" || screen == "level2" || screen == "level3" || screen == "level4")
    	{
    		Rectangle marioRect = new Rectangle(mario.getX(), mario.getY(), mario.getWidth(),mario.getHeight());
    		g.drawImage(currBack,currBackX,0,null);
    		
    		//Drawing the texture for the platforms underneath the very top of the platform
			for(int i=0; i<currPlatforms.size(); i++)
			{
				g.drawImage(currPlatPics.get(i), currPlatforms.get(i).getX(), currPlatforms.get(i).getY(), null);
			}
			//Drawing the texture for the platforms for the very top of the platform where entities can walk
			for(int i=0; i<currPlatforms.size(); i++)
			{
				g.drawImage(currPlatTopPics.get(i), currPlatforms.get(i).getX(), currPlatforms.get(i).getY(), null);
			}
			for(coin c : currCoins)
			{
				if(c.getCollected() == false) //if Mario has not yet collected a coin, it will be drawn
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
						if(!marioBig && !b2.get(i).getQuestion())//if mario is small and hit hits the bottom of a brick segment, he pushes it up a bit
						{
							g.drawImage(brickPic,b2.get(i).getX(),b2.get(i).getY()-15,null);	
						}
						if(b2.get(i).getQuestion() && !b2.get(i).getShroom()) //if it is a question block and Mario has not already hit this question block for a red mushroom
						{
							g.drawImage(questionPic,b2.get(i).getX(),b2.get(i).getY()-15,null);
							if(!b2.get(i).getShroom()) //there is a 50% chance of getting a red mushroom, otherwise he gets a coin
							{						   
								Random rand = new Random();
								int n = rand.nextInt(2);
								if(n==1)
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
									currBrick = new ArrayList<brick>(); //reset list 
									for(brick b: b2)
									{
										if(!b.getBroken()) 
										{
											currBrick.add(b); //this is for determining when the red mushroom will fall of its brick segment
										}
										else if(b.getX()>b2.get(i).getX()) //if any brick is broken that is after the question block, that is the new brick segment's end
										{
											break; 
										}
									}
								}
								else
								{
									coinCollide = true;
									tmpBrick = b2.get(i); //used for drawing a coin above the question block Mario obtained the coin from
									totalCoins++; 
								}
								b2.get(i).setShroom(true); //Mario cannot obtain another mushroom from the same question block
							}	
						}
					}
					else if(b2.get(i).getBroken()) //if mario is big and he collides with the bottom of a normal brick, he breaks it instead
					{
						if(b2.get(i).getY()<555)
						{  
							g.drawImage(brickSegments[0], b2.get(i).getX()-b2.get(i).getVX(), b2.get(i).getY(), null); //top left piece
							g.drawImage(brickSegments[1], b2.get(i).getX()+20+b2.get(i).getVX(), b2.get(i).getY(), null); //top right piece
							g.drawImage(brickSegments[2], b2.get(i).getX()-b2.get(i).getVX(), b2.get(i).getY()+20, null); //bottom left piece 
							g.drawImage(brickSegments[3], b2.get(i).getX()+20+b2.get(i).getVX(), b2.get(i).getY()+20, null); //bottom right piece
							b2.get(i).addY(b2.get(i).getVY()); //basically making the brick pieces "jump" since Mario hits them from underneath and they break into 4 pieces
							b2.get(i).addVY(1); 
							b2.get(i).addVX(1);
						}
					}						
					else if(!m.intersects(brickBot)) 
					{
						if(!b2.get(i).getQuestion()) //if it is not a question block
						{
							g.drawImage(brickPic,b2.get(i).getX(),b2.get(i).getY(),null);	
						}
						else if(b2.get(i).getQuestion() && !b2.get(i).getShroom()) //if Mario has not hit a quesiton block yet
						{
							g.drawImage(questionPic,b2.get(i).getX(),b2.get(i).getY(),null);
						}
						else if(b2.get(i).getQuestion() && b2.get(i).getShroom())
						{
							g.drawImage(emptyQuestionPic,b2.get(i).getX(),b2.get(i).getY(),null);
						}
					}
				}
			}
			if(coinCollide) //if gets a coin instead of a red mushroom, a coin appears for a short time above the block Mario got the coin from
			{
				g.drawImage(coinPic, tmpBrick.getX()+20, tmpBrick.getY()-40, null);
				coinCount++;
				if(coinCount>=20)
				{
					coinCollide = false;
					coinCount = 0;
				}
			}
			for(marioShroom mushroom : currMarioMushrooms)
	    	{
	    		if(mushroom.getCond()) //if the red mushroom has not been collected yet
	    		{
	    			g.drawImage(shroomPic, mushroom.getX()+5,mushroom.getY()+10,null);
    				mushroom.addX(3);
	    		}
	    		
				int dx = currBrick.get(currBrick.size()-1).getX()+40 - currBrick.get(0).getX(); //this gets the length of the red mushroom's brick segment
				Rectangle mushRect = new Rectangle(mushroom.getX(), mushroom.getY(), 40, 40); 
				
				if(mushroom.getX() > currBrick.get(0).getX()+dx && mushroom.getGround() == false) //if the mushroom has reached the end of its brick segment, it falls to the ground
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
    		for(goomba gb : currGoombas) //draws the sprites for the goombas if they are not killed
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
			//Drawing 1 up (green) mushrooms if they are not collected 
			for(mushroom m : currMushrooms)
			{
				if(m.getCollected() == false)
				{
					g.drawImage(mushroomPic, m.getX(), m.getY(), null);
				}
			}
			
			if(fball.getUsed() == true) //if Mario has shot a fireball
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
					s.draw(g); //drawing spinys if they are not dead
				}
				else
				{
					if(s.getLeft())
					{ 
						if(s.getKillTimer()<=15) // dead spiny appears for a bit
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
			}
			
			for(bulletBill bb : currBills)
			{
				if(bb.getKilled())
				{
					if(bb.getY()<555) //if Mario kills the bullet bill before it explodes, it falls to the ground
					{
						bb.addY(8);
						bb.draw(g);
					}
				}
				if(!bb.getKilled())
				{
					if(Math.abs(bb.getX()-bb.getBX())<= 390) //if the bullet bill has reached its distance of 390 pixels from its blaster, it explodes
					{
						bb.draw(g);	//drawing the bullet bills
					}	
					else
					{
						bb.drawExplosion(g);
					}			
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
			
			if(screen == "level4")
			{
				if(bowser.getHealth()<=0)
				{
					bowser.drawDeath(g);
					if(bowser.getKilled()) //this is true after Bowser's death animation is done 
					{
						screen = "exit";
						try{sound = Applet.newAudioClip(clearwavFile.toURL());} //level clear sound
					    catch(Exception e){e.printStackTrace();}
					    sound.play();
					}
				}
				//these draw whatever Bowser is currently doing (shooting fireballs, walking, or punching)
				else if(bowser.getFireball()) 
				{
					bowser.draw(g);
				}
				else if(!bowser.getPunch() && !bowser.getFireball())
				{
					bowser.drawWalk(g);
				}
				else if(bowser.getPunch())
				{
					bowser.drawPunch(g);
				}			
			}
			
			if(collide) //when mario has hit a red mushroom or an enemy, he grows/shrinks
			{
				if(marioBig) //mario grows
				{
					if(evolveCD<=25) //each picture for the evolve animation lasts for around every 25 counts added to evolveCD
					{
						g.drawImage(tmp.get(0), mario.getX(), mario.getY()+13, null);
						evolveCD++;
					}
					if(evolveCD>=25 && evolveCD<=50)
					{
						g.drawImage(tmp.get(1), mario.getX(), mario.getY()+6, null);
						evolveCD++;
					}
					if(evolveCD>=50 && evolveCD<=75) //this gets the third picture in the animation according to which ever way Mario is facing
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
					if(evolveCD==75) //reseting the timer
					{
						evolveCD = 0;
						collide = false;
					}
					//Getting the new set of pictures for big Mario
					if(shiftLeft) //if Mario is facing left
					{
						if(left) //if Mario is facing left and walking left
						{
							currPic = marioBigLeftWalkPics.get(frames);
						}
						else
						{
							currPic = marioBigLeftWalkPics.get(0);
						}
					}
					//Same concept for right pictures
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
				else //mario shrinks, same concept applied for Mario shrinking, except his the pictures are drawn in reverse order
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
			
			if(!collide) //Mario's walking animation
			{
				if(!right && !left) //Idle pictures depending on if Mario is small/big and the direction he is facing
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
						currPic = marioBigRightWalkPics.get(0); //Idle picture is set to Mario facing right
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
	//Getters and setters
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
	private Image leftPic, rightPic, blaster, explodePic;
	private boolean killed = false;
	private boolean cooldown;

	public bulletBill(int x1, int y1, int bX1, int bY1, int sx1, int sy1, boolean c)
	{
		X = x1; //coordinates of the bullet bill
		Y = y1;
		sizeX = sx1;
		sizeY = sy1;
		bX = bX1; //coordinates of the blaster
		bY = bY1;
		blaster = new ImageIcon("MarioPics/blaster2.png").getImage().getScaledInstance(60, 70,Image.SCALE_SMOOTH);
		leftPic = new ImageIcon("MarioPics/leftBill.png").getImage().getScaledInstance(sizeX, sizeY,Image.SCALE_SMOOTH);
		rightPic = new ImageIcon("MarioPics/rightBill.png").getImage().getScaledInstance(sizeX, sizeY,Image.SCALE_SMOOTH);
		explodePic = new ImageIcon("MarioPics/explosionPic.png").getImage().getScaledInstance(sizeX, sizeY,Image.SCALE_SMOOTH);
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
	
	public void drawExplosion(Graphics g)
	{
		g.drawImage(explodePic, X, Y, null);
	}
}

class boss
{
	private int X,Y,VX,sizeX, sizeY, health, fireCount, fireDist, totalFireDist, attackCount;
	private int frames, deathFrames, totalFrames;
	private boolean killed, punch, fireball, attackCD;
	private boolean left = true;
	private boolean right = false;
	private Image bowserRight, bowserLeft, fireballRight, fireballLeft;
	private ArrayList<Image> fireballPics = new ArrayList<Image>();	
	private ArrayList<Image> deadPics = new ArrayList<Image>();	
	private ArrayList<Image> punchPics = new ArrayList<Image>();
	private ArrayList<Image> walkPics = new ArrayList<Image>();	
	
	public boss(int X1, int Y1, int sizeX1, int sizeY1, int h)
	{
		X = X1;
		Y = Y1;
		sizeX = sizeX1;
		sizeY = sizeY1;
		health = 50;
		frames = 0;
		deathFrames = 0; //frames for Bowser death animation
		totalFrames = 0;
		killed = false;
		punch = false;
		fireball = true;
		for(int i=0; i<13; i++)
		{
			for(int z=0; z<5; z++)
			{
				deadPics.add(new ImageIcon("MarioPics/bowserDead/bowserDead"+Integer.toString(i)+".png").getImage().getScaledInstance(sizeX, sizeY,Image.SCALE_SMOOTH));
			}
		}
		for(int i=1; i<17; i++)
		{
			for(int z=0; z<5; z++)
			{
				walkPics.add(new ImageIcon("MarioPics/bowserWalk/bowserWalk"+Integer.toString(i)+".png").getImage().getScaledInstance(sizeX, sizeY,Image.SCALE_SMOOTH));
			}
		}
		for(int i=1; i<29; i++)
		{
			for(int z=0; z<5; z++)
			{
				punchPics.add(new ImageIcon("MarioPics/bowserPunch/bowserPunch"+Integer.toString(i)+".png").getImage().getScaledInstance(sizeX, sizeY,Image.SCALE_SMOOTH));
			}
		}
		bowserRight = new ImageIcon("MarioPics/bowser0.png").getImage().getScaledInstance(sizeX, sizeY,Image.SCALE_SMOOTH);
		bowserLeft = new ImageIcon("MarioPics/bowser1.png").getImage().getScaledInstance(sizeX, sizeY,Image.SCALE_SMOOTH);
		fireballRight = new ImageIcon("MarioPics/bowserFireBall.png").getImage().getScaledInstance(80, 40,Image.SCALE_SMOOTH);
		fireballLeft = new ImageIcon("MarioPics/bowserFireBallLeft.png").getImage().getScaledInstance(80, 40,Image.SCALE_SMOOTH);
	}
	
	public int getX()
	{
		return X;
	}
	
	public int getY()
	{
		return Y;
	}
	
	public int getHealth()
	{
		return health;
	}
	
	public int getSizeX()
	{
	    return sizeX;
	}
	
	public int getSizeY()
	{
	    return sizeY;
	}
	
	public int getFireDist()
	{
		return fireDist;
	}
	
	public boolean getAttackCD()
	{
	    return attackCD;
	}
	
	public boolean getLeft()
	{
	    return left;
	}
	
	public boolean getRight()
	{
	    return right;
	}
	
	public boolean getPunch()
	{
	    return punch;
	}
	
	public boolean getFireball()
	{
	    return fireball;
	}
	
	public boolean getKilled()
	{
	    return killed;
	}
	
	public void addX(int n)
	{
		X += n;
	}
	
	public void damage(int n)
	{
		health -= n;
	}
	
	public void setAttackCD(boolean bool)
	{
		attackCD = bool;
	}
	
	public void setLeft(boolean bool)
	{
		left = bool;
	}
	
	public void setRight(boolean bool)
	{
		right = bool;
	}
	
	public void setPunch(boolean bool)
	{
		punch = bool;
	}
	
	public void attackCD() //Cooldown for when Mario jumps on Bowser's head
	{
		attackCount++;
		if(attackCount>=30)
		{
			attackCount = 0;
			attackCD = false;
		}
	}
	
	public void draw(Graphics g)
	{
		if(fireCount<70) //Cooldown between fireballs
		{
			fireCount++;	
		}
		if(right)
		{
			g.drawImage(bowserRight, X+5, Y, null);
		}
		if(left)
		{
			g.drawImage(bowserLeft, X-15, Y, null);
		}
		if(fireCount==70) 	
		{	
			if(right)
			{
				fireDist+=5; //distance of the fireball from bowser
				g.drawImage(fireballRight, X+fireDist, Y+10, null);
			}
			if(left)
			{
				fireDist-=5;
				g.drawImage(fireballLeft, X+fireDist, Y+10, null);
			}
		}

		if(Math.abs(fireDist)==300) //reset distance
		{
			fireDist=0;
			fireCount=0;
			totalFireDist+= 300;
		}
		if(totalFireDist>=1200) //once 4 fireballs have been shot, Bowser switches to tracking Mario down and using Punch attack when he is range	
		{
			fireball = false;
			totalFireDist = 0;
		}
	}
	
	public void drawWalk(Graphics g)
	{
		if(punch) //Bowser cannot walk while he is punching
		{
			return;
		}
		Graphics2D g2d = (Graphics2D) g;
		if(left)
		{
			g2d.drawImage(walkPics.get(frames), X+sizeX, Y, -sizeX, sizeY,  null); //flipping the images using Graphics2D
		}		
		if(right)
		{
			g.drawImage(walkPics.get(frames), X, Y, null);
		}
		frames++;
		if(frames==80)
		{
			frames=0;
		}
	}
	
	public void drawPunch(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		if(left)
		{
			g2d.drawImage(punchPics.get(frames), X+sizeX, Y, -sizeX, sizeY,  null);
		}		
		if(right)
		{
			g.drawImage(punchPics.get(frames), X, Y, null);
		}
		frames++;
		if(frames==140)
		{
			frames = 0; 
			totalFrames++;
			punch = false; //after each punch, Bowser checks to see where Mario is and moves accordingly if needed
		} 
		if(totalFrames*140>=420) //after 3 punches, he switches back to fireballs 
		{
			System.out.println(true);
			punch = false;
			fireball = true;
			totalFrames = 0;
			frames = 0;
		}
	}
	
	public void drawDeath(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		if(!killed)
		{
			if(left)
			{
				g2d.drawImage(deadPics.get(deathFrames), X+sizeX, Y, -sizeX, sizeY,  null); 
			}
			if(right)
			{
				g.drawImage(deadPics.get(deathFrames), X, Y, null);
			}
		}
			
		if(deathFrames==50) 
		{
			Y+=5; //Decreasing bowser's height later on in his death animation because he is falling to the ground
		}
		deathFrames++;
		if(deathFrames==60)
		{	
			deathFrames = 0;
			totalFrames++;
		}
		if(totalFrames*60>=120) //this ensures two loops of the death animation because the animation takes time to load
		{
			killed = true; //after the animation is done, this allows the screen to be set to the exit screen
		}
	}
}