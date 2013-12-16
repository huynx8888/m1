/**
 *  encendie
 *  Author: NGUYEN Van Tho
 *  Description: 
 */

model incendie

global {
//	the default configuration
	file shape_file_obstacles <- file('../includes/gisdata/obstacles.shp');
	file shape_file_bounds <- file('../includes/gisdata/bounds.shp');	
	file shape_file_ports <- file('../includes/gisdata/ports.shp');
	file shape_file_alarms <- file('../includes/gisdata/alarms.shp');
////	the configuration proposed 1 
//	file shape_file_obstacles <- file('../includes/planpropose1/obstacles.shp');
//	file shape_file_bounds <- file('../includes/planpropose1/bounds.shp');	
//	file shape_file_ports <- file('../includes/planpropose1/ports.shp');
//	file shape_file_alarms <- file('../includes/planpropose1/alarms.shp');

////	the configuration proposed 2 
//	file shape_file_obstacles <- file('../includes/planpropose2/obstacles.shp');
//	file shape_file_bounds <- file('../includes/planpropose2/bounds.shp');	
//	file shape_file_ports <- file('../includes/planpropose2/ports.shp');
//	file shape_file_alarms <- file('../includes/planpropose2/alarms.shp');

	
	int nb_people <- 300;
	
	//string smoke_image <- '../images/smoke.gif';
	float max_human_size <- 2.0;
	float min_human_size <- 1.2;
	
	float max_power <- 10.0;
	float min_initial_power <- 5.0;	
	float max_speed <- 2.0;
	float min_speed <- 1.0;
	
	float min_size_of_smoke <- 2.0;
	float max_size_of_smoke <- 4.0;
	
	float wander_speed min: 0.1 max:0.7;
	
	float speed_of_smoke <- 2.0 ;
	
	list obstacles of: obstacle;
	background bg;
	bool is_burning;
	bool is_alarm <- false;
	
	float burning_probability <- 0.1;
	
	float propagation_probability <- 0.02;
	float create_smoke_speed_ <- 0.3; // create smoke each 3s
	
	float fire_range_of_affect <- 5.0;
	float smoke_range_of_affect <- 10.0;
	bool fire_detected <- false;
		
	float alpha <- 0.1;
	float beta <- 0.07;
	//geometry qqch;
	
	int nb_dead <- 0;
	float total_power_at_begin <- 0.0;
	float total_power_at_exit <-0.0;	
	float total_exit_time <- 0.0;
	
	init{
		
		create obstacle from: shape_file_obstacles returns: list_of_obstacle;		
		create background from: shape_file_bounds returns: list_of_bg;
		create alarm from: shape_file_alarms ;
		create port from: shape_file_ports ;
		
		set obstacles <- obstacles union list(list_of_obstacle);
		set bg <- first(list_of_bg);
		ask target:bg {
			set shape <- self - obstacles;
		}		
		
		let ports type: list of: port <- list(port);
		create people number: nb_people{
			set location <- any_location_in (bg);
			set ports <- ports sort_by(self distance_to each);
			set total_power_at_begin <- total_power_at_begin + power;
			set the_target <- first(ports);	
		}
	}
}

