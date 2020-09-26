
class Celle:
	def __init__(self):
		#setter status til "død"
		self._status = "død"

	#printer ut tegnet til statusen
	def __str__(self):
		return self.hentStatusTegn()

	#setter statusen til død
	def settDoed(self):
		self._status = "død"

	#setter statusen til levende
	def settLevende(self):
		self._status = "levende"

	def erLevende(self):
		return self._status == "levende"

	#returnerer det som skal tegnes i brettet
	def hentStatusTegn(self):
		#returnerer "." hvis cellen er død
		if self._status == "død":
			return "."
		#returnerer "O" hvis cellen lever
		else:
			return "O"