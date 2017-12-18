package app;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import data.FrameBuffer;
import data.World;
import view.ViewAndInputController;

public class BlobsD3 {

	
	
	public static void main (String[] args) {
		
		 /* Use an appropriate Look and Feel */
	    try {
	        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
	        //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
	    } catch (UnsupportedLookAndFeelException ex) {
	        ex.printStackTrace();
	    } catch (IllegalAccessException ex) {
	        ex.printStackTrace();
	    } catch (InstantiationException ex) {
	        ex.printStackTrace();
	    } catch (ClassNotFoundException ex) {
	        ex.printStackTrace();
	    }
	    /* Turn off metal's use bold fonts */
	    UIManager.put("swing.boldMetal", Boolean.FALSE);
		
	    javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new BlobsD3();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	
	public BlobsD3 () {	
		System.out.println("Blobs01 thread - " + Thread.currentThread().getName());
		//MVC pattern variation
		World dc = new World();
		ViewAndInputController v = new ViewAndInputController(FrameBuffer.getInstance());
		new Loop(dc, v);		
	}
}

//00: create square root alternative for VecMath and use it wherever possible &
//	switchable gfx: draw blobs as same size "flies" - this should look like  
//OK 0: wrap around, zoom by ctrl + mouse wheel or +/-, 
//	 make debris "rain-fireworks"
// !all the charge-point changes applied to mouse pointer.
// independent charge point created on click and undo button


/*G@MES:
 * 1. "pool" - Blobs of 2 or more colours randomly placed in window. Blobs have random sizes and positions
 * but sums of contents of each "team" of blobs are equal. These sums are displayed as (initially equal) bars.
 * P1 punches a hole in a blob choosing P1's colour.Content oozing through the hole propels the blob.
 * First blob of P1's colour that leaves the window marks the edge it crosses as target.
 * Content of blobs crossing the target edge become a player's score.
 * 
 * 2. "flies": players' directional input is translated to creation and destruction of "charge points"
 * 	that pull player's fly. The fly at each time "orbits" at least 2 charges - continuous movement.
 *  flies movement is swift horizontally but it's difficult to gain height. Gravity helps to "drop down"
 *  to attack other flies. When flies "collide" (collision circle bigger than flies itself) the fly that is lower
 *  loses control / becomes inert for  a short time. Each fly that gets below certain height dies.
 * 
 * 3. "platform game" - Vertical scrolling. Screen populated by slowly moving blobs of random sizes, positions,
 * directions and colour categories. Player(s) jump from blob to blob trying to get as high as possible as
 * fast as possible.
 * Interactions with blobs-platforms: 
 * 
 * 4. shooting small blobs that become inside collidees of hit blob - this disrupts control.
 */


//01: a cloud-swirl object: new ColDet that would disregard all collisions between blobs building a cloud
//		until a blob overlaps with only one other blob - then it would have to "hold on" to it to keep cloud's integrity

//A - draw line (mouseDragged) and change it into blobs (every nth pixel in an array...)
//B - make "gravity point" or explosion( "repulsion point") with mouseClick
//C - a click on blob results in a tiny hole. Blob's content escaping through the hole propels the blob;
//D - all that is not currently colliding die.
//		Blobs set to "debris collisions" and player somehow "shoots"/hits the "blob nest"

//E - steer agents by settings charges in the direction of movement and dispose old charges - nice effect
//F - RGB blobs consume smaller blobs but absorb only their own colour (R, G or B) + inside collisions and winning by colour


/*
 * GENERAL:
 * add easily sparks effect at Physics.prodCollPoint() locations
 * 
 * GAMES:
 * 
 * 1. guys coming from right, don't let them pass through the screen
 * 
 * 2. slow-motion big guys move uniformly from right creating
 * 	a terrain. you can only jump up. try to "climb up" the
 * terrain==don't get pushed back by it 
 * 
 * 3. original
 * 
 * 4. a) setup: like inheritance5 + mass
 * 		b) player has to stay on the screen
 * 		c)  player looses energy/mass when moving/jumping
 * 		d) player gets energy/mass for knocking out "attackers" off the screen
 * 			(attacker == just a random blob like in inheritance5)
 * 
 * 5. a)only left/right movement and one jump (for player).
 * 		b) player tries to bounce off of someone else in order to
 * 			get as high as possible (=="convert" horizontal speed into
 * 			vertical speed)
 * 		c) one standard jump or mid-air jump allowed
 * 
 * 6. setup a'la inheritance5.
 * 		task: get a target colour with timer
 * 		game:
 * 		a) player have to reach a designated colour animal QUICK
 * 		b) player have to keep a tight control in order not to "repaint"
 * 			other animals with itself as the next colour-to-get will be
 * 			chosen from existing other-colour animals.
 *  
 * 7. animals "hang" in the air. each time the player hits an animal
 * 		the JUMP ability is restored for 1 jump.
 * 		GOAL: climb up.
 * 
 * 8.	a) "Inside bubbles" like in build 76:
 * 			- they have lifting power
 * 			- they transfer energy back to parent when "not stirred"
 * 		b) A Blob has limited speed of transfer energy->size. If it eats
 * 			too fast/too much, bubbles/children appear inside
 * 		C) build76 "bug" - bigger blobs consume smaller blobs of the same family
 * 			when intersected -> if player can't contain "choroba kesonowa"/growth
 * 			of lifting "inside-bubbles" (by "not stirring" them) player can:
 * 			a) seek closed space/change movement to induce creation
 * 			of as big "children" as possible (to "save lineage" coz the goal is related
 * 			with growing big) by reducing scatter of "children" during "explosion"
 * 			a2) with right "transfer energy from smaller" multiplier one can
 * 				GAIN ON ENERGY after well conduced "explosion"
 * 				or
 * 			b) "over-feed" an enemy with itself (& it's soon-to-be-born children?)
 * 				or
 * 			c) use up energy quickly to inhibit production of children
 * 				aka: don't let anybody recycle me
 *  		inducing massive bubbles spawning in the enemy 
 * 		d) goal is related to: #eat/grow as fast as possible; #stuff to eat at bottom
 * 		
 * 8.b.	modification of game 8 (above)separate "heat level" increased when eating
 * 		(+ possibly when performing other actions). Children-bubbles do not suck
 * 		parent's energy. They behave just like gas bubbles == player-blob starts
 * 		to boil (so appropriate INCREASE in SIZE of the player/parent-blob
 * 		 should be applied).
 * 
 * 9. "Tactical" version of no 8 (above). Changes/additions:
 * 		- #two blobs per player#
 * 		- player can switch between her blobs
 * 		- #"slow-motion" speed (TODO)#
 * 		- player can hit the "flying-away" blob from above with the other blob to do stuff
 * 			(keeping it down, launch it at the enemy, launch to "explode" over rich pastures...)
 * 		- #incorporated optional "auto-switching" to the player's faster blob (-> confuse enemy by
 * 			hitting her slower blob and evoking surprising switch in the middle of doing things)#
 * 
 * 10.  	- "view from above"
 * 			- players inside big blob
 * 			- goal: push the big blob towards player's side/goal
 * 				(probably addition of "higher level stuff" like
 * 				fixed biggest-and-fixed-blob-arena "surrounded by abyss" plus
 * 				optional addition of surrounding baskets/goals
 *  
 *  11. zostan rojem:
 *  		- odpowiednio stuningowane transfery energii
 *  		- larva: eat plants/worms & grow
 *  		- swarm:  
 *  			- stop drawing parent & start "spray" children
 *  			- don't do anything special about survival of single children
 *  			  but leave is as it is in build761
 *  			- use another variable, .volume to keep recreation of children stable
 *  			- use invisible parent's energy radius*0.80 to detect collisions
 *				- suck energy from anything that's still a
 *					visible blob and is smaller than player
 *				- adjust invisible boundary/parent's radius to accommodate to victim's size
*/