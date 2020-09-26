import pygame
import brettClass
import time



antall = 20

data = "\n"


for nummer in range(0, 20):

	totSum = 0
	bredde = 500
	hoyde = 500
	
	spillListe = []
	høyScore = 0
	Score2 = 0
	Score3 = 0
	høyIndex = None
	Index2 = None
	Index3 = None
	spillListe.append(brettClass.Brett(int(bredde / 10), int(hoyde / 10), 1))
	spillListe.append(brettClass.Brett(int(bredde / 10), int(hoyde / 10), 2))

	for _ in range(0, antall):
		spillListe.append(brettClass.Brett(int(bredde / 10), int(hoyde / 10), 0))
	
	for spill in spillListe:
	
		done = False
		retning = "hoyre"
		
		def koordinatsystem(plassering):
			return plassering * 10
		
		
		while not done:
		
			spill.bevege()
			if spill.erIkkeOK():
				done = True
			liste = spill.tegne()

		totSum += spill.getScore()
		if høyScore < spill.getScore():
			Score3 = Score2
			Index3 = Index2
			Score2 = høyScore
			Index2 = høyIndex
			høyScore = spill.getScore()
			høyIndex = spill
			print("Denne er den beste, score: {}".format(spill.getScore()))
		elif Score2 < spill.getScore():
			Score2 = spill.getScore()
			Index2 = spill
			print("denne er nest best, score: {}".format(spill.getScore()))
		elif Score3 < spill.getScore():
			Score3 = spill.getScore()
			Index3 = spill
	
	fil = open("SnakeV2_Bias1.txt", "w")
	for n in høyIndex.getBias():
		string = ""
		for x in n:
			string += str(x) + ","
		string = string[:-1]
		string += "\n"
		
		fil.write(string)
	fil.close()
	print("Kilde 1 har blitt oppdatert")
	
	fil = open("SnakeV2_Bias2.txt", "w")
	for n in Index2.getBias():
		string = ""
		for x in n:
			string += str(x) + ","
		string = string[:-1]
		string += "\n"
		
		fil.write(string)
	fil.close()
	print("Kilde 2 har blitt oppdatert")

	fil = open("SnakeV2_Bias3.txt", "w")
	for n in Index3.getBias():
		string = ""
		for x in n:
			string += str(x) + ","
		string = string[:-1]
		string += "\n"
		
		fil.write(string)
	fil.close()
	print("Kilde 3 har blitt oppdatert")

	print("gjennomsnittet var: {}".format(totSum / (antall + 2)))

	data = data + str((høyScore + Score2) / 2) + ","

	print("")
	print("")
	print("")
	print("Ny runde")
	print("nummer: {}".format(nummer))

data = data[:-1]
fil = open("graph.txt", "a")

fil.write(data)
fil.close()

print("Ferdig")