entities {
	species obstacle  {
		rgb color <- rgb([200, 200, 200]);
		aspect base {
			draw geometry: shape color: color ;
		}
		
		reflex create_flame when: !is_burning {
			if (flip (burning_probability)){			
				create fire{
					set location <- myself.location;
				}
				set is_burning <- true;
			}
		}
	}
	
	species port skills: [situated]{
		rgb color <- rgb('green')  ;
		aspect base{
			draw geometry: shape color: color ;		
		}		
	}
	
	species fire skills: [situated] {
		rgb color <- rgb ([ rnd (155) + 100, 0 , 0 ]) ;
		float size <- 4.0;
		float range_of_affect <- fire_range_of_affect;
		float create_smoke_speed <- create_smoke_speed_;
		bool is_smoke_created <- false;
		aspect base{
			draw shape : triangle size : size color : color ;
		}
		
		reflex create_smoke{
			//create smoke;
			if (flip (create_smoke_speed)){		
				create smoke{
					set location <- myself.location;
				}
			}
		}
		reflex propager{
			let candidates type: list of: obstacle <- ((self neighbours_at range_of_affect) of_species obstacle );
			let good_candidates <- candidates - fire overlapping(candidates);
			//let good_candidates type: list of: obstacle <- candidates where
			
			if (flip(propagation_probability)){
				let candidate <- one_of (good_candidates);
				ask target: candidate{
					create fire{
						set location <- any_location_in(myself);
					}
				}
			} 
		}
	}
	
	species smoke skills: [situated, moving] {
		rgb color <- rgb('gray');
		float speed <- speed_of_smoke;
		float range_affected <- smoke_range_of_affect;
		float size <- rnd(max_size_of_smoke - min_size_of_smoke) + min_size_of_smoke;
		aspect base{
			draw shape : circle size : size color : color ;
			//draw geometry: shape color: color ;		
		}
//		aspect image {
//			draw image: smoke_image size: 35;      
//		}
		
		reflex do_move {
			do wander speed: speed;
		}
	}
	
	species background skills:[situated]{
		rgb color <- rgb('pink')  ;
		aspect base{
			draw geometry: shape color: color ;		
		}
	}	
	species alarm skills:[situated]{
		float range <- 55.0;
		int duration <- 10;
		rgb color <- rgb('red');
		bool is_ringing <- false;	
		aspect base {
			draw geometry: shape color: color ;
		}
		
		reflex detect_fire when: !fire_detected{
			let fires type: list of: fire <- ((self neighbours_at range) of_species fire );
			if (fires != nil and ! empty(fires)){
				set is_ringing <- true;
				set fire_detected <- true;
				set is_alarm <- true;
			}
		}
		
		reflex receive_signal_of_other_alarm when: fire_detected and duration > 0 and !is_ringing{
			set is_ringing <- true;			
		}
		reflex ringing when: is_ringing{
			if(duration > 0){
				set duration <- duration -1;
				set color <- rgb ([ rnd (55) + 200, 0 , 0 ]) ;
			}else{
				set is_ringing <- false;
			}
		}
	}
	
	species people skills: [moving]{
		rgb color <- rgb('yellow');
		float size <- rnd(max_human_size - min_human_size) + min_human_size;
		//float power max: max_power min: min_initial_power;
		float power <- rnd(max_power - min_initial_power) + min_initial_power;		
		float speed <- rnd(max_speed - min_speed) + min_speed;
		float range <- 40.0;
		float range_of_observation <- 30.0;
		float direction <- 0.0;		
		geometry the_target;
		point delta <- {0, 0};		
		bool is_evacuate <- false;
		float time_begin_evacuate;
		
		aspect base {
			draw shape : circle size : size color : color ;
		}
				
		reflex do_wander when: !is_evacuate{
			//get alarmed			
			let fires type: list of: fire <- ((self neighbours_at range) of_species fire );
			if (fires != nil and ! empty(fires)){
				set is_evacuate <- true;		
			}
			//optimisation
			if(!is_evacuate){
				let smokes type: list of: smoke <- ((self neighbours_at range_of_observation) of_species smoke );
				if (smokes != nil and ! empty(smokes)){
					set is_evacuate <- true;				
				}
			}
			if(!is_evacuate){
				let alarms type: list of: alarm <- ((self neighbours_at range_of_observation) of_species alarm );
				if (alarms != nil and ! empty(alarms)){
					loop a over: alarms{
						if (a.is_ringing){
							set is_evacuate <- true;
							set time_begin_evacuate <- cycle;
							break;
						}
					}
				}				
			}
						
			do wander speed: wander_speed bounds: bg;
		}
		
		reflex do_move when: is_evacuate{  			
			//do goto target: the_target on: floor;
			
			if( the_target distance_to self.location < 2*speed ){
				do goto target: the_target on: bg;
			
			}else{
				do move heading: self towards the_target speed: speed bounds: bg;	
			}
			//do move heading: self towards the_target speed: 5;
		}
		
		reflex calculate_power when: is_evacuate{
			let fires type: list of: fire <- ((self neighbours_at fire_range_of_affect) of_species fire );			
			let smokes type: list of: smoke <- ((self neighbours_at smoke_range_of_affect) of_species smoke );
			let nb_fire <- fires count (each);
			let nb_smoke <- smokes count (each);
write "fires: " + nb_fire;
write "smoke: " + nb_smoke;
			let delta type: float <- 0.0;
			
			loop f over: fires{
				let dist type: float <- self distance_to f;
				set delta <- delta + alpha/dist; 	
			}
			
			loop s over: smokes{
				let dist type: float <- self distance_to s;
				set delta <- delta + beta/dist; 	
			}

			let old_power <-power;			
			set power <- power - delta;
			
			set speed <- speed * power / old_power;
			if ( power <= 0){
				do action: do_die;
			}		
		}
		action do_die {		
			create people_dead number : 1 {
				set location <- myself.location;
				set size <- myself.size;
			}
			set nb_dead <- nb_dead + 1;
			do action: die;		
		}
		
		reflex exit {
			if (self overlaps the_target){
				set total_power_at_exit <- total_power_at_exit + power;
				set total_exit_time <- total_exit_time + cycle - time_begin_evacuate;
				do die;
			}
		}
		
		// Avoid colision between people
		reflex flee_people {
			//let closes type: list of: people <- people overlapping(self);	
			let closes type: list of: people <-  (self neighbours_at max_human_size of_species people);
			loop close over: closes{
				do move speed: speed/2 heading: self towards close + rnd(180)+ 45 bounds: bg;
			}		
		}
	}
	species people_dead skills : [ situated ] {
		rgb color <- rgb ( 'black' ) ;
		float size <- 1.0 ;
		aspect base {
			draw shape : circle size : size color : color ;
		}
	}
}

environment bounds: shape_file_bounds ;
experiment incendie type: gui {
	output {		
		inspect name:'Agent' type: agent refresh_every: 1;
		inspect name:'Species' type:species refresh_every: 1;			
		display building_display refresh_every: 1 {
//			species building aspect: base ;			
			species background aspect : base;
//			species floor aspect: base ;
			species obstacle aspect: base ;
			species port aspect: base ;
			species alarm aspect: base ;
			species fire aspect: base;
			species smoke aspect: base ;			
			species people aspect: base ;
			species people_dead aspect: base ;					
		}		
		monitor name:"number of death:" value: nb_dead;
		monitor name:"number of survival:" value: nb_people - nb_dead;
		monitor name:"means power at exit:" value: total_power_at_exit*100/total_power_at_begin;
		monitor name:"means time for exit:" value: total_exit_time/(nb_people - nb_dead);
//write string(nb_people - nb_dead) + "" + 1;
//write (total_power_at_begin - total_power_at_exit)*100/total_power_at_begin;

		monitor name: "total critere:" value: string(nb_people - nb_dead)  + " " + string(total_power_at_exit*100/total_power_at_begin) + " " +string (total_exit_time/(nb_people - nb_dead));		
	}
}