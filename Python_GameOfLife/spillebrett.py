#importerer klasser som programmet skal bruke
from celle import Celle
from random import randint

class Spillebrett:
	def __init__(self, rader, kolonner):
		self._kolonner = kolonner
		self._rader = rader
		self._spillbrett = self.lagBrett()
		self.generer()
		self._generasjon = 1

	def __str__(self):
		def antallLevende():
			antallLevende = 0
			for n in self._spillbrett:
				for i in n:
					if i.erLevende():
						antallLevende += 1
			return antallLevende
				
		return "Generasjon: {} - Antall levende: {}".format(self._generasjon, antallLevende())


		return string

	def lagBrett(self):
		brett = []
		for i in range(0, self._rader):
			brett.append([])
			for _ in range(0, self._kolonner):
				brett[i].append(Celle())
		return brett

	def generer(self):
		for i in range(0, len(self._spillbrett)):
			for n in self._spillbrett[i]:
				if randint(1,3) == 1:
					n.settLevende()

	def getBrett(self):
		return self._spillbrett


	def finnNabo(self, rad, kolonne):
		naboer = []
		sjekkRad = rad - 1

		for i in range(0, 3):
			for n in range(-1, 2):
				try:
					if (sjekkRad >= 0 and (kolonne + n) >= 0) and (sjekkRad != rad or n != 0):
						x = self._spillbrett[sjekkRad][kolonne + n]
						naboer.append(x)
				except:
					pass
			sjekkRad += 1

		return naboer

	def oppdatering(self):
		def antallLevendeListe(rad, kol):
			naboer = []
			for n in self.finnNabo(rad, kol):
				naboer.append(n.erLevende())
			return naboer


		skalLeve = []
		skalDø = []
		for rad in range(0, len(self._spillbrett)):
			for kol in range(0, len(self._spillbrett[rad])):
				if self._spillbrett[rad][kol].erLevende():
					if not 2 <= antallLevendeListe(rad, kol).count(True) <= 3:
						skalDø.append(self._spillbrett[rad][kol])
				else:
					if antallLevendeListe(rad, kol).count(True) == 3:
						skalLeve.append(self._spillbrett[rad][kol])

		for n in skalLeve:
			n.settLevende()
		for n in skalDø:
			n.settDoed()

		self._generasjon += 1