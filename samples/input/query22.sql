SELECT * FROM Boats, Reserves, Sailors WHERE Boats.D = Reserves.H AND Boats.E = 3 AND Reserves.G = Sailors.A AND Reserves.H >= 101 AND Boats.E = Sailors.A AND Reserves.H <= 103;