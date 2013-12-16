/**
 *  tp3
 *  Author: tom
 *  Description: 
 */

model tp3

/* Insert your model definition here */

environment width:100 height:100 torus:false;

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
//	user_command kill_myself {
//	    do action: die;
//	}		
	init {
		create tree number: 100{}
		//create pompier number: 2{}
		
		ask target : init_tree_on_fire among list ( tree ) {
		//set state <- 'burning';
		//set color <- rgb('red');
			do action: change_to_burning;
		}
	}
}


entities {
	inspect name:'Agent' type: agent;
	inspect name:'Species' type:species;
	species name: tree skills : [ situated , visibe ] control: fsm{
		rgb color <- rgb ([ 0 , rnd (55) + 200 , 0 ]) ;
		float size <- rnd ( MAX_SIZE ) + 1.0 ;
		const location type : point init : [ rnd ( 100 - 2 * size ) + size , rnd ( 100 - 2 * size ) + size ] ;
		int duration;
		const threshold type: int init: 1999;
		const range type: int init:20;

		aspect base {
			draw shape : circle size : size color : color ;
		}
		aspect default {
			draw shape: circle size: size color: color ;
			//set state <- 'burning';
		}	
		
		action change_to_burning{			
			create tree_burning number : 1 {
				set location <- myself.location;
				set size <- myself.size;
			}
			do action: die;
		}
	}
	
	species name: tree_burning skills : [ situated , visibe ]{		
		rgb color <- rgb ([ 255 , rnd (255) , 0 ]);
		//float size <- rnd ( MAX_SIZE ) + 1.0 ;
		//const location type : point init : [ rnd ( 100 - 2 * size ) + size , rnd ( 100 - 2 * size ) + size ] ;
		float size;
		point location;
		int duration;
		const threshold type: int init: 1999;
		const range type: int init:20;

		aspect base {
			draw shape : circle size : size color : color ;
		}
		aspect default {
			draw shape: circle size: size color: color ;
			//set state <- 'burning';
		}
		
		reflex progage {
			set duration <- duration + 1;
			set color <- rgb ([ 255 , rnd (255) , 0 ]);
			//set color <- rgb ('red');
			
			//if(duration > rnd(5000) + 10000 ){
			let candidates type: list of: tree <- ((self neighbours_at range) of_species tree );
			if ! (empty(candidates)) {
				ask candidates {
					
					if( flip(proba_propagation)){
						do action:change_to_burning;
					}
				}	
			}
			
			if( duration > threshold ){
				do action: dead;
			}
		}
		
		action dead{
			create tree_dead number : 1 {
				set location <- myself.location;
				set size <- myself.size;
			}
			
			do action: die;
		}
	}

	species name: tree_dead skills : [ situated , visibe ]{		
		rgb color <- rgb ('black');
		float size;
		point location;

		aspect base {
			draw shape : circle size : size color : color ;
		}
		aspect default {
			draw shape: circle size: size color: color ;
			//set state <- 'burning';
		}		
	}	
	
	species pompier skills : [ humanmoving, moving ] control: humanmoving {
		rgb color <- rgb ( 'red' ) ;
		const speed type : float <- speed_of_agents ;
		const range type: int init: 30;
		point position <- point([0, 0]); 
		point next <- point([0, 0]);
		point zero <- point([0, 0]);
		aspect enter_forest {			
			draw shape : square size: 4.0 color : color;
		}
	}

}


//display name:"tree_distribution";
experiment tp3 type : gui {
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
			species tree_burning aspect: default;
			species tree_dead aspect: default;
			species pompier aspect: enter_forest;
			// among list (tree);
		}
	}

}
