
class Celle:
	def __init__(self):

		self._status = 0

	def __str__(self):
		return str(self._status)

	def settEple(self):
		ret = False
		if self._status == 0:
			self._status = -1
			ret = True
		return ret

	def settTom(self):
		self._status = 0

	def settVegg(self):
		if self._status == 1:
			return True
		else:
			self._status = 1
			return False

	def hentStatus(self):
		return self._status