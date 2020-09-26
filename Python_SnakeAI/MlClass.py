import random


class MachinLearning:
	def __init__(self, tilfeldig):
		self._tilfeldig = tilfeldig
		if self._tilfeldig == 0:
			self._bias = self.FilBias()
		elif self._tilfeldig == 1:
			self._bias = self.FilBias1()
		elif self._tilfeldig == 2:
			self._bias = self.FilBias1()
		self._ingen = False

	def FilBias(self):
		bias = []
		bias1 = []
		bias2 = []
		bias3 = []
	
		fil1 = open("SnakeV2_Bias1.txt")
		for n in fil1:
			bias1.append(n.strip().split(","))
		for n in range(0, len(bias1)):
			for x in range(0, len(bias1[n])):
				bias1[n][x] = float(bias1[n][x])
		fil1.close()
	
		fil2 = open("SnakeV2_Bias2.txt")
		for n in fil2:
			bias2.append(n.strip().split(","))
		for n in range(0, len(bias2)):
			for x in range(0, len(bias2[n])):
				bias2[n][x] = float(bias2[n][x])
		fil2.close()

		fil3 = open("SnakeV2_Bias3.txt")
		for n in fil3:
			bias3.append(n.strip().split(","))
		for n in range(0, len(bias3)):
			for x in range(0, len(bias3[n])):
				bias3[n][x] = float(bias3[n][x])
		fil3.close()
	
		for i in range(0, len(bias1)):
			breed = random.randint(1,3)
			if breed == 1:
				bias.append(bias1[i])
			elif breed == 2:
				bias.append(bias2[i])
			else:
				bias.append(bias3[i])
	
		for n in range(0, len(bias)):
			for i in range(0, len(bias[n])):
				if i == 8:
					if random.randint(1, 10) == 1:
						print("endret")
						bias[n][i] = random.randint(-10, 10)
				elif random.randint(1, 10) == 1:
					bias[n][i] = round(random.uniform(-3, 3), 2)


		return bias

	def FilBias1(self):
		bias1 = []

		fil1 = open("SnakeV2_Bias" + str(self._tilfeldig) + ".txt")
		for n in fil1:
			bias1.append(n.strip().split(","))
		
		for n in range(0, len(bias1)):
			for x in range(0, len(bias1[n])):
				bias1[n][x] = float(bias1[n][x])
		fil1.close()

		return bias1


	def indexRetning(self, index):
		if index == 0:
			return "hoyre"
		elif index == 1:
			return "venstre"
		elif index == 2:
			return "opp"
		elif index == 3:
			return "ned"
		else:
			print("hææææ")
			print(index)
			return "hoyre"

	def styre(self, inputs):
		retningIndex = 0
		out = []

		for p in range(0, len(self._bias)):
			tot = 0
			for i in range(0, len(inputs)):
				tot += inputs[i] * self._bias[p][i]
			tot += self._bias[p][-1]
			out.append(max([0, tot]))

		retningIndex = out.index(max(out))

		return self.indexRetning(retningIndex)

		"""retningIndex = 0
		hidden1Out = []
		hidden2Out = []
		out = []

		for p in range(0, 20):
			tot = 0
			for i in range(0, len(inputs)):
				tot += inputs[i] * self._bias[p][i]
			tot += self._bias[p][-1]
			if tot < 0:
				hidden1Out.append(0)
			elif tot >= 0:
				hidden1Out.append(1)


		for p in range(20, 28):
			tot = 0
			for i in range(0, len(inputs)):
				tot += hidden1Out[i] * self._bias[p][i]
			tot += self._bias[p][-1]
			if tot < 0:
				hidden2Out.append(0)
			elif tot >= 0:
				hidden2Out.append(1)



		for p in range(28, 32):
			tot = 0
			for i in range(0, len(hidden2Out)):
				tot += hidden2Out[i] * self._bias[p][i]
			tot += self._bias[p][-1]

			if tot < 0:
				out.append(0)
			elif tot >= 0:
				out.append(1)

		if not 1 in out:
			retningIndex = random.randint(0, 3)
			self._ingen = True
			print("ingen")
		else:
			self._ingen = False
			retningIndex = out.index(1)

		return self.indexRetning(retningIndex)"""

	def getBias(self):
		return self._bias

	def getIngen(self):
		return self._ingen














