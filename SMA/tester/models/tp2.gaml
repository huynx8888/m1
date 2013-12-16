/**
 *  tp2
 *  Author: tom
 *  Description: 
 */

model tp2

/* Insert your model definition here */

environment width:100 height:100 torus:false;

//display name:"tree_distribution";
experiment tp2 type : gui {
	output {
/*		display tree_distribution refresh_every: 1{
			chart td_diagram type : pie {
				data under_4m value : length ( list ( tree ) where ( each . size < 4 ) ) ;
				data from_4m_to_7m value : length ( list ( tree ) where ( each . size >= 4
				and each . size < 7 ) ) ;
				data over_7m value : length ( list ( tree ) where ( each . size >= 7 ) ) ;
			}
		}
 
*/
		display forest refresh_every: 1{
			species tree aspect: default;
			species pompier aspect: enter_forest;
			// among list (tree);
		}
	}

}

global {
	//float size_of_trees min
	float MAX_SIZE <- 10.0;	
	int dimensions <- 20 ;
	float DESTROYED_SIZE <- 3.0;
	rgb DESTROYED_COLOR <- rgb('black');
	int init_tree_on_fire <- rnd(2) + 1;
//	int speed <- 5;
	float speed_of_agents min: 0.1  <- 0.5 ;
	float proba_propagation <- 0.0001;
	const range_of_protection type: int init: 10;		
	init {
		create tree number: 100{}		
		create pompier number: 2{}
		ask target: init_tree_on_fire among list (tree) {
			set state <- 'burning';
			//set color <- rgb('red');		
		}
	}
}


entities {
	inspect name:'Agent' type: agent;
	inspect name:'Species' type:species;
	species name: tree skills : [ situated , visibe ] control: fsm{
		rgb color <- rgb ([ 0 , rnd (55) + 200 , 0 ]) ;
		//const color type : rgb init : rgb ( 'green' ) ;
		float size <- rnd ( MAX_SIZE ) + 1.0 ;
		//const size type : float init : 5.0 ;
		const location type : point init : [ rnd ( 100 - 2 * size ) + size , rnd ( 100 - 2 * size ) + size ] ;
		//const color_burning type: rgb init: rgb ([ 255 , rnd (255) , 0 ]);
		int duration;
		const threshold type: int init: 5999;
		const range type: int init:20;		
		aspect base {
			draw shape : circle size : size color : color ;
		}
		aspect default {
			draw shape: circle size: size color: color ;
			//set state <- 'burning';
		}
		
		state 'intact' initial: true{
			//draw shape : circle size : size color : color ;
			set duration <- 0;
		}
		
		state 'burning'{
			set duration <- duration + 1;
			set color <- rgb ([ 255 , rnd (255) , 0 ]);
			//set color <- rgb ('red');
			
			//if(duration > rnd(5000) + 10000 ){
			let candidates type: list of: tree <- ((self neighbours_at range) of_species tree );
			if ! (empty(candidates)) {
				ask candidates {
					if (state = 'intact'){
						if( flip(proba_propagation)){
							set state <- 'burning';
						}
					}
				}	
			}
			
			let pompiers type: list of: pompier <- ((self neighbours_at range_of_protection) of_species pompier );
//			transition to: protected when : ( pompiers != nil );
//			transition to: destroyed when : ( duration > threshold );
			//write pompiers;			
			if !(empty(pompiers)){
//				write "not empty pompier";
//				transition to: protected when : (1 = 1 );
//				write state;
												
				set state <- 'protected';
				//transition to: protected when : (1 = 1 );
//				write state;
			}
//			write "outside";
			//}
			
			transition to: destroyed when : ( duration > threshold );
		}
		
		state 'protected' {
			set color <- rgb(blue);
			set duration <- duration - 1;
			transition to: intact when : (duration = 0);
		}
		
		state 'destroyed'{
			set color <- rgb('black');
			set size <- 3;
			//draw shape : circle size : DESTROYED_SIZE color : color ;
		}
		
	}
	
	species pompier skills : [ visible, moving, situated ] {
		rgb color <- rgb ( 'red' ) ;
		const speed type : float <- speed_of_agents ;
		const range type: int init: 30;
		point position <- point([0, 0]); 
		point next <- point([0, 0]);
		point zero <- point([0, 0]);
		tree goal <- nil;
		int rayon_perception <- range;
		reflex find_a_goal {
			if (goal = nil){
				//do goto: zero;
				do wander speed: speed - 10 amplitude: 120;
				let candidates type: list of: tree <- ((self neighbours_at range) of_species tree ) sort_by(self distance_to each);
//				let nearest value: first(list(tree) where (each.state = 'burning') sort_by(self distance_to each));
//
//				if ( nearest != nil){
//					set goal <- nearest; 
//				}
				set goal <- first (candidates);
			}
		}
		
		reflex verify_goal {
			if (goal != nil and (goal.state != 'burning' or goal.state != 'protected')){
				set goal <- nil;
			}
		}
		reflex goto_fire {
			if (goal != nil and self distance_to goal > range_of_protection){
				do goto target: goal speed:speed;			
			}
		}
		
//		reflex do_watering {
//			if (goal != nil and self distance_to goal <= range_of_protection){
//				set goal.state <- 'protected';
//			}
//		}
		aspect enter_forest {			
			draw shape : square size: 4.0 color : color;
		}
	}

}


