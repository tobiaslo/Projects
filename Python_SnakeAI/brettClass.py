import random
from celleClass import Celle
import math
import MlClass 


class Brett:
	def __init__(self, rad, kol, tilfeldig):
		self._rad = rad
		self._kol = kol
		self._score = 0
		self._slange = [[25, 25], [25, 24]]
		self._IkkeOK = False
		self._eple = self.lagEple()
		self._lengde = 10
		self._brett = self._lagBrett()
		self._tilfeldig = tilfeldig
		self._ML = MlClass.MachinLearning(self._tilfeldig)
		self._moves = 250

	def __str__(self):
		return str(self._slange)


	def _lagBrett(self):
		brett = []

		#Lager antall rader som verdien til variablen _rader
		for i in range(0, self._rad):
			brett.append([])
			#Legger til antall "celle" objekter som verdien til variablen _kolonner
			for n in range(0, self._kol):
				brett[i].append(Celle())

		for celle in brett[0]:
			celle.settVegg()
		for celle in brett[self._rad - 1]:
			celle.settVegg()
		for i in range(0, self._rad):
			brett[i][0].settVegg()
			brett[i][self._kol - 1].settVegg()


		for index in range(0, len(self._slange)):
			posX = self._slange[index][0]
			posY = self._slange[index][1]
			if brett[posX][posY].settVegg():
				self._IkkeOK = True

		self._score += 1
		self._moves -= 1
		if self._moves <= 0:
			self._IkkeOK = True
		brett[self._eple[0]][self._eple[1]].settEple()

		return brett


	def LagInputs(self):
		inn = []
		inn.append(self._brett[self._slange[0][0] + 1][self._slange[0][1]].hentStatus())
		inn.append(self._brett[self._slange[0][0] - 1][self._slange[0][1]].hentStatus())
		inn.append(self._brett[self._slange[0][0]][self._slange[0][1] + 1].hentStatus())
		inn.append(self._brett[self._slange[0][0]][self._slange[0][1] - 1].hentStatus())

		if self.dist(self._slange[0][0], self._slange[0][1]) > self.dist(self._slange[0][0] + 1, self._slange[0][1]):
			inn.append(1)
		else:
			inn.append(0)

		if self.dist(self._slange[0][0], self._slange[0][1]) > self.dist(self._slange[0][0] - 1, self._slange[0][1]):
			inn.append(1)
		else:
			inn.append(0)

		if self.dist(self._slange[0][0], self._slange[0][1]) > self.dist(self._slange[0][0], self._slange[0][1] + 1):
			inn.append(1)
		else:
			inn.append(0)

		if self.dist(self._slange[0][0], self._slange[0][1]) > self.dist(self._slange[0][0], self._slange[0][1] - 1):
			inn.append(1)
		else:
			inn.append(0)

		return inn


	def bevege(self):
		retning = self._ML.styre(self.LagInputs())
		if self._ML.getIngen():
			self._score -= 30


		if retning == "ned":
			pos1 = [self._slange[0][0] + 1, self._slange[0][1]]

		elif retning == "opp":
			pos1 = [self._slange[0][0] - 1, self._slange[0][1]]

		elif retning == "venstre":
			pos1 = [self._slange[0][0], self._slange[0][1] - 1]

		elif retning == "hoyre":
			pos1 = [self._slange[0][0], self._slange[0][1] + 1]

		self._slange.insert(0, pos1)
		if len(self._slange) > self._lengde:
			self._slange.pop(-1)

		self._brett = self._lagBrett()

		if self.dist(self._slange[0][0], self._slange[0][1]) == 0:
			self._eple = self.lagEple()
			self._lengde += 3

	def lagEple(self):
		pos = [random.randint(1, self._kol - 2), random.randint(1, self._rad - 2)]
		self._score += 100
		self._moves = 500
		return pos

	def dist(self, koX, koY):
		liste = [self._eple[0] - koX, self._eple[1] - koY]
		return math.sqrt((liste[0] ** 2) + liste[1] ** 2)


	def erIkkeOK(self):
		return self._IkkeOK

	def tegne(self):
		return self._brett

	def getBias(self):
		return self._ML.getBias()

	def getScore(self):
		return self._score













