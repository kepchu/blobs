package app;

import java.awt.EventQueue;

import data.FrameBuffer;
import data.World;
import view.ViewAndInputController;

public class Blobs011 {

	public static void main (String[] args) {	
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new Blobs011();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	
	public Blobs011 () {
		
		System.out.println("Blobs01 thread - " + Thread.currentThread().getName());
		//MVC pattern variation
		World dc = new World();
		ViewAndInputController v = new ViewAndInputController(FrameBuffer.getInstance());
		new Logic(dc, v);
		
	}
	
}

//00: create square root alternative for VecMath and use it wherever possible &
//	use provider - client pattern with notifyAll for display buffer

//0: wrap around, zoom by ctrl + mouse wheel or +/-, 
//	switchable gravity to centre of the window, make debris "rain-fireworks"

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