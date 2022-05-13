(define (domain sokoban)

	(:requirements :strips :typing  :negative-preconditions)

	(:types caisse joueur cible vide - case
			vertical horizontal - position
	)

	(:predicates 	(at ?ca - case ?v - vertical ?h - horizontal)
					(estCaisse ?v - vertical ?h - horizontal)
					(estACote ?v1 - vertical ?h1 - horizontal ?v2 - vertical ?h2 - horizontal)
					(estMemeLigne ?vO - vertical ?vN - vertical)
					(estMemeColonne ?hO - horizontal ?hN - horizontal)

	)

	(:action MoveCaisseColonne
		:parameters (?J - joueur ?vO - vertical ?hO - horizontal ?ca - caisse ?vN - vertical ?hN - horizontal ?vP - vertical ?hP - horizontal)
		:precondition (and
			(at ?J ?vO ?hO) ;; Le joueur est sur la case de depart
			(estACote ?vO ?hO ?vN ?hN) ;; La case ou il veut aller est a cote de la sienne
			(estCaisse ?vN ?hN) ;; La case d'arrive contient une caisse
			(at ?ca ?vN ?hN) ;; La bonne caisse est dans la case
			(not (estCaisse ?vP ?hP)) ;; La case où l'on pousse la caisse n'est pas déjà occupé
			(estACote ?vN ?hN ?vP ?hP) ;; La caisse est a cote de la case ou elle serait poussee
			(estMemeColonne ?hO ?hN) ;; Le joueur, la caisse et la destination de la caisse sont tous sur la même colonne
			(estMemeColonne ?hN ?hP)
            (not (at ?J ?vP ?hP))
		)
		:effect (and
			(not (at ?J ?vO ?hO)) ;; Le joueur n'est plus a sa position
			(at ?J ?vN ?hN) ;; Le joueur se deplace
			(not (estCaisse ?vN ?hN)) ;; La caisse n'est plus à sa position
			(not (at ?ca ?vN ?hN))
			(estCaisse ?vP ?hP) ;; La caisse se déplace
			(at ?ca ?vP ?hP)
		)
	)

	(:action MoveCaisseLigne
		:parameters (?J - joueur ?vO - vertical ?hO - horizontal ?ca - caisse ?vN - vertical ?hN - horizontal ?vP - vertical ?hP - horizontal)
		:precondition (and
			(at ?J ?vO ?hO) ;; Le joueur est sur la case de depart
			(estACote ?vO ?hO ?vN ?hN) ;; La case ou il veut aller est a cote de la sienne
			(estCaisse ?vN ?hN) ;; La case d'arrive contient une caisse
			(at ?ca ?vN ?hN) ;; La bonne caisse est dans la case
			(not (estCaisse ?vP ?hP)) ;; La case où l'on pousse la caisse n'est pas déjà occupé
			(estACote ?vN ?hN ?vP ?hP) ;; La caisse est a cote de la case ou elle serait poussee
			(estMemeLigne ?vO ?vN) ;; Le joueur, la caisse et la destination de la caisse sont tous sur la même ligne
			(estMemeLigne ?vN ?vP)
            (not (at ?J ?vP ?hP))
		)
		:effect (and
			(not (at ?J ?vO ?hO)) ;; Le joueur n'est plus a sa position
			(at ?J ?vN ?hN) ;; Le joueur se deplace
			(not (estCaisse ?vN ?hN)) ;; La caisse n'est plus à sa position
			(not (at ?ca ?vN ?hN))
			(estCaisse ?vP ?hP) ;; La caisse se déplace
			(at ?ca ?vP ?hP)
		)
	)

	(:action Move
		:parameters (?J - joueur ?vO - vertical ?hO - horizontal ?vN - vertical ?hN - horizontal)
		:precondition (and
			(at ?J ?vO ?hO) ;; Le joueur est sur la case de depart
			(estACote ?vO ?hO ?vN ?hN) ;; La case ou il veut aller est a cote de la sienne
			(not (estCaisse ?vN ?hN)) ;; La case d'arrive n'est pas une caisse)
		)
		:effect (and
			(not (at ?J ?vO ?hO)) ;; Le joueur n'est plus a sa position
			(at ?J ?vN ?hN) ;; Le joueur se deplace
		)
	)
)


